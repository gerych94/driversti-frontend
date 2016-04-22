package org.bitbucket.treklab.client.map;

import com.google.gwt.core.client.GWT;
import org.discotools.gwt.leaflet.client.map.Map;
import org.discotools.gwt.leaflet.client.types.Icon;
import org.discotools.gwt.leaflet.client.types.IconOptions;
import org.discotools.gwt.leaflet.client.types.Point;

public class IconHelper
{
       public static Icon createIcon(Map map)
    {
        // Create map center position
        IconOptions iconOptions = new IconOptions();


        //адрес картинки
        iconOptions.setIconUrl(GWT.getHostPageBaseURL() + "img/wifi.png");
        //размер картинки
        iconOptions.setIconSize(new Point(16, 16));
        iconOptions.setIconAnchor(new Point(16, 16));
        iconOptions.setPopupAnchor(new Point(0, -28));

        return new Icon(iconOptions);
    }
}