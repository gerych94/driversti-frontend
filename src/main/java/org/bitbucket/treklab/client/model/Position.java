package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class Position extends JavaScriptObject {
    protected Position(){}

    public final native int getDeviceId() /*-{ return this.deviceId; }-*/;
    public final native String getType() /*-{ return this.type; }-*/;
    public final native Attributes getAttributes() /*-{ return this.attributes }-*/;
    public final native int getId() /*-{ return this.id }-*/;
    public final native String getProtocol() /*-{ return this.protocol }-*/;
    public final native String getServerTime() /*-{ return this.serverTime }-*/;
    public final native String getDeviceTime() /*-{ return this.deviceTime }-*/;
    public final native String getFixTime() /*-{ return this.fixTime; }-*/;
    public final native boolean getOutdated()/*-{ return this.outdated; }-*/;
    public final native boolean getValid()/*-{ return this.valid; }-*/;
    public final native double getLatitude()/*-{ return this.latitude; }-*/;
    public final native double getLongitude()/*-{ return this.longitude; }-*/;
    public final native double getAltitude()/*-{ return this.altitude; }-*/;
    public final native double getSpeed()/*-{ return this.speed * 1.852; }-*/;
    public final native double getCourse()/*-{ return this.course; }-*/;
    public final native String getAddress()/*-{ return this.address; }-*/;

    public final native void setDeviceId(int id) /*-{ this.deviceId = deviceId; }-*/;
    public final native void setType(String type) /*-{ this.type = type; }-*/;
    public final native void setAttributes(Attributes attributes) /*-{ this.attributes = attributes; }-*/;
    public final native void setId(int id) /*-{ this.id = id; }-*/;
    public final native void setProtocol(String protocol) /*-{ this.protocol = protocol; }-*/;
    public final native void setServerTime(String serverTime) /*-{ this.serverTime = serverTime; }-*/;
    public final native void setDeviceTime(String deviceTime) /*-{ this.deviceTime = deviceTime; }-*/;
    public final native void setFixTime(String fixTime) /*-{ this.fixTime = fixTime; }-*/;
    public final native void setOutdated(boolean outdated)/*-{ this.outdated = outdated; }-*/;
    public final native void setValid(boolean valid)/*-{ this.valid = valid; }-*/;
    public final native void setLatitude(double latitude)/*-{ this.latitude = latitude; }-*/;
    public final native void setLongitude(double longitude)/*-{ this.longitude = longitude; }-*/;
    public final native void setAltitude(double altitude)/*-{ this.altitude = altitude; }-*/;
    public final native void setSpeed(double speed)/*-{ this.speed = speed / 1.852; }-*/;
    public final native void setCourse(double course)/*-{ this.course = course; }-*/;
    public final native void setAddress(String address)/*-{ this.address = address; }-*/;
}
