package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;


public class PublishResultsEvent implements Event<Model> {
    private Model modeltoPublish;

    public PublishResultsEvent(Model m){
        modeltoPublish=m;
    }

    public Model getModeltoPublish(){
        return modeltoPublish;
    }
}
