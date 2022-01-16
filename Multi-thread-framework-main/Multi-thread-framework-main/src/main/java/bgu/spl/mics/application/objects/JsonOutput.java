package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class JsonOutput {
    private Student[] students;
    private List<ConfrenceInformation> confrenceInformations;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private int batchesProcessed;

    public JsonOutput(Student[] students,List<ConfrenceInformation> confrenceInformations,int cpuTimeUsed,int  gpuTimeUsed, int batchesProcessed){
        this.students=students;
        this.confrenceInformations=new LinkedList<>();
        for(ConfrenceInformation c :confrenceInformations){
            this.confrenceInformations.add(c);
        }
        this.cpuTimeUsed=cpuTimeUsed;
        this.gpuTimeUsed=gpuTimeUsed;
        this.batchesProcessed=batchesProcessed;
    }
}
