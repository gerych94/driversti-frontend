package org.bitbucket.treklab.client.util;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

public class DateHelper {

    public static native int getUTCDayNumber() /*-{
        return new Date().getUTCDay();
    }-*/;

    public static native int getUTCDay() /*-{
        return new Date().getUTCDate();
    }-*/;

    public static native int getUTCMonth() /*-{
        return new Date().getUTCMonth();
    }-*/;

    public static native int getUTCFullYear() /*-{
        return new Date().getUTCFullYear();
    }-*/;

    public static native int getUTCHours() /*-{
        return new Date().getUTCHours();
    }-*/;

    public static native int getUTCMinutes() /*-{
        return new Date().getUTCMinutes();
    }-*/;

    public static native int getUTCSeconds() /*-{
        return new Date().getUTCSeconds();
    }-*/;

    public static Date getUTCDate() {
        return new Date(getUTCFullYear() - 1900,
                getUTCMonth(),
                getUTCDay(),
                getUTCHours(),
                getUTCMinutes(),
                getUTCSeconds());
    }

    public static String getISOToDefaultTime(String deviceTime) {
        DateTimeFormat format = DateTimeFormat.getFormat("HH:mm:ss");
        return format.format(new Date(deviceTime));
    }
}
