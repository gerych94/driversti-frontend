package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.Date;

public class OverSpeedEvent extends JavaScriptObject {

    protected OverSpeedEvent() {}

    public final native int getId() /*-{ return this.id; }-*/;
    public final native Date getTime() /*-{ return this.time; }-*/;
    public final native int getDeviceId() /*-{ return this.deviceId; }-*/;
    public final native double getSpeed() /*-{ return this.speed; }-*/;

    public final native void setId(int id) /*-{ this.id = id; }-*/;
    public final native void setTime(Date time) /*-{ this.time = time; }-*/;
    public final native void setDeviceId(int deviceId) /*-{ this.deviceId = deviceId; }-*/;
    public final native void setSpeed(double speed) /*-{ this.speed = speed; }-*/;
}
