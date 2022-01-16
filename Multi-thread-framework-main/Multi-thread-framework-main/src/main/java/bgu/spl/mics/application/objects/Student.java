package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.StudentService;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }
    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private Queue<Model> models;
    private Queue<Model> trainedModels;

    public Student(String name, String department, String status){
        this.name=name;
        this.department=department;
        if(status.equals("MSc"))
            this.status=Degree.MSc;
        if(status.equals("PhD"))
            this.status=Degree.PhD;
        this.publications=0;
        this.papersRead=0;
        models=new LinkedList<Model>();
        trainedModels=new LinkedList<>();
    }

    public String getName(){
        return name;
    }

    public void addModel(Model m){
        int toInsert=0;
        for(Model m1 :models) {
            if(m1.getData().getSize()>m.getData().getSize())
                break;
            else
                toInsert++;
        }
        Queue<Model> q=new LinkedList<>();
        while(toInsert>0){
            q.add(models.poll());
            toInsert--;
        }
        q.add(m);
        while (!models.isEmpty()){
            q.add(models.poll());
        }

        while (!q.isEmpty()){
            models.add(q.poll());
        }

    }

    public void RecivednewPublish(){
        papersRead++;
    }

    public void IncreasePublications(){
        publications++;
    }

    public Queue<Model> getModels(){
        return models;
    }
    public Queue<Model> getTestedModels(){
        return trainedModels;
    }

    public String getDegree(){
        if(status==Degree.MSc)
            return "MSc";
       else
           return "PhD";
    }

    public void SetModels(){
       models=null;
    }




}
