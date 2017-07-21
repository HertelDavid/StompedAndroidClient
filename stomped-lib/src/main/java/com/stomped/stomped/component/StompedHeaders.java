package com.stomped.stomped.component;

import java.util.Map;
import java.util.TreeMap;

public class StompedHeaders {

    public final static String TAG = "StompedHeaders";
    public final static String STOMP_HEADER_DESTINATION = "destination";
    public final static String STOMP_HEADER_CONTENT_TYPE = "content-type";
    public final static String STOMP_HEADER_SUB_ID = "id";
    public final static String STOMP_HEADER_ACK = "ack";
    public final static String STOMP_HEADER_HEARTBEAT = "heart-beat";
    public final static String STOMP_HEADER_ACCEPT_VERSION= "accept-version";
    public final static String STOMP_HEADER_HOST = "host";
    public final static String STOMP_HEADER_RECEIPT = "receipt";

    private TreeMap<String, String> stompedHeaders;

    private static final String HEADER_SEPARATOR = ":";

    public StompedHeaders(){
        this.stompedHeaders = new TreeMap<>();
    }

    public StompedHeaders(TreeMap<String, String> stompedHeaders){
        this.stompedHeaders = stompedHeaders;
    }

    public void addHeader(String key, String value){
        stompedHeaders.put(key, value);
    }

    public TreeMap<String, String> getStompedHeaders(){
        return stompedHeaders;
    }

    public String getStringFormat(){

        //Returns a string format of the Stomp Headers.

        StringBuilder builder = new StringBuilder();

        for(Map.Entry<String, String> entry: stompedHeaders.entrySet()){

            builder.append(entry.getKey());
            builder.append(HEADER_SEPARATOR);
            builder.append(entry.getValue());
            builder.append("\n");
        }

        return builder.toString();
    }

    public String getHeaderValueFromKey(String key){
        return stompedHeaders.get(key);
    }

    public boolean hasHeader(String header){
        return (stompedHeaders.get(header) != null);
    }
}
