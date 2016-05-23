package org.bitbucket.treklab.client.controller;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.state.DeviceFollowHandler;

import java.util.HashMap;

public class FollowController implements ContentController, DeviceFollowHandler {

    private HashMap<Integer, DeviceState> deviceHashMap = new HashMap<>();
    private ListStore<Device> deviceListStore;

    public FollowController(ListStore<Device> deviceListStore) {
        this.deviceListStore = deviceListStore;
        run();
    }

    @Override
    public ContentPanel getView() {
        return null;
    }

    @Override
    public void run() {
        setFollowAllDevices();
    }

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
            state.visible = false;
            deviceHashMap.put(id, state);
        }
        return state;
    }

    private void setFollowAllDevices() {
        for (int i = 0; i < deviceListStore.size(); i++) {
            deviceHashMap.put(deviceListStore.get(i).getId(), new DeviceState());
        }
    }

    private static class DeviceState {
        boolean visible = false;
    }

}