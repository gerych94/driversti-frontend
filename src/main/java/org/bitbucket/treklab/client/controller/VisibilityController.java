package org.bitbucket.treklab.client.controller;

import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.state.DeviceVisibilityHandler;

import java.util.HashMap;
import java.util.Map;



public class VisibilityController implements DeviceVisibilityHandler {

    private final Map<Integer, DeviceState> deviceState = new HashMap<>();



    @Override
    public boolean isVisible(Device device) {
        return getState(device.getId()).visible;
    }

    @Override
    public void setVisible(Device device, boolean state) {
        DeviceState deviceState = getState(device.getId());
        if (deviceState.visible != state) {
            deviceState.visible = state;
        }
    }

    private DeviceState getState(Integer id) {
        DeviceState state = deviceState.get(id);
        if (state == null) {
            state = new DeviceState();
            deviceState.put(id, state);
        }
        return state;
    }

    private class DeviceState {
        boolean visible = true;
    }

}