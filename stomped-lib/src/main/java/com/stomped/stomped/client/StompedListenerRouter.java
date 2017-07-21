package com.stomped.stomped.client;

/*
 * Description: This class will route incoming messages to
 * the correct listeners. This way, a client who is subscribed
 * to two different paths wont have to worry about the listeners
 * catching messages from both subscriptions.
 */

import com.stomped.stomped.component.StompedFrame;
import com.stomped.stomped.component.StompedHeaders;
import com.stomped.stomped.listener.StompedListener;

import java.util.HashMap;

public class StompedListenerRouter {

    private static final String TAG = "StompedListenerRouter";
    private static StompedListenerRouter router;

    private HashMap<String, StompedListener> routingTable;

    private StompedListenerRouter(){
        routingTable = new HashMap<>();
    }

    public static synchronized StompedListenerRouter getInstance() {

        if (router == null) {

            router = new StompedListenerRouter();
        }

        return router;
    }

    public void addListener(StompedListener listener){
        routingTable.put(listener.getDestination(), listener);
    }

    public StompedListener removeListener(String key){
        return routingTable.remove(key);
    }

    public void sendMessage(StompedFrame frame){
        StompedListener currentListener = routingTable.get(frame.getHeaderValueFromKey(StompedHeaders.STOMP_HEADER_DESTINATION));

        if(currentListener != null){
            currentListener.onNotify(frame);
        }
    }
}
