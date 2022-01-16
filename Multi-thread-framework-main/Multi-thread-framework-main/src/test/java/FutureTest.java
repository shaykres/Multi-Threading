package bgu.spl.mics.test.java;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

public class FutureTest {
    private Future f;
    private TrainModelEvent t;

    @Before
    public void setUp() {
        f=new Future();
        t=new TrainModelEvent(new Model("image","Images",2000,new Student("Simba","Computer Science","MSc")));
    }

    @Test
    public void get() {
        f.resolve(t);
       assertNotNull(f.get());
    }

    @Test
    public void isDone() {
        Assert.assertFalse("We did not start", f.isDone());
        f.resolve(5);
        Assert.assertTrue("resolve has a value", f.isDone());
    }

    @Test
    public void testGet() {
        TimeUnit time = TimeUnit.valueOf("MINUTES");
        Assert.assertNull("time out",f.get(0,time));
        f.resolve(t);
        assertNotNull(f.get());
    }
}