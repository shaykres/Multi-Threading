package bgu.spl.mics.application.objects;


/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {



    enum Status {
        PreTrained, Training, Trained, Tested
    }

    enum Result {
        None, Good, Bad
    }

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String name, String type, int size, Student student) {
        this.name = name;
        this.data = new Data(type, size);
        this.student = student;
        this.status = Status.PreTrained;
        this.result = Result.None;
        student.addModel(this);
    }

    public String getName(){
        return name;
    }

    public Student getStudent(){
        return student;
    }

    public Data getData() {
        return data;
    }

    public String toString() {
        String mymodel = "";
        mymodel += "name :" + this.name + "\n" +
                "data :{" + data.toString() + "}," + "/n" +
                "status :" + status.toString() + ",/n" +
                "results :" + result.toString();
        return mymodel;
    }

    public String translateResult() {
        if (result == Result.None)
            return "None";
        if (result == Result.Bad)
            return "Bad";
        if (result == Result.Good)
            return "Good";
        return null;
    }

    public String translateStatus() {
        if (status == Status.PreTrained)
            return "PreTrained";
        if (status == Status.Training)
            return "Training";
        if (status == Status.Trained)
            return "Trained";
        if (status == Status.Tested)
            return "Tested";
        return null;
    }

    public void SetStatus() {
        if (status == Status.PreTrained)
            status=Status.Training;
        else if (status == Status.Training)
            status=Status.Trained;
        else if (status == Status.Trained)
            status=Status.Tested;
    }

    public void SetResult(Boolean val) {
       if(val)
           result=Result.Good;
       else
           result=Result.Bad;
    }

    public boolean processData(){
        return data.finishProcessed();
    }

    public void ResetStudent(){
        this.student=null;
    }

    public void ResetData() {
        data= data.reset();

    }

}
