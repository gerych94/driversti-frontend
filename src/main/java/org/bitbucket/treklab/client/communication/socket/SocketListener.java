package org.bitbucket.treklab.client.communication.socket;

import com.google.gwt.typedarrays.shared.ArrayBuffer;
import org.bitbucket.treklab.client.util.LoggerHelper;
import org.bitbucket.treklab.client.util.ServerDataHolder;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListener;

public class SocketListener implements WebSocketListener {

    private static final String className = SocketListener.class.getSimpleName();

    @Override
    public void onOpen(WebSocket webSocket) {
        //Window.alert( "WebSocket open!" );
        LoggerHelper.log(className, "WebSocket open!");
    }

    @Override
    public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) {
        //Window.alert("WebSocket close! " + reason + " and webSocket is connected: " + webSocket.isConnected());
        LoggerHelper.log(className, "WebSocket close!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String data) {
        //Window.alert( "message from server: " + data);
        ServerDataHolder instance = ServerDataHolder.getInstance();
        instance.parse(data);
    }

    @Override
    public void onMessage(WebSocket webSocket, ArrayBuffer data) {
        LoggerHelper.log(className, "ArrayBuffer data");
    }

    @Override
    public void onError(WebSocket webSocket) {

    }

}