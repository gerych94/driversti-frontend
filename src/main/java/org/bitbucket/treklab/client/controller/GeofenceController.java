package org.bitbucket.treklab.client.controller;

import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.model.Geofence;

public class GeofenceController {

    private final ListStore<Geofence> geofenceStore;

    public GeofenceController(ListStore<Geofence> globalGeofenceStore) {
        this.geofenceStore = globalGeofenceStore;
    }

    public void run() {

    }
}
