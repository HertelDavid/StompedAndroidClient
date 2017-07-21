package com.stomped.stomped.client;

import com.stomped.stomped.component.StompedFrame;
import com.stomped.stomped.connection.WebSocketConnector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class StompedPayloadOperator implements Runnable{

    private StompedFrame payload;
    private WebSocketConnector connector;

    public StompedPayloadOperator(StompedFrame payload, WebSocketConnector connector){
        this.payload = payload;
        this.connector = connector;
    }

    @Override
    public void run(){

        try {

            synchronized(connector.getMonitor()){
                if (!connector.isStompConnected()) {
                    connector.getMonitor().wait();
                }
            }

            //A write method has to exist with the payload.builds return type.
            Method method = WebSocketConnector.class.getDeclaredMethod("write", payload.build().getClass());
            method.invoke(connector, payload.build());

        } catch (NoSuchMethodException e) {

            e.printStackTrace();

        } catch (InvocationTargetException e) {

            e.printStackTrace();

        } catch (IllegalAccessException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}
