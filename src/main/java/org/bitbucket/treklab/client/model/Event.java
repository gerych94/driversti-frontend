package org.bitbucket.treklab.client.model;

import org.bitbucket.treklab.client.util.DateHelper;

public class Event {
    private static int COUNTER = 0;

    private int id;
    private String time;
    private String deviceName;
    private String message;

    public Event() {
        this.id = COUNTER++;
    }

    public Event(String deviceTime, String deviceName, String message) {
        this.id = COUNTER++;
        this.time = DateHelper.getISOToDefaultTime(deviceTime);
        this.deviceName = deviceName;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMessage() {
        return message;
    }

/*    private String currentTime() {
        DateTimeFormat format = DateTimeFormat.getFormat("hh:mm:ss");
        return format.format(new Date());
    }*/
}
