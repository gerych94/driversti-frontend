package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class Coordinate extends JavaScriptObject {
    protected Coordinate() {}

    public final native double getLatitude() /*-{ return this.latitude }-*/;
    public final native double getLongitude() /*-{ return this.longitude }-*/;

    public final native void setLatitude(double latitude) /*-{ this.latitude = latitude; }-*/;
    public final native void setLongitude(double longitude) /*-{ this.longitude = longitude; }-*/;
}
