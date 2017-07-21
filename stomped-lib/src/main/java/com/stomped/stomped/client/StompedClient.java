package com.stomped.stomped.client;

import android.util.Log;

import com.stomped.stomped.component.StompedCommand;
import com.stomped.stomped.component.StompedFrame;
import com.stomped.stomped.component.StompedHeaders;
import com.stomped.stomped.connection.WebSocketConnector;
import com.stomped.stomped.listener.StompedListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StompedClient {

    private final static String TAG = StompedClient.class.toString();

    private ExecutorService threadPool;
    private WebSocketConnector connector;
    private int uniqueDestinationID = 0;
    private StompedClientBuilder builder;

    public final static class StompedClientBuilder{

        private int heartBeat = 0;

        public StompedClient build(String url){
            return build(new WebSocketConnector.Builder().setHeartBeat(heartBeat).connect(url));
        }

        public StompedClient build(WebSocketConnector connector){
            return new StompedClient(connector, this);
        }

        public StompedClientBuilder setHeartBeat(int milliseconds){
            this.heartBeat = milliseconds;
            return this;
        }
    }

    private StompedClient(WebSocketConnector connector, StompedClientBuilder builder){
        this.connector = connector;
        this.threadPool = Executors.newSingleThreadExecutor();
        this.builder = builder;
        Log.d(TAG, "StompedClient has been built.");
    }

    public void disconnect(){
        this.connector.disconnect();
        Log.d(TAG, "Client disconnecting");
    }

    public void send(String destination){

        //Using SEND command.
        StompedFrame frame = StompedFrame.construct(StompedCommand.STOMP_COMMAND_SEND);
        frame.addHeader(StompedHeaders.STOMP_HEADER_DESTINATION, destination);

        threadPool.execute(constructOperator(frame));
        Log.d(TAG, "Client sending to " + destination);
    }

    public void send(String destination, String body){

        StompedFrame frame = StompedFrame.construct(StompedCommand.STOMP_COMMAND_SEND, null, body);
        frame.addHeader(StompedHeaders.STOMP_HEADER_DESTINATION, destination);
        frame.addHeader(StompedHeaders.STOMP_HEADER_CONTENT_TYPE, "application/json");

        threadPool.execute(constructOperator(frame));
        Log.d(TAG, "Client sending to " + destination);
    }

    public void subscribeAndSend(String destination, String subscription, @NotNull StompedListener listener){
        subscribe(subscription, listener);
        send(destination);
    }

    public void subscribe(String destination, @NotNull StompedListener listener){

        String destinationID = String.valueOf(incrementDestinationID());

        listener.setDestinationID(destinationID);
        listener.setDestination(destination);
        StompedListenerRouter.getInstance().addListener(listener);

        StompedFrame frame = StompedFrame.construct(StompedCommand.STOMP_COMMAND_SUBSCRIBE);
        frame.addHeader(StompedHeaders.STOMP_HEADER_SUB_ID, destinationID);
        frame.addHeader(StompedHeaders.STOMP_HEADER_DESTINATION, destination);
        frame.addHeader(StompedHeaders.STOMP_HEADER_ACK, "auto");

        threadPool.execute(constructOperator(frame));
        Log.d(TAG, "Client subscribe to " + destination + " with destinationID " + destinationID);
    }

    public void unsubscribe(String destination){

        StompedListener currentListener = StompedListenerRouter.getInstance().removeListener(destination);

        StompedFrame frame = StompedFrame.construct(StompedCommand.STOMP_COMMAND_UNSUBSCRIBE);
        frame.addHeader(StompedHeaders.STOMP_HEADER_SUB_ID, currentListener.getDestinationID());

        threadPool.execute(constructOperator(frame));
        Log.d(TAG, "Client unsubscribed from " + currentListener.getDestination() + " with destinationID " + currentListener.getDestinationID());
    }

    public void setWebSocketConnector(WebSocketConnector connector){
        this.connector = connector;
    }

    private StompedPayloadOperator constructOperator(StompedFrame payload){
        return new StompedPayloadOperator(payload, connector);
    }

    private int incrementDestinationID(){
        return uniqueDestinationID++;
    }
}
