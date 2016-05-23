package org.bitbucket.treklab.client.controller;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.state.DeviceVisibilityHandler;

import java.util.HashMap;
import java.util.Map;

public class VisibilityController implements ContentController, DeviceVisibilityHandler {

    private final Map<Integer, DeviceState> deviceState = new HashMap<>();
    private final ListStore<Device> deviceListStore;

    public VisibilityController(ListStore<Device> deviceListStore) {
        this.deviceListStore = deviceListStore;
        run();
    }

    @Override
    public ContentPanel getView() {
        return null;
    }

    @Override
    public void run() {
        setVisibilityAllDevices();
    }

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
            state.visible = true;
            deviceState.put(id, state);
        }
        return state;
    }

    private void setVisibilityAllDevices() {
        for (int i = 0; i < deviceListStore.size(); i++) {
            deviceState.put(deviceListStore.get(i).getId(), new DeviceState());
        }
    }

    private class DeviceState {
        boolean visible = true;
    }

}