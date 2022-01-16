package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadCast implements Broadcast {
    private int time;

    public TickBroadCast(int time){
        this.time=time;
    }

    public int GetTime(){
        return time;
    }
}
