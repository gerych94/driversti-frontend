package org.bitbucket.treklab.client.communication;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;

public abstract class BaseRequestCallback implements RequestCallback {
    @Override
    public abstract void onResponseReceived(Request request, Response response);

    @Override
    public void onError(Request request, Throwable exception) {
        MessageBox error = new MessageBox("Couldn't retrieve JSON");
        RootPanel.get().add(error);
    }
}
