package org.bitbucket.treklab.client.communication.socket;

import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Window;
import org.bitbucket.treklab.client.util.LoggerHelper;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListener;

public class SocketListener implements WebSocketListener {

    @Override
    public void onOpen(WebSocket webSocket) {
        Window.alert( "WebSocket open!" );
    }

    @Override
    public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) {
        Window.alert( "WebSocket close! "  + reason);
    }

    @Override
    public void onMessage(WebSocket webSocket, String data) {
        Window.alert( "message from server: " + data);
    }

    @Override
    public void onMessage(WebSocket webSocket, ArrayBuffer data) {

    }

    @Override
    public void onError(WebSocket webSocket) {

    }

}