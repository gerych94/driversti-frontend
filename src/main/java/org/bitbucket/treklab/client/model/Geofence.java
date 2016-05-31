package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.List;

public class Geofence extends JavaScriptObject {

    protected Geofence() {}

    public final native int getId() /*-{ return this.id; }-*/;
    public final native String getName() /*-{ return this.name; }-*/;
    public final native String getDescription() /*-{ return this.description; }-*/;
    public final native Type getType() /*-{ return this.type; }-*/;
    public final native List<Coordinate> getCoordinates() /*-{ return this.coordinates; }-*/;
    public final native double getRadius() /*-{ return this.radius; }-*/;
    public final native int getUserId() /*-{ return this.userId; }-*/;

    public final native void setId(int id) /*-{ this.id = id; }-*/;
    public final native void setName(String name) /*-{ this.name = name; }-*/;
    public final native void setDescription(String description) /*-{ this.description = description; }-*/;
    public final native void setType(Type type) /*-{ this.type = type; }-*/;
    public final native void setCoordinates(List<Coordinate> coordinates) /*-{ this.coordinates = coordinates; }-*/;
    public final native void setRadius(double radius) /*-{ this.radius = radius; }-*/;
    public final native void setUserId(int userId) /*-{ this.userId = userId; }-*/;

    public final String getEmpty() {
        return "";
    }
}
