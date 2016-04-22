package org.bitbucket.treklab.client.state;

import org.bitbucket.treklab.client.model.Device;

public interface DeviceFollowHandler {
    boolean isFollow(Device device);

    void setFollow(Device device, boolean state);
}
