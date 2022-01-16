package bgu.spl.mics.application.objects;


import bgu.spl.mics.application.services.ConferenceService;
import com.google.gson.JsonArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
    private List<GPU> GPUS;
    private List<CPU> CPUS;
    //private Queue<Map<DataBatch[],Integer>> reciveBatches;
    private Map<GPU, Queue<DataBatch>> BatchesToGPU;
    private Map<GPU.Type, List<DataBatch[]>> BatchesToCPU;
    private Map<String, List<String>> Statistics;
    private int dataIndex;
    private static boolean isDone = false;
    private static Cluster instance = null;
    private int ProcessedDataBatch;
    private boolean isCpuTaking;
    private List<Student> students;
    private int Counter1 = 1;
    private int Counter2 = 2;
    private int Counter4 = 4;
    private List<ConfrenceInformation> conferences;
    private Object lock3090 = new Object();
    private Object lock2080 = new Object();
    private Object lock1080 = new Object();

    private Cluster() {
        GPUS = new LinkedList<>();
        CPUS = new LinkedList<>();
        Statistics = new ConcurrentHashMap<>();
        Statistics.put("Models", new LinkedList<String>());
        Statistics.put("cpuTimeUsed", new LinkedList<String>());
        Statistics.put("gpuTimeUsed", new LinkedList<String>());
        Statistics.put("batchesProcessed", new LinkedList<String>());
        BatchesToCPU = new ConcurrentHashMap<>();

        BatchesToCPU.put(GPU.Type.RTX3090, new LinkedList<>());
        BatchesToCPU.put(GPU.Type.RTX2080, new LinkedList<>());
        BatchesToCPU.put(GPU.Type.GTX1080, new LinkedList<>());

        ProcessedDataBatch = 0;
        BatchesToGPU = new ConcurrentHashMap<>();
        students = new LinkedList();
        conferences = new LinkedList<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Cluster getInstance() {

        if (isDone == false) {
            synchronized (Cluster.class) {
                if (isDone == false) {
                    instance = new Cluster();
                    isDone = true;
                }
            }
        }
        return instance;
    }

    public  void AddStudent(Student s) {
        this.students.add(s);
    }

    public  Map<GPU, Queue<DataBatch>> getBatchesToGPU() {
        return BatchesToGPU;
    }

    public  void addGPU(GPU toadd) {
        GPUS.add(toadd);
    }

    public  void addCPU(CPU toadd) {
        CPUS.add(toadd);
    }

    public  void reciveBatches(DataBatch[] dataBatches, GPU.Type type) {
        int index = 0;
        synchronized (BatchesToCPU.get(type)) {
            if (dataBatches[0].getDataType().equals("Images"))
                BatchesToCPU.get(type).add((BatchesToCPU.get(type).size()), dataBatches);
            else if (dataBatches[0].getDataType().equals("Tabular")) {
                for (DataBatch[] d : BatchesToCPU.get(type))
                    if (d[0].getDataType().equals("Tabular"))
                        index++;
                BatchesToCPU.get(type).add(index, dataBatches);
            } else {
                for (DataBatch[] d : BatchesToCPU.get(type)) {
                    if (d[0].getDataType().equals("Tabular"))
                        index++;
                    if (d[0].getDataType().equals("Text"))
                        index++;
                }
                BatchesToCPU.get(type).add(index, dataBatches);
            }
            BatchesToCPU.get(type).notifyAll();
        }
    }

    public  void readyBatches(DataBatch data) {
        //System.out.println("cpu finish and send data");
        BatchesToGPU.get(data.getGPU()).add(data);
        sendBatchtoGPU(data.getGPU());
    }

    public  void sendBatchtoGPU(GPU gpu) {
        if (!gpu.IsFull() && !BatchesToGPU.get(gpu).isEmpty())
            gpu.trainDataBatch(BatchesToGPU.get(gpu).poll());
    }

    public  void FinishWithModel(Model m) {
        Statistics.get("Models").add(m.getName());
    }

    public  void ProcessedDataBatch() {
        ProcessedDataBatch++;
    }

    public void PrepareStatistics() throws IOException {
        Statistics.get("batchesProcessed").add(String.valueOf(ProcessedDataBatch));
        int timegpu = 0;
        for (GPU g : GPUS) {
            //System.out.println(g +" "+g.getType()+ " " + g.getUnitTimeUsed());
            timegpu += g.getUnitTimeUsed();
        }
        Statistics.get("gpuTimeUsed").add(String.valueOf(timegpu));
        int timecpu = 0;
        for (CPU c : CPUS) {
            timecpu += c.getUnitTimeUsed();
        }
        Statistics.get("cpuTimeUsed").add(String.valueOf(timecpu));
        CreateTextFile createTextFile = new CreateTextFile(students, Statistics, conferences);

    }

    public  boolean getDataBatch(CPU cpu) {
        if (hasData()) {
            if ((Counter4 <= 0 || !GpuTypeHasData(GPU.Type.RTX3090)) & (Counter2 <= 0 || !GpuTypeHasData(GPU.Type.RTX2080)) & (Counter1 <= 0 || !GpuTypeHasData(GPU.Type.GTX1080))) {
                Counter4 = 4;
                Counter2 = 2;
                Counter1 = 1;
            }


                synchronized (BatchesToCPU.get(GPU.Type.RTX3090)) {
                    if (Counter4 > 0 && GpuTypeHasData(GPU.Type.RTX3090)) {
                    Counter4--;
                    DataBatch[] d = BatchesToCPU.get(GPU.Type.RTX3090).get(0);
                    if (!d[0].getData().finishSendToCpuProcess()) {
                        cpu.AddDataBatch(d[d[0].getData().getCpuSend() / 1000]);
                        d[0].getData().UpdateCpuProcess();
                        //BatchesToCPU.get(GPU.Type.RTX3090).notifyAll();
                        return true;
                    } else {
                        BatchesToCPU.get(GPU.Type.RTX3090).remove(0);
                    }
                    BatchesToCPU.get(GPU.Type.RTX3090).notifyAll();
                }
            }


                synchronized (BatchesToCPU.get(GPU.Type.RTX2080)) {
                    if (Counter2 > 0 && GpuTypeHasData(GPU.Type.RTX2080)) {
                    Counter2--;
                    DataBatch[] d = BatchesToCPU.get(GPU.Type.RTX2080).get(0);
                    if (!d[0].getData().finishSendToCpuProcess()) {
                        cpu.AddDataBatch(d[d[0].getData().getCpuSend() / 1000]);
                        d[0].getData().UpdateCpuProcess();
                       // BatchesToCPU.get(GPU.Type.RTX2080).notifyAll();
                        return true;
                    } else
                        BatchesToCPU.get(GPU.Type.RTX2080).remove(0);
                    BatchesToCPU.get(GPU.Type.RTX2080).notifyAll();
                }
            }


                synchronized (BatchesToCPU.get(GPU.Type.GTX1080)) {
                    if (Counter1 > 0 && GpuTypeHasData(GPU.Type.GTX1080)) {
                    Counter1--;
                    DataBatch[] d = BatchesToCPU.get(GPU.Type.GTX1080).get(0);
                    if (!d[0].getData().finishSendToCpuProcess()) {
                        cpu.AddDataBatch(d[d[0].getData().getCpuSend() / 1000]);
                        d[0].getData().UpdateCpuProcess();
                        return true;
                    } else BatchesToCPU.get(GPU.Type.GTX1080).remove(0);
                    BatchesToCPU.get(GPU.Type.GTX1080).notifyAll();
                }
            }
        }
        return false;
    }

    private boolean GpuTypeHasData(GPU.Type type) {
        return !(BatchesToCPU.get(type).isEmpty());
    }

    private boolean hasData() {
        boolean val = false;
        for (GPU.Type type : BatchesToCPU.keySet()) {
            val = val || GpuTypeHasData(type);
        }
        return val;
    }

    public void AddConference(ConfrenceInformation confrenceInformation) {
        conferences.add(confrenceInformation);
    }


}
