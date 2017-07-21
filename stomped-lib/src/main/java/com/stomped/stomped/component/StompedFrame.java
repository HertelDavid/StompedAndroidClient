package com.stomped.stomped.component;

public class StompedFrame {

    //TODO Change implementation of body.

    private final static String TAG = "StompedFrame";
    private String command;
    private StompedHeaders headers;
    private String body;
    private String termination = "\u0000";

    private StompedFrame(){}

    public static StompedFrame construct(String command){
        return construct(command, null);
    }

    public static StompedFrame construct(String command, StompedHeaders headers){
        return construct(command, headers, null);
    }

    public static StompedFrame construct(String command, StompedHeaders headers, String body){

        StompedFrame frame = new StompedFrame();
        frame.setCommand(command);
        frame.setStompedHeaders(headers);
        frame.setStompedBody(body);

        return frame;
    }

    public String build(){

        StringBuilder builder = new StringBuilder();
        builder.append(command);
        builder.append("\n");

        if(headers != null){
            builder.append(headers.getStringFormat());
        }

        builder.append("\n");

        if(body != null){
            builder.append(body);
            builder.append("\n\n");
            builder.append(termination);
        }else{
            builder.append(termination);
        }

        return builder.toString();
    }

    public StompedHeaders createHeaders(){
        headers = new StompedHeaders();
        return headers;
    }

    public void addHeader(String key, String value){
        if(headers != null){

            headers.addHeader(key, value);
        }else{

            this.createHeaders().addHeader(key, value);
        }
    }

    public String getHeaderValueFromKey(String key){
        return headers.getHeaderValueFromKey(key);
    }

    private void setCommand(String command) {
        this.command = command;
    }

    private void setStompedHeaders(StompedHeaders headers){
        this.headers = headers;
    }

    private void setStompedBody(String body){
        this.body = body;
    }

    public String getCommand(){
        return this.command;
    }

    public StompedHeaders getStompedHeaders(){
        return this.headers;
    }

    public String getStompedBody(){
        return this.body;
    }
}
