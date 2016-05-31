package org.bitbucket.treklab.client.state;

import org.bitbucket.treklab.client.model.Geofence;

/**
 * Created by vitaliy on 24.05.16.
 */
public interface GeofenceVisibilityHandler {

    boolean isVisible(Geofence geofence);

    void setVisible(Geofence geofence,boolean state);
}
