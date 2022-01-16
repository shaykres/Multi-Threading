package bgu.spl.mics;

import bgu.spl.mics.application.objects.Cluster;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private Map<MicroService, Queue<Message>> services;
    private Map<Class<? extends Message>, Queue<MicroService>> subscribes;
    private Map<Event, Future> FutureEvent;
    private static boolean isDone = false;
    private static MessageBusImpl instance = null;
    private Object lockServices = new Object();
    private Object lockSendEvent = new Object();
    private Object lockSendBroad = new Object();
    private Object lockSubscribe = new Object();

    private MessageBusImpl() {
        services = new ConcurrentHashMap<MicroService, Queue<Message>>();
        subscribes = new ConcurrentHashMap<Class<? extends Message>, Queue<MicroService>>();
        FutureEvent = new ConcurrentHashMap<Event, Future>();
    }

    public static MessageBusImpl getInstance() {
        if (isDone == false) {
            synchronized (MessageBusImpl.class) {
                if (isDone == false) {
                    instance = new MessageBusImpl();
                    isDone = true;
                }
            }
        }
        return instance;
    }

	public Future getEventFuture(Event e){
        if(e!=null) {
            return FutureEvent.get(e);
        }
        return null;
	}

	public boolean IsRegister(MicroService m){
        return services.containsKey(m);
    }

	public boolean Issubcriber(Class<? extends Message> type, MicroService m){
        if(subscribes.containsKey(type))
            return subscribes.get(type).contains(m);
        return false;
    }

    public boolean hasMessage(MicroService m){
        //System.out.println(services.get(m));
        return !services.get(m).isEmpty();
    }

    //@PRE:none
    //@POST:Subscribes.get(type).contains(m)==true
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (subscribes) {
            if (!subscribes.containsKey(type)) {
                subscribes.put(type, new LinkedList<MicroService>());
                subscribes.get(type).add(m);
            } else
                subscribes.get(type).add(m);
            subscribes.notifyAll();
        }
    }

    //@PRE:none
    //@POST:Subscribes.get(type).contains(m)==true
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (subscribes) {
            if (!subscribes.containsKey(type)) {
                Queue<MicroService> microService = new LinkedList<MicroService>();
                subscribes.put(type, microService);
                subscribes.get(type).add(m);
            } else if (!subscribes.get(type).contains(m))
                subscribes.get(type).add(m);
            subscribes.notifyAll();
        }

    }


    @Override
    public <T> void complete(Event<T> e, T result) {
        if (FutureEvent.containsKey(e)) {
            FutureEvent.get(e).resolve(result);
        }

    }

    //@PRE:none
    //@POST:
    // foreach (m in BroadCastsubscribes.get(b)){
    //     services.get(m).contains(b)==true
    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (subscribes) {
            synchronized (services) {
                if (subscribes.containsKey(b.getClass()) && !subscribes.get(b.getClass()).isEmpty()) {
                    for (MicroService m : subscribes.get(b.getClass())) {
                        services.get(m).add(b);
                    }
                }
                subscribes.notifyAll();
            }
        }
        synchronized (lockServices){
            lockServices.notifyAll();
        }
    }

    //@PRE:none
    //@POST:
    // foreach (m in Eventsubscribes.get(e)){
    //     services.get(m).contains(b)==true - one of quenes should return true
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> f;
        synchronized (subscribes) {
            synchronized (services) {
                //System.out.println(e+" "+subscribes.containsKey(e.getClass())+" "+subscribes.get(e.getClass()).isEmpty());
                if (subscribes.containsKey(e.getClass()) && !subscribes.get(e.getClass()).isEmpty()) {
                    // System.out.println(e+" "+services.get(subscribes.get(e.getClass()).peek()).isEmpty());
                    MicroService firstinQueue = subscribes.get(e.getClass()).poll();
                    services.get(firstinQueue).add(e);
                    subscribes.get(e.getClass()).add(firstinQueue);
                    f = new Future<T>();
                    FutureEvent.put(e, f);
                } else {
                    return null;
                }
                services.notifyAll();
            }
            subscribes.notifyAll();
            synchronized (lockServices) {
                lockServices.notifyAll();
            }
            return f;
        }
    }

    //@PRE:none
    //@POST:
    //  getservices().get(m)!=null
    @Override
    public void register(MicroService m) {
        if (services.get(m) == null) {
            //System.out.println(m.getName()+" register");
            Queue<Message> queue = new LinkedList<>();
            services.put(m, queue);
         //   System.out.println(m.getName()+" "+services.get(m));
        }
    }

    //@PRE:none
    //@POST:
    //  getservices().get(m)==null
    @Override
    public void unregister(MicroService m) {
        if (services.get(m) != null) {
            services.remove(m);
        }
       for(Queue q: subscribes.values()){
           if(q.contains(m))
               q.remove(m);
        }
       m.terminate();
    }

    //@PRE:
    //   services.get(m)!=null
    //@POST:
    //  services.get(m).isEmpty()==true
    @Override
    public Message awaitMessage(MicroService m)  {
        Queue<Message> q = services.get(m);
        try{
        synchronized (lockServices) {
            while (q.isEmpty()) {
                lockServices.wait();
            }
        }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return q.poll();
    }


}
