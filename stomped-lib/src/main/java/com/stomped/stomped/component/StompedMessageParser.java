package com.stomped.stomped.component;

public class StompedMessageParser {

    private final static String TAG = "StompedMessageParser";

    private StompedMessageParser(){}

    public static StompedFrame constructFrame(String message){

        int currentPosition = 1;
        String command;
        StompedHeaders headers = new StompedHeaders();
        StringBuilder body = new StringBuilder();

        String[] splitMessage = message.split("\n");

        command = splitMessage[0];

        for(int i = currentPosition; i < splitMessage.length; i++) {
            if (splitMessage[i].equals("")) {
                currentPosition = i;
                break;
            } else {
                String[] header = splitMessage[i].split(":");
                headers.addHeader(header[0], header[1]);
            }
        }

        for(int i = currentPosition; i < splitMessage.length; i++){
            body.append(splitMessage[i]);
        }

        return StompedFrame.construct(command, headers, body.toString());
    }
}
