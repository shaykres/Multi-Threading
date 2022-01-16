package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {


    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;

    private Integer processed;
    private int size;
    private Integer cpuProcessed;
    public Data(String type, int size){
        switch (type){
            case "Images": this.type = Type.Images;
            break;
            case "Tabular": this.type=Type.Tabular;
            break;
            case "Text":  this.type=Type.Text;
        }
        this.processed=0;
        this.size=size;
        this.cpuProcessed=0;
    }

    public Data(String type,int size,boolean val){
        switch (type){
            case "Images": this.type = Type.Images;
                break;
            case "Tabular": this.type=Type.Tabular;
                break;
            case "Text":  this.type=Type.Text;
        }
        this.size=size;
    }
    public int GetSize(){
        return size;
    }

    public String toString(){
        return
            "type :" +type.toString()+","+"/n"+ "size :" +size;
    }
    public int getSize(){
        return size;
    }

    public String getType(){
        if(this.type == Type.Images)
            return "Images";
        if(this.type==Type.Tabular)
           return "Tabular";
        if(this.type==Type.Text)
            return  "Text";
        return null;
    }

    public void UpdateProcessed(){
//        if(processed>=size)
//            System.out.println("to muchhhhhhhhh bigggeerrrrrrrr");
//        else
            this.processed+=1000;
    }

    public void UpdateCpuProcess(){
        this.cpuProcessed+=1000;
    }

    public boolean finishProcessed(){
        return size<=processed;
    }

    public int getProcessed(){
        return processed;
    }


    public int getCpuSend(){
        return cpuProcessed;
    }

    public boolean finishSendToCpuProcess(){
        return size<=cpuProcessed;
    }

    public Data reset() {
        Data data=new Data(this.getType(),this.size,true);
        data.processed= null;
        data.cpuProcessed=null;
        return data;
       // processed= Integer.parseInt(null);
       // cpuProcessed=Integer.parseInt(null);
    }

}
