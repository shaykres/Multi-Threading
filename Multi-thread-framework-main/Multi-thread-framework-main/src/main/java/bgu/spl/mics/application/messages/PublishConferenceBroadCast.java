package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;

public class PublishConferenceBroadCast implements Broadcast {
    private List<Model> publish;

    public PublishConferenceBroadCast(List<Model> publish){
        this.publish=publish;
    }

    public List<Model> getPublish(){
        return publish;
    }

}
