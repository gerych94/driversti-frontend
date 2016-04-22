package org.bitbucket.treklab.client.communication;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import org.bitbucket.treklab.client.model.User;

public class SessionData extends ServerData {
    //Request callback return User object
    public void getSession(RequestCallback callback) throws RequestException {
        sendRequest("/session", null, RequestBuilder.GET, callback);
    }

    public void setSession(String email, String password, RequestCallback callback) throws RequestException {
        sendRequest("/session", "email=" + email + "&password=" + password, RequestBuilder.POST, callback);
    }

    // if request is valid return 204
    public void deleteSession(RequestCallback callback) throws RequestException {
        sendRequest("/session", null, RequestBuilder.DELETE, callback);
    }

    @Override
    protected void sendRequest(String urlParameters, String data, final RequestBuilder.Method method, RequestCallback callback) throws RequestException {
        String url = URL.encode(JSON_URL + urlParameters);
        RequestBuilder builder = new RequestBuilder(method, url);
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
        Request request = builder.sendRequest(data, callback);
    }

    private String createUserDataForRequest(User user) {
        return JsonUtils.stringify(user);
    }
}
