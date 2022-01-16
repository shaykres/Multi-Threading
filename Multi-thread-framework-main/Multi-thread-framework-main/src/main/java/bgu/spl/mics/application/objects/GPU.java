package bgu.spl.mics.application.objects;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {


    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private int tickToProcess;
    private Queue<DataBatch> trainingDataBatches;
    private Map<DataBatch, Integer> trainingStartTime;
    private int MaxProcessBatches;
    private int time;
    private int numOfBatches;
    private int UnitTimeUsed;


    public GPU(String type) {
        if (type.equals("RTX3090")) {
            this.type = GPU.Type.RTX3090;
            tickToProcess = 1;
            MaxProcessBatches = 32;
        }

        if (type.equals("RTX2080")) {
            this.type = GPU.Type.RTX2080;
            tickToProcess = 2;
            MaxProcessBatches = 16;
        }

        if (type.equals("GTX1080")) {
            this.type = GPU.Type.GTX1080;
            tickToProcess = 4;
            MaxProcessBatches = 8;
        }
        this.model = null;
        cluster = cluster.getInstance();
        cluster.addGPU(this);
        cluster.getBatchesToGPU().put(this,new LinkedList<>());
        trainingDataBatches = new LinkedList<DataBatch>();
        this.UnitTimeUsed = 0;
        trainingStartTime= new ConcurrentHashMap<DataBatch, Integer>();
    }

    public int getUnitTimeUsed() {
        return UnitTimeUsed;
    }

    public void finishWithModel(){
        this.model=null;
    }

    public void SetTime(int time) {
        this.time = time;
        Train();
    }

    //@PRE:this.model==null
    //@POST:this.model!=null
    public void SetModel(Model m) {
        //System.out.println("very important!!"+this+" changes his model to "+m.getName());
        this.model = m;
        m.SetStatus();
    }

    //@PRE:this.model==null
    //@POST:this.model!=null
    public void SetModelToTest(Model m) {
        this.model = m;
    }

    //@PRE:none
    //@POST:
    // len(getBatches())==@pre(getData().size()/1000)
    public DataBatch[] prepareBatches() {
        int size = model.getData().getSize();
        numOfBatches = size / 1000;
        //System.out.println(numOfBatches);
        if (size % 1000 != 0)
            numOfBatches++;
        DataBatch[] dataBatches = new DataBatch[numOfBatches];
        for (int i = 0; i < numOfBatches; i++) {
            dataBatches[i] = new DataBatch(model.getData(), i * 1000, this);
        }
        return dataBatches;
    }

    public void sendBatches() {
        //System.out.println("Batches of "+this+" sent to cluster");
        DataBatch[] dataBatches = prepareBatches();
        cluster.reciveBatches(dataBatches,type);
    }

    public void trainDataBatch(DataBatch dataBatch) {
        synchronized (trainingDataBatches) {
            if (dataBatch != null)
                this.trainingDataBatches.add(dataBatch);
            trainingDataBatches.notifyAll();
        }
    }

    public void Train() {
        //System.out.println(trainingDataBatches.isEmpty());
        synchronized (trainingDataBatches) {
            if (!trainingDataBatches.isEmpty()) {
                UnitTimeUsed += 1;
                if (!trainingStartTime.containsKey(trainingDataBatches.peek())) {
                    trainingStartTime.put(trainingDataBatches.peek(), time);
                }
                if (time - trainingStartTime.get(trainingDataBatches.peek()) == tickToProcess) {
                    model.getData().UpdateProcessed();
                    trainingStartTime.remove(trainingDataBatches.poll());
                    if (!trainingDataBatches.isEmpty()) {
                        trainingStartTime.put(trainingDataBatches.peek(), time);
                    }
                }
            }
          trainingDataBatches.notifyAll();
        }
    }

    public boolean isDone() {
        return model.processData();
    }

    public Model getDoneModel() {
        if (isDone()) {
            model.SetStatus();
            cluster.FinishWithModel(model);
            return model;
        }
        return null;
    }

    public boolean IsFull() {
        return trainingDataBatches.size()-1 >= MaxProcessBatches;
    }

    public Model TestModel() {
        Student s = model.getStudent();
        boolean val = false;
        double rnd = Math.random();
        if (s.getDegree().equals("PhD")) {
            if (rnd < 0.8)
                val = true;
        } else if (rnd < 0.6)
            val = true;

        model.SetResult(val);
        model.SetStatus();
        return model;
    }
    public int TrainingModels(){
        return trainingDataBatches.size();
    }
    public Model getModel(){
        return model;
    }

    public Type getType(){
        return type;
    }


}
