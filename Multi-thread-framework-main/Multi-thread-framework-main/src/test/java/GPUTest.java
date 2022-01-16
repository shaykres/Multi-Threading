package bgu.spl.mics.test.java;

import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    private GPU gpuRTX3090;
    private GPU gpuRTX2080;
    private GPU gpuGTX1080;
    private Model image;
    private Model text;
    private Model tabular;

    @Before
    public void setUp() {
        gpuRTX3090 = new GPU("RTX3090");
        gpuRTX2080 = new GPU("RTX2080");
        gpuGTX1080 = new GPU("GTX1080");
        image=new Model("image","Images",2000,new Student("Simba","Computer Science","MSc"));
        text=new Model("text","Text",3000,new Student("Simba","Computer Science","MSc"));
        tabular=new Model("tabular","Tabular",50000,new Student("Simba","Computer Science","MSc"));
    }

    @Test
    public void setModel() {
        gpuRTX3090.SetModel(image);
        Assert.assertEquals("Training",image.translateStatus());
        gpuRTX3090.SetModel(image);
        Assert.assertEquals("Trained",image.translateStatus());
        gpuRTX3090.SetModel(image);
        Assert.assertEquals("Tested",image.translateStatus());
        gpuRTX2080.SetModel(text);
        Assert.assertEquals("Training",text.translateStatus());
        gpuRTX2080.SetModel(text);
        Assert.assertEquals("Trained",text.translateStatus());
        gpuRTX2080.SetModel(text);
        Assert.assertEquals("Tested",text.translateStatus());
        gpuGTX1080.SetModel(tabular);
        Assert.assertEquals("Training",tabular.translateStatus());
        gpuGTX1080.SetModel(tabular);
        Assert.assertEquals("Trained",tabular.translateStatus());
        gpuGTX1080.SetModel(tabular);
        Assert.assertEquals("Tested",tabular.translateStatus());
    }

    @Test
    public void prepareBatches() {
        gpuRTX3090.SetModel(image);
        DataBatch[] dataBatch1=gpuRTX3090.prepareBatches();
        Assert.assertEquals(2,dataBatch1.length);
        gpuRTX3090.SetModel(text);
        DataBatch[] dataBatch2=gpuRTX3090.prepareBatches();
        Assert.assertEquals(3,dataBatch2.length);
        gpuRTX3090.SetModel(tabular);
        DataBatch[] dataBatch3=gpuRTX3090.prepareBatches();
        Assert.assertEquals(50,dataBatch3.length);
        gpuRTX2080.SetModel(image);
        DataBatch[] dataBatch4=gpuRTX2080.prepareBatches();
        Assert.assertEquals(2,dataBatch4.length);
        gpuRTX2080.SetModel(text);
        DataBatch[] dataBatch5=gpuRTX2080.prepareBatches();
        Assert.assertEquals(3,dataBatch5.length);
        gpuRTX2080.SetModel(tabular);
        DataBatch[] dataBatch6=gpuRTX2080.prepareBatches();
        Assert.assertEquals(50,dataBatch6.length);
        gpuGTX1080.SetModel(image);
        DataBatch[] dataBatch7=gpuGTX1080.prepareBatches();
        Assert.assertEquals(2,dataBatch7.length);
        gpuGTX1080.SetModel(text);
        DataBatch[] dataBatch8=gpuGTX1080.prepareBatches();
        Assert.assertEquals(3,dataBatch8.length);
        gpuGTX1080.SetModel(tabular);
        DataBatch[] dataBatch9=gpuGTX1080.prepareBatches();
        Assert.assertEquals(50,dataBatch9.length);
    }

    @Test
    public void train() {
        //1080
        gpuGTX1080.SetModel(image);
        DataBatch d1=new DataBatch(image.getData(),0,gpuGTX1080);
        gpuGTX1080.trainDataBatch(d1);
        gpuGTX1080.SetTime(1);
        gpuGTX1080.Train();
        Assert.assertEquals(1,gpuGTX1080.TrainingModels());
        gpuGTX1080.SetTime(2);
        gpuGTX1080.Train();
        Assert.assertEquals(1,gpuGTX1080.TrainingModels());
        gpuGTX1080.SetTime(3);
        gpuGTX1080.Train();
        Assert.assertEquals(1,gpuGTX1080.TrainingModels());
        gpuGTX1080.SetTime(4);
        gpuGTX1080.Train();
        Assert.assertEquals(1,gpuGTX1080.TrainingModels());
        gpuGTX1080.SetTime(5);
        gpuGTX1080.Train();
        Assert.assertEquals(0,gpuGTX1080.TrainingModels());
        //2080
        gpuRTX2080.SetModel(text);
        DataBatch d2=new DataBatch(text.getData(),0,gpuRTX2080);
        gpuRTX2080.trainDataBatch(d2);
        gpuRTX2080.SetTime(1);
        gpuRTX2080.Train();
        Assert.assertEquals(1,gpuRTX2080.TrainingModels());
        gpuRTX2080.SetTime(2);
        gpuRTX2080.Train();
        Assert.assertEquals(1,gpuRTX2080.TrainingModels());
        gpuRTX2080.SetTime(3);
        gpuRTX2080.Train();
        Assert.assertEquals(0,gpuRTX2080.TrainingModels());
        //3090
        gpuRTX3090.SetModel(text);
        DataBatch d3=new DataBatch(text.getData(),0,gpuRTX3090);
        gpuRTX3090.trainDataBatch(d3);
        gpuRTX3090.SetTime(1);
        gpuRTX3090.Train();
        Assert.assertEquals(1,gpuRTX3090.TrainingModels());
        gpuRTX3090.SetTime(2);
        gpuRTX3090.Train();
        Assert.assertEquals(0,gpuRTX3090.TrainingModels());
    }

    @Test
    public void IsFull() {
        //3090
        gpuRTX3090.SetModel(image);
        for(int i=0;i<32;i++){
            DataBatch d1=new DataBatch(image.getData(),0,gpuRTX3090);
            gpuRTX3090.trainDataBatch(d1);
            Assert.assertFalse(gpuRTX3090.IsFull());
        }
        gpuRTX3090.trainDataBatch(new DataBatch(image.getData(),0,gpuRTX3090));
        Assert.assertTrue(gpuRTX3090.IsFull());
        //2080
        gpuRTX2080.SetModel(image);
        for(int i=0;i<16;i++){
            DataBatch d1=new DataBatch(image.getData(),0,gpuRTX2080);
            gpuRTX2080.trainDataBatch(d1);
            Assert.assertFalse(gpuRTX2080.IsFull());
        }
        gpuRTX2080.trainDataBatch(new DataBatch(image.getData(),0,gpuRTX2080));
        Assert.assertTrue(gpuRTX2080.IsFull());
        //1080
        gpuRTX2080.SetModel(image);
        for(int i=0;i<8;i++){
            DataBatch d1=new DataBatch(image.getData(),0,gpuGTX1080);
            gpuGTX1080.trainDataBatch(d1);
            Assert.assertFalse(gpuGTX1080.IsFull());
        }
        gpuGTX1080.trainDataBatch(new DataBatch(image.getData(),0,gpuGTX1080));
        Assert.assertTrue(gpuGTX1080.IsFull());
    }
}