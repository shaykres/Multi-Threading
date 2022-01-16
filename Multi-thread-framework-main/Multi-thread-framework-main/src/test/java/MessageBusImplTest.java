package bgu.spl.mics.test.java;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadCast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private MessageBusImpl messageBus;
    private MicroService m;
    private  PublishResultsEvent p;
    private Model model;
    TickBroadCast t;
    @Before
    public void setUp() {
        messageBus=messageBus.getInstance();
        m=new CPUService("c",new CPU(32));
        model=new Model("image","Images",2000,new Student("Simba","Computer Science","MSc"));
        p=new PublishResultsEvent(model);
        t=new TickBroadCast(1);
    }
    @After
    public void tearDown(){
        messageBus.unregister(m);
    }
    @Test
    public void getInstance() {
        Assert.assertNotNull("MessageBus canot be null",messageBus.getInstance());
    }

    @Test
    public void subscribeEvent() {
        messageBus.subscribeEvent(p.getClass(),m);
        Assert.assertTrue(messageBus.Issubcriber(p.getClass(),m));
        Assert.assertFalse(messageBus.Issubcriber(p.getClass(),new CPUService("c",new CPU(10))));
    }

    @Test
    public void subscribeBroadcast() {

        messageBus.subscribeBroadcast(t.getClass(),m);
        Assert.assertTrue(messageBus.Issubcriber(t.getClass(),m));
        Assert.assertFalse(messageBus.Issubcriber(t.getClass(),new CPUService("c",new CPU(10))));
    }

    @Test
    public void complete() {
        messageBus.register(m);
        messageBus.subscribeEvent(p.getClass(),m);
        messageBus.sendEvent(p);
        messageBus.complete(p,model);
        Assert.assertNotNull(messageBus.getEventFuture(p));
    }

    @Test
    public void sendBroadcast() {
        messageBus.register(m);
        messageBus.subscribeBroadcast(t.getClass(),m);
        messageBus.sendBroadcast(t);
        Assert.assertTrue(messageBus.hasMessage(m));
    }

    @Test
    public void sendEvent() {
        messageBus.register(m);
        messageBus.subscribeEvent(p.getClass(),m);
        messageBus.sendEvent(p);
        Assert.assertTrue(messageBus.hasMessage(m));
    }

    @Test
    public void register() {
        messageBus.register(m);
        Assert.assertTrue(messageBus.IsRegister(m));
        Assert.assertFalse(messageBus.IsRegister(new CPUService("c",new CPU(16))));
    }

    @Test
    public void unregister() {
        messageBus.register(m);
        messageBus.unregister(m);
        Assert.assertFalse(messageBus.IsRegister(m));
    }

    @Test
    public void awaitMessage() {
        messageBus.register(m);
        messageBus.subscribeBroadcast(t.getClass(),m);
        messageBus.sendBroadcast(t);
        messageBus.awaitMessage(m);
        messageBus.sendBroadcast(t);
        Assert.assertTrue(messageBus.hasMessage(m));
    }
}