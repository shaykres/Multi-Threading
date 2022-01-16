package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadCast;
import bgu.spl.mics.application.messages.TickBroadCast;
import bgu.spl.mics.application.messages.TickFinishBroadCast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

/**
 * CPU service is responsible for handling the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private MessageBusImpl messageBus;
    private CPU cpu;

    public CPUService(String name, CPU c) {
        super(name);
        messageBus = MessageBusImpl.getInstance();
        this.cpu = c;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadCast.class, c -> {
           // System.out.println(getName());
            cpu.SetTime(c.GetTime());
            DataBatch processBatches = cpu.process();
            if(processBatches!=null) {
               // System.out.println("cpu sent data to cluster");
                cpu.send(processBatches);
            }
        });
        subscribeBroadcast(TickFinishBroadCast.class, c -> terminate());
    }

}
