package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.List;
import java.util.PrimitiveIterator;
import java.util.concurrent.TimeUnit;


/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private MessageBusImpl messageBus;
    private Student student;
    private boolean isInTrain = false;
    private boolean isInTest = false;
    private boolean isInPublishResult = false;
    private TrainModelEvent trainModelEvent;
    private TestModelEvent testModelEvent;
    private PublishResultsEvent publishResultsEvent;


    public StudentService(String name, Student s) {
        super(name);
        messageBus = MessageBusImpl.getInstance();
        this.student = s;

    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadCast.class, c -> {
                    List<Model> published = c.getPublish();
           // System.out.println(published.size());
                    for (Model m : published) {
                        //System.out.println(m.getName()+" was published "+student+" "+m.getStudent() );
                        if (m.getStudent() == student) {
                            student.IncreasePublications();
                        } else
                            student.RecivednewPublish();
                    }

                }
        );
        subscribeBroadcast(TickFinishBroadCast.class, c -> terminate());

        subscribeBroadcast(TickBroadCast.class, c -> {
            if(!student.getModels().isEmpty()) {
               // System.out.println(student.getName()+" "+student.getModels().peek().getName()+" the status is: "+student.getModels().peek().translateStatus());
                if (!isInTrain&&student.getModels().peek().translateStatus().equals("PreTrained")) {
                    trainModelEvent = new TrainModelEvent(student.getModels().peek());
                    if(sendEvent(trainModelEvent)!=null)
                        isInTrain=true;
                } else if (!isInTest && (student.getModels().peek().translateStatus().equals("Trained"))) {
                    Future<Model> f = messageBus.getEventFuture(trainModelEvent);
                    Model m = f.get();
                    testModelEvent = new TestModelEvent(m);
                    sendEvent(testModelEvent);
                    isInTrain=false;
                    isInTest = true;
                } else if (student.getModels().peek().translateStatus().equals("Tested")) {
                    if (!isInPublishResult) {
                        Future<Model> f = messageBus.getEventFuture(testModelEvent);
                        Model m = f.get();
                        isInTest = false;
                        //System.out.println(m.getName()+" !!!!!!!!!!!!"+m.translateResult()+"!!!!!!!!!!!!!");
                        if (m.translateResult().equals("Good")) {
                            isInPublishResult = true;
                            publishResultsEvent = new PublishResultsEvent(m);
                            sendEvent(publishResultsEvent);
                        }
                    }
                    if (isInPublishResult && messageBus.getEventFuture(publishResultsEvent).isDone()) {
                        isInPublishResult = false;
                    }
                    if (!isInPublishResult) {
                        student.getTestedModels().add(student.getModels().poll());
                    }
                }
            }
        });


    }
}