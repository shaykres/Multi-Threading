package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import bgu.spl.mics.application.objects.Model;


public class TrainModelEvent implements Event {
    private Model modeltotrain;
    public TrainModelEvent(Model m){
        modeltotrain=m;
    }
    public Model getModeltotrain(){
        return modeltotrain;
    }

}
