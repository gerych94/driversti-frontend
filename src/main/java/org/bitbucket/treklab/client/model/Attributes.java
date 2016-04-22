package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class Attributes extends JavaScriptObject {

    protected Attributes() {}

    public final native int getPriority() /*-{ return this.priority; }-*/;
    public final native int getSat() /*-{ return this.sat; }-*/;
    public final native int getEvent() /*-{ return this.event; }-*/;
    public final native String getIp() /*-{ return this.ip; }-*/;
    public final native double getBattery() /*-{ return this.battery; }-*/;

    public final native void setPriority(int priority) /*-{ this.priority = priority; }-*/;
    public final native void setSat(int sat) /*-{ this.sat = sat; }-*/;
    public final native void setEvent(int event) /*-{ this.event = event; }-*/;
    public final native void setIp(String ip) /*-{ this.ip = ip; }-*/;
    public final native void setBattery(double battery) /*-{ this.battery = battery; }-*/;
}
