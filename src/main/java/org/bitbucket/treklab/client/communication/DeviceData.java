package org.bitbucket.treklab.client.communication;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import org.bitbucket.treklab.client.model.Device;

public class DeviceData extends ServerData {
    // методы получения добавления и парсинга данных
    public void getDevices(RequestCallback callback) throws RequestException {
        sendRequest("/devices", null, RequestBuilder.GET, callback);
    }

    public void addDevice(Device device, RequestCallback callback) throws RequestException {
        sendRequest("/devices", createDeviceDataForRequest(device), RequestBuilder.POST, callback);
    }

    public void updateDevice(Device device, RequestCallback callback) throws RequestException {
        sendRequest("/devices/" + device.getId(), createDeviceDataForRequest(device), RequestBuilder.PUT, callback);
    }

    public void removeDevice(Device device, RequestCallback callback) throws RequestException {
        sendRequest("/devices/" + device.getId() , null, RequestBuilder.DELETE, callback);
    }

    private String createDeviceDataForRequest(Device device) {
        return JsonUtils.stringify(device);
    }

}
