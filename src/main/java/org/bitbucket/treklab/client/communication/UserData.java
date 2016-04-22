package org.bitbucket.treklab.client.communication;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import org.bitbucket.treklab.client.model.User;

public class UserData extends ServerData {
    public void getUsers(RequestCallback callback) throws RequestException {
        super.sendRequest("/users", null, RequestBuilder.GET, callback);
    }

    public void addUser(User user, RequestCallback callback) throws RequestException {
        sendRequest("/users", createUserDataForRequest(user), RequestBuilder.POST, callback);
    }

    public void updateUser(User user, RequestCallback callback) throws RequestException {
        super.sendRequest("/users/" + user.getId(), createUserDataForRequest(user), RequestBuilder.PUT, callback);
    }

    public void removeUser(User user, RequestCallback callback) throws RequestException {
        super.sendRequest("/users/" + user.getId(), null, RequestBuilder.DELETE, callback);
    }

    private String createUserDataForRequest(User user) {
        return JsonUtils.stringify(user);
    }

    @Override
    protected void sendRequest(String urlParameters, String data, final RequestBuilder.Method method, RequestCallback callback) throws RequestException {
        String url = URL.encode(JSON_URL + urlParameters);
        RequestBuilder builder = new RequestBuilder(method, url);
        builder.setHeader("Content-Type", "application/json");
        Request request = builder.sendRequest(data, callback);
    }
}
