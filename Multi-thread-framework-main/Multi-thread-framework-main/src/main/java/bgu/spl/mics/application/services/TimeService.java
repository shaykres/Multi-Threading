package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadCast;
import bgu.spl.mics.application.messages.TickFinishBroadCast;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.CreateTextFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    private MessageBusImpl messageBus;
    private int speed;
    private int duration;
    private int time;
    //private Time t;

    public TimeService(int speed, int duration) {
        super("Time Service");
        this.speed = speed;
        this.duration = duration;
        messageBus = MessageBusImpl.getInstance();
        this.time = 1;
        //t=Time.valueOf("1");
    }

    @Override
    protected void initialize() {
        while (time <= duration) {
            try {
                //System.out.println("the time is:"+time);
                Thread.sleep(speed);
                TickBroadCast tickBroadCast = new TickBroadCast(time);
                messageBus.sendBroadcast(tickBroadCast);
                time++;
            } catch (InterruptedException ignored) {
            }
        }
        try {
            Thread.sleep(speed);
            TickFinishBroadCast tickFinishBroadCast = new TickFinishBroadCast();
            messageBus.sendBroadcast(tickFinishBroadCast);
            Cluster.getInstance().PrepareStatistics();


        } catch (InterruptedException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        terminate();
    }
}

