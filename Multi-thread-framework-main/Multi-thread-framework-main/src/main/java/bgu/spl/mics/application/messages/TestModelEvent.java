package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<Model> {
    private Model modeltoTest;

    public TestModelEvent(Model m){
        this.modeltoTest=m;
    }

    public Model getModeltoTest(){
        return modeltoTest;
    }


}
