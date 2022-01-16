package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private List<Model> publications;
    public ConfrenceInformation(String name,int date){
        this.name=name;
        this.date=date;
        publications = new LinkedList<Model>();
    }

    public int getDate(){
        return date;
    }

    public void AddModelToPublish(Model m){
        publications.add(m);
    }

    public List<Model> getPublications(){
        return publications;
    }
}
