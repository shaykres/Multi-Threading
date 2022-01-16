package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CreateTextFile {

    public CreateTextFile(List<Student> students, Map<String, List<String>> Statistics,List<ConfrenceInformation> confrenceInformations) throws IOException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Student[] students1 = new Student[students.size()];
        int i = 0;
        for (Student s1 : students) {
            students1[i] = s1;
            i++;
        }
        for (int j = 0; j < students1.length; j++) {

            for (Model m : students1[j].getTestedModels()) {
                m.ResetStudent();
                m.ResetData();
            }
            students1[j].getModels().clear();
            students1[j].SetModels();
        }
        int cpuTimeUsed=Integer.parseInt(Statistics.get("cpuTimeUsed").get(0));
        int gpuTimeUsed=Integer.parseInt(Statistics.get("gpuTimeUsed").get(0));
        int batchesProcessed=Integer.parseInt(Statistics.get("batchesProcessed").get(0));
        for (ConfrenceInformation c:confrenceInformations){
            for(Model model:c.getPublications()){
                model.ResetData();
            }
        }
        FileWriter fileWriter = new FileWriter("Output.json");
        gson.toJson(new JsonOutput(students1,confrenceInformations,cpuTimeUsed,gpuTimeUsed,batchesProcessed), fileWriter);
        fileWriter.flush();
        fileWriter.close();
        System.out.println("finish");

    }
}

