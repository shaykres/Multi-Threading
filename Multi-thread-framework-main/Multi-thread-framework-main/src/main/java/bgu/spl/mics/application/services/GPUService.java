package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * GPU service is responsible for handling the
 * {@link //TrainModelEvent} and {@link //TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private MessageBusImpl messageBus;
    private GPU gpu;
    private boolean isTraining = false;
    private boolean isTested = false;
    private Queue<TrainModelEvent> modelEvents;
    private Queue<TestModelEvent> testModelEvent;
   // private TestModelEvent testModelEvent;


    public GPUService(String name, GPU g) {
        super(name);
        messageBus = MessageBusImpl.getInstance();
        this.gpu = g;
        modelEvents = new LinkedList<>();
        testModelEvent=new LinkedList<>();
    }

    @Override
    protected void initialize() {
        subscribeEvent(TestModelEvent.class, c -> {
            testModelEvent.add(c);
            //isTested=true;

        });
        subscribeBroadcast(TickBroadCast.class, c -> {
            //System.out.println(this.gpu+" got tick "+c.GetTime());
            gpu.SetTime(c.GetTime());
            if(!testModelEvent.isEmpty()&!isTraining){
                //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                if(!isTested) {
                    gpu.SetModelToTest(testModelEvent.peek().getModeltoTest());
                    isTested = true;
                    gpu.TestModel();
                }
                //System.out.println("????????????????????????????????????????????????????"+gpu.getModel().translateStatus());
                if(isTested&gpu.getModel().translateStatus().equals("Tested")){
                   // System.out.println("insideeeeeee");
                    complete(testModelEvent.poll(),gpu.getModel());
                    //System.out.println("out of insideeee");
                    isTested=false;
                    //gpu.finishWithModel();
               }
            }
            if(!modelEvents.isEmpty()&&!isTested) {
                if (!isTraining) {
                   // System.out.println(this.getName()+" start to work on "+modelEvents.peek().getModeltotrain().getName());
                    gpu.SetModel(modelEvents.peek().getModeltotrain());
                    gpu.sendBatches();
                    isTraining = true;
                }
                Cluster.getInstance().sendBatchtoGPU(gpu);
                Model m = gpu.getDoneModel();
                if (m != null) {
                   // System.out.println(this.getName()+" finish to work on "+modelEvents.peek().getModeltotrain().getStudent().getName());
                    complete(modelEvents.poll(), m);
                    isTraining = false;
                    gpu.finishWithModel();

                }
            }

        });
        subscribeEvent(TrainModelEvent.class, c -> {
            modelEvents.add(c);
        });
        subscribeBroadcast(TickFinishBroadCast.class, c -> terminate());
    }

}

