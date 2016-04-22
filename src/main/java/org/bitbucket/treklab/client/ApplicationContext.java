package org.bitbucket.treklab.client;

import org.bitbucket.treklab.client.model.Device;

import java.util.HashSet;
import java.util.Set;

public class ApplicationContext {
    private static final ApplicationContext context = new ApplicationContext();

    public static ApplicationContext getInstance() {
        return context;
    }

    private Set<Integer> followedDeviceIds;

    public void follow(Device device) {
        if (followedDeviceIds == null) {
            followedDeviceIds = new HashSet<>();
        }
        followedDeviceIds.add(device.getId());
    }

    public void stopFollowing(Device device) {
        if (followedDeviceIds != null) {
            followedDeviceIds.remove(device.getId());
        }
    }

    public boolean isFollowing(Device device) {
        return followedDeviceIds != null && followedDeviceIds.contains(device.getId());
    }
}
