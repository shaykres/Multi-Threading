package bgu.spl.mics.test.java;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class CPUTest {
    private CPU cpu32;
    private CPU cpu16;
    private DataBatch image;
    private DataBatch text;
    private DataBatch tabular;

    @Before
    public void setUp() {
        cpu16=new CPU(16);
        cpu32=new CPU(32);
        image=new DataBatch(new Data("Images",1000),0,new GPU("RTX3090"));
        text=new DataBatch(new Data("Text",1000),0,new GPU("RTX3090"));
        tabular=new DataBatch(new Data("Tabular",1000),0,new GPU("RTX3090"));
    }
    @Test
    public void addDataBatch() {
        cpu16.AddDataBatch(null);
        cpu32.AddDataBatch(null);
        Assert.assertEquals(0,cpu16.getData().size());
        Assert.assertEquals(0,cpu32.getData().size());
        cpu16.AddDataBatch(image);
        cpu32.AddDataBatch(image);
        Assert.assertEquals(1,cpu16.getData().size());
        Assert.assertEquals(1,cpu32.getData().size());
        cpu16.AddDataBatch(text);
        cpu32.AddDataBatch(text);
        Assert.assertEquals(2,cpu16.getData().size());
        Assert.assertEquals(2,cpu32.getData().size());
        cpu16.AddDataBatch(tabular);
        cpu32.AddDataBatch(tabular);
        Assert.assertEquals(3,cpu16.getData().size());
        Assert.assertEquals(3,cpu32.getData().size());
    }
    @Test
    public void  processImage(){
        cpu16.AddDataBatch(image);
        cpu32.AddDataBatch(image);
        cpu16.SetTime(1);
        cpu32.SetTime(1);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(2);
        cpu32.SetTime(2);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(3);
        cpu32.SetTime(3);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(4);
        cpu32.SetTime(4);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(5);
        cpu32.SetTime(5);
        Assert.assertNull(cpu16.process());
        Assert.assertNotNull(cpu32.process());
        Assert.assertEquals(0,cpu32.getData().size());
        cpu16.SetTime(6);
        Assert.assertNull(cpu16.process());
        cpu16.SetTime(7);
        Assert.assertNull(cpu16.process());
        cpu16.SetTime(8);
        Assert.assertNull(cpu16.process());
        cpu16.SetTime(9);
        Assert.assertNotNull(cpu16.process());
        Assert.assertEquals(0,cpu16.getData().size());
    }
    @Test
    public void processText(){
        cpu16.AddDataBatch(text);
        cpu32.AddDataBatch(text);
        cpu16.SetTime(1);
        cpu32.SetTime(1);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(2);
        cpu32.SetTime(2);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(3);
        cpu32.SetTime(3);
        Assert.assertNull(cpu16.process());
        Assert.assertNotNull(cpu32.process());
        Assert.assertEquals(0,cpu32.getData().size());
        cpu16.SetTime(4);
        Assert.assertNull(cpu16.process());
        cpu16.SetTime(5);
        Assert.assertNotNull(cpu16.process());
        Assert.assertEquals(0,cpu16.getData().size());
    }
    @Test
    public void processTabular(){
        cpu16.AddDataBatch(tabular);
        cpu32.AddDataBatch(tabular);
        cpu16.SetTime(1);
        cpu32.SetTime(1);
        Assert.assertNull(cpu16.process());
        Assert.assertNull(cpu32.process());
        cpu16.SetTime(2);
        cpu32.SetTime(2);
        Assert.assertNull(cpu16.process());
        Assert.assertNotNull(cpu32.process());
        Assert.assertEquals(0,cpu32.getData().size());
        cpu16.SetTime(3);
        Assert.assertNotNull(cpu16.process());
        Assert.assertEquals(0,cpu16.getData().size());
    }
}