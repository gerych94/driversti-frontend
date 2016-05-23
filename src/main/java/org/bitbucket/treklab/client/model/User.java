package org.bitbucket.treklab.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by dog on 3/30/16.
 */
public class User extends JavaScriptObject {
    protected User() {
    }

    public final native int getId() /*-{
        return this.id;
    }-*/;

    public final native void setId(int id) /*-{
        this.id = id;
    }-*/;

    public final native String getName() /*-{
        return this.name;
    }-*/;

    public final native void setName(String name) /*-{
        this.name = name;
    }-*/;

    public final native String getEmail() /*-{
        return this.email;
    }-*/;

    public final native void setEmail(String email) /*-{
        this.email = email;
    }-*/;

    public final native boolean getReadonly() /*-{
        return this.readonly;
    }-*/;

    public final native void setReadonly(boolean readonly) /*-{
        this.readonly = readonly;
    }-*/;

    public final native boolean getAdmin() /*-{
        return this.admin;
    }-*/;

    public final native void setAdmin(boolean admin) /*-{
        this.admin = admin;
    }-*/;

    public final native String getMap() /*-{
        return this.map;
    }-*/;

    public final native void setMap(String map) /*-{
        this.map = map;
    }-*/;

    public final native String getLanguage() /*-{
        return this.language;
    }-*/;

    public final native void setLanguage(String language) /*-{
        this.language = language;
    }-*/;

    public final native String getDistanceUnit() /*-{
        return this.distanceUnit;
    }-*/;

    public final native void setDistanceUnit(String distanceUnit) /*-{
        this.distanceUnit = distanceUnit;
    }-*/;

    public final native String getSpeedUnit() /*-{
        return this.speedUnit;
    }-*/;

    public final native void setSpeedUnit(String speedUnit) /*-{
        this.speedUnit = speedUnit;
    }-*/;

    public final native int getLatitude()/*-{
        return this.latitude;
    }-*/;

    public final native void setLatitude(int latitude)/*-{
        this.latitude = latitude;
    }-*/;

    public final native int getLongitude()/*-{
        return this.longitude;
    }-*/;

    public final native void setLongitude(int longitude)/*-{
        this.longitude = longitude;
    }-*/;

    public final native int getZoom()/*-{
        return this.zoom;
    }-*/;

    public final native void setZoom(int zoom)/*-{
        this.zoom = zoom;
    }-*/;

    public final native String getPassword()/*-{
        return this.password;
    }-*/;

    public final native void setPassword(String password)/*-{
        this.password = password;
    }-*/;

    public final native String getHashedPassword()/*-{
        return this.hachedPassword;
    }-*/;

    public final native void setHashedPassword(String hashedPassword)/*-{
        this.hachedPassword = hashedPassword;
    }-*/;

    public final native String getSalt()/*-{
        return this.salt;
    }-*/;

    public final native void setSalt(String salt)/*-{
        this.salt = salt;
    }-*/;

    public final native boolean getPasswordValid()/*-{
        return this.passwordValid;
    }-*/;

    public final native void setPasswordValid(boolean passwordValid)/*-{
        this.passwordValid = passwordValid;
    }-*/;
}
