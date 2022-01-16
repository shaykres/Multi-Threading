package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Queue<DataBatch> data;
    private Cluster cluster;
    private Queue<Integer> time;
    private int UnitTimeUsed;
    private Boolean tookData;

    public CPU(int cores){
        this.cores=cores;
        data=new LinkedList<DataBatch>();
        cluster= Cluster.getInstance();
        cluster.addCPU(this);
        UnitTimeUsed=0;
        time=new LinkedList<>();
        tookData=false;
    }
    public boolean isEmpty(){
        return data.isEmpty();
    }

    public Queue<DataBatch> getData(){
        return data;
    }


    public void AddDataBatch(DataBatch dataBatch){
          if (dataBatch != null) {
            //  System.out.println(this+" took databatch "+dataBatch+"  index "+dataBatch.getStart_index()+" of "+dataBatch.getGPU());
              data.add(dataBatch);
              tookData=true;

          }
    }

    public boolean hasData(){
        return tookData;
    }



    public void SetTime(int time){
        if(!data.isEmpty()) {
            this.time.add(time);
        }
        else {
             cluster.getDataBatch(this);
        }
    }


    public DataBatch process(){
        if(!data.isEmpty()) {
            DataBatch dataBatch = data.peek();

            if (dataBatch.getDataType().equals("Images") )
                return processImage();
            if (dataBatch.getDataType().equals("Tabular"))
                return processTabular();
            if (dataBatch.getDataType().equals("Text"))
                return processText();
        }
        return null;
    }
    public DataBatch processImage(){
        int timeToWait=(32/cores)*4;
        if (time.size()-1!=timeToWait){
            return null;
        }
        UnitTimeUsed+=time.size();
        time.clear();
        return data.poll();
    }
    public DataBatch processText(){
        int timeToWait=(32/cores)*2;
        if (time.size()-1!=timeToWait){
            return null;
        }
        UnitTimeUsed+=time.size();
        time.clear();
        return  data.poll();
    }
    public DataBatch processTabular(){
        int timeToWait=(32/cores)*1;
        if (time.size()-1!=timeToWait){
            return null;
        }
        UnitTimeUsed+=time.size();
        time.clear();
        return   data.poll();
    }
    public void send(DataBatch data){
        if(data!=null) {
            //System.out.println(this+" finish work on databatch "+data.getStart_index()+" "+data);
            tookData=false;
            cluster.ProcessedDataBatch();
            cluster.readyBatches(data);
        }
    }

    public int getUnitTimeUsed(){
        return UnitTimeUsed;
    }
}
