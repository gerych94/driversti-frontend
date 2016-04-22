package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import org.bitbucket.treklab.client.resources.Resources;

import java.util.Date;

public class Device extends JavaScriptObject{

    static Resources resources = GWT.create(Resources.class);

    protected Device(){}

    public final native int getId() /*-{ return this.id; }-*/;
    public final native String getName() /*-{ return this.name; }-*/;
    public final native String getUniqueId() /*-{ return this.uniqueId; }-*/;
    public final native String getStatus() /*-{ return this.status; }-*/;
    public final native String getLastUpdate() /*-{ return this.lastUpdate; }-*/;
    public final native int getPositionId() /*-{ return this.positionId; }-*/;


    public final native void setId(int id) /*-{ this.id = id; }-*/;
    public final native void setName(String name) /*-{ this.name = name; }-*/;
    public final native void setUniqueId(String uniqueId) /*-{ this.uniqueId = uniqueId; }-*/;
    public final native void setStatus(String status) /*-{ this.status = status; }-*/;
    public final native void setLastUpdate(String lastUpdate) /*-{  this.lastUpdate = lastUpdate; }-*/;
    public final native void setPositionId(int positionId) /*-{  this.positionId = positionId; }-*/;

    public final String getDateTime() {
        Date tmp = new Date(getLastUpdate());
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
        return fmt.format(tmp);
    }

    public final ImageResource getStatusImage() {
        if (getStatus().equals("online")) {
            return resources.wifiEnable();
        } else return resources.wifiDisable();
    }

    public final String getEmpty() {
        return "";
    }

    public final Device getDevice() {
        return this;
    }

    public final boolean hasId() {
        return this.getId() >= 0;
    }
}