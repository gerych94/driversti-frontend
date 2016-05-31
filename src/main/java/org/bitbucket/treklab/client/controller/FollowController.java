package org.bitbucket.treklab.client.controller;

import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.state.DeviceFollowHandler;

import java.util.HashMap;

public class FollowController implements DeviceFollowHandler {

    private HashMap<Integer, DeviceState> deviceHashMap = new HashMap<>();



    @Override
    public boolean isFollow(Device device) {
        return getState(device.getId()).visible;
    }

    @Override
    public void setFollow(Device device, boolean state) {
        DeviceState deviceState = deviceHashMap.get(device.getId());
        if (deviceState.visible != state) {
            deviceState.visible = state;
        }
    }

    private DeviceState getState(Integer id) {
        DeviceState state = deviceHashMap.get(id);
        if (state == null) {
            state = new DeviceState();
            deviceHashMap.put(id, state);
        }
        return state;
    }

    private static class DeviceState {
        boolean visible = false;
    }

}