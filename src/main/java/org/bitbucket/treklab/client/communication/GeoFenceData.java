package org.bitbucket.treklab.client.communication;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import org.bitbucket.treklab.client.model.Geofence;

/**
 * Created by dog on 5/5/16.
 */
public class GeofenceData extends ServerData{
    public void getGeofences(RequestCallback callback) throws RequestException {
        sendRequest("/geofence", null, RequestBuilder.GET, callback);
    }

    public void addGeofence(Geofence geofence, RequestCallback callback) throws RequestException {
        sendRequest("/geofence", createGeofenceDataForRequest(geofence), RequestBuilder.POST, callback);
    }

    public void updateGeofence(Geofence geofence, RequestCallback callback) throws RequestException {
        sendRequest("/geofence/" + geofence.getId(), createGeofenceDataForRequest(geofence), RequestBuilder.PUT, callback);
    }

    public void removeGeofence(Geofence geofence, RequestCallback callback) throws RequestException {
        sendRequest("/geofence/" + geofence.getId() , null, RequestBuilder.DELETE, callback);
    }

    private String createGeofenceDataForRequest(Geofence geofence) {
        return JsonUtils.stringify(geofence);
    }
}
