package org.bitbucket.treklab.client.state;

import org.bitbucket.treklab.client.model.Device;

public interface DeviceVisibilityHandler {
    boolean isVisible(Device device);

    void setVisible(Device device, boolean state);
}
