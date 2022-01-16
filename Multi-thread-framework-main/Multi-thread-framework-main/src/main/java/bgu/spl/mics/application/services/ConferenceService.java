package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link //PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation confrenceInformation;
    private MessageBusImpl messageBus;
    private int time;

    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        messageBus = MessageBusImpl.getInstance();
        this.confrenceInformation = confrenceInformation;
    }

    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class, c -> {
            confrenceInformation.AddModelToPublish(c.getModeltoPublish());
            complete(c, c.getModeltoPublish());
        });
        subscribeBroadcast(TickBroadCast.class, c -> {
                    time = c.GetTime();
                    if (time == confrenceInformation.getDate()) {
                        PublishConferenceBroadCast p = new PublishConferenceBroadCast(confrenceInformation.getPublications());
                        messageBus.sendBroadcast(p);
                        Cluster.getInstance().AddConference(confrenceInformation);
                        messageBus.unregister(this);
                    }
                }
        );
        subscribeBroadcast(TickFinishBroadCast.class, c -> terminate());
    }
}
