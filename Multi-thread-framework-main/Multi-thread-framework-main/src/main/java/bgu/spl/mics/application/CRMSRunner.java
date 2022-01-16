package bgu.spl.mics.application;

//import jdk.nashorn.internal.parser.JSONParser;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Iterator;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.FileReader;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Gson g = new Gson();
        JsonObject json = g.fromJson(new FileReader(args[0]), JsonObject.class);
        JsonArray students=(JsonArray) json.get("Students");
        JsonArray GPUS=(JsonArray) json.get("GPUS");
        JsonArray CPUS=(JsonArray) json.get("CPUS");
        JsonArray Conferences=(JsonArray) json.get("Conferences");
        JsonElement TickTime= json.get("TickTime");
        JsonElement Duration= json.get("Duration");

        for (JsonElement s: students) {
            JsonObject studentObject =s.getAsJsonObject();
            JsonElement name=studentObject.get("name");
            JsonElement department=studentObject.get("department");
            JsonElement status=studentObject.get("status");
            JsonArray models=(JsonArray) studentObject.get("models");
            Student student=new Student(name.getAsString(),department.getAsString(),status.getAsString());
            Cluster.getInstance().AddStudent(student);
            for(JsonElement m:models){
                JsonObject modelObject =m.getAsJsonObject();
                JsonElement modelname=modelObject.get("name");
                JsonElement modeltype=modelObject.get("type");
                JsonElement modelsize=modelObject.get("size");
                Model model=new Model(modelname.getAsString(),modeltype.getAsString(),modelsize.getAsInt(),student);
            }
            StudentService studentService=new StudentService(name.getAsString(),student);
            Thread t=new Thread(studentService);
            t.setName(studentService.getName());
            t.start();
        }
        int indexGPU=1;
        for(JsonElement jsongpu: GPUS){
            GPU gpu=new GPU(jsongpu.getAsString());
            GPUService gpuService=new GPUService("GPU"+indexGPU,gpu);
            indexGPU++;
            Thread t=new Thread(gpuService);
            t.setName(gpuService.getName());
            t.start();

        }

        int indexCPU=1;
        for(JsonElement jsoncpu: CPUS){
            CPU cpu=new CPU(jsoncpu.getAsInt());
            CPUService cpuService=new CPUService("CPU"+indexCPU,cpu);
            indexCPU++;
            Thread t=new Thread(cpuService);
            t.setName(cpuService.getName());
            t.start();
        }
        for(JsonElement C:Conferences){
            JsonObject CObject =C.getAsJsonObject();
            JsonElement cname=CObject.get("name");
            JsonElement cdate=CObject.get("date");
            ConfrenceInformation confrenceInformation=new ConfrenceInformation(cname.getAsString(),cdate.getAsInt());
            ConferenceService conferenceService=new ConferenceService(cname.getAsString(),confrenceInformation);
            Thread t=new Thread(conferenceService);
            t.setName(conferenceService.getName());
            t.start();
        }

        TimeService timeService=new TimeService(TickTime.getAsInt(),Duration.getAsInt());
        Thread t=new Thread(timeService);
        t.setName(timeService.getName());
        Thread.sleep(200);
        t.start();

    }
}
