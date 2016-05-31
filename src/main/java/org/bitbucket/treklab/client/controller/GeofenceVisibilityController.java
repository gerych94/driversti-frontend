package org.bitbucket.treklab.client.controller;

import org.bitbucket.treklab.client.model.Geofence;
import org.bitbucket.treklab.client.state.GeofenceVisibilityHandler;

import java.util.HashMap;


/**
 * Created by vitaliy on 24.05.16.
 */
public class GeofenceVisibilityController implements GeofenceVisibilityHandler {

    private final HashMap<Integer,GeofenceState> geofenceState=new HashMap();



    @Override
    public boolean isVisible(Geofence geofence) {
        return getState(geofence.getId()).state;
    }

    @Override
    public void setVisible(Geofence geofence, boolean state) {
       GeofenceState stateGeo=geofenceState.get(geofence.getId());
       if(state!=stateGeo.state){
           stateGeo.state=state;
       }
    }

    private GeofenceState getState(int idGeofence){
        GeofenceState state=geofenceState.get(idGeofence);
        if(state==null){
            state=new GeofenceState();
            geofenceState.put(idGeofence,state);
        }
        return state;
    }

    private class GeofenceState{
        boolean state=true;
    }
}
