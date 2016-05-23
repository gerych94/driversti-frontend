package org.bitbucket.treklab.client.communication;

import com.google.gwt.http.client.*;
import org.bitbucket.treklab.client.Application;

public abstract class ServerData {
    // Використовується тракар на який я налаштував на сервері
    protected static final String JSON_URL = "http://185.69.152.120:8082/api";

    //Метод для створення запиту на сервер
    //urlParameters - параметри що додаються до url
    //data - дані які передаються в тілі запиту таких як post put
    //method - метод запиту даних post put get delete
    //callback - функція зворотнього виклику
    //
    protected void sendRequest(String urlParameters, String data, final RequestBuilder.Method method, RequestCallback callback) throws RequestException {
        String url = URL.encode(JSON_URL + urlParameters);
        RequestBuilder builder = new RequestBuilder(method, url);
        builder.setHeader("Content-Type", "application/json");
        //авторизаційні дані які потрібні для кожного запиту, зараз (admin admin)
        //builder.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        builder.setHeader("Authorization", "Basic " + Application.getDataServiceController().getUserAuthentication());

        Request request = builder.sendRequest(data, callback);
    }
}
