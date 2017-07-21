package com.stomped.stomped.listener;

import com.stomped.stomped.component.StompedFrame;

public abstract class StompedListener {

    private final static String TAG = "StompedListener";
    private String destination;
    private String destinationID;

    public abstract void onNotify(final StompedFrame frame);

    public void setDestination(String destination){
        this.destination = destination;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestinationID(String destinationID){
        this.destinationID = destinationID;
    }

    public String getDestinationID(){
        return destinationID;
    }
}
