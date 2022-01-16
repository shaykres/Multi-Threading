package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private boolean status;
    private GPU gpu;

    public DataBatch(Data data,int start_index,GPU gpu){
        this.data=data;
        this.start_index=start_index;
        this.status=false;
        this.gpu=gpu;
    }
    public GPU getGPU(){
        return gpu;
    }

    public Boolean getStatus(){
        return status;
    }
    public Data getData(){
        return data;
    }

    public String getDataType(){
        return data.getType();
    }

    public int getStart_index(){return start_index;}

    public void SetStatus(Boolean val){
        status=val;
    }

}
