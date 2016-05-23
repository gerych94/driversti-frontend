package org.bitbucket.treklab.client.map;

import com.google.gwt.core.client.GWT;
import org.discotools.gwt.leaflet.client.Options;
import org.discotools.gwt.leaflet.client.layers.vector.Ellipse;
import org.discotools.gwt.leaflet.client.map.Map;
import org.discotools.gwt.leaflet.client.types.LatLng;

public class EllipseHelper {
    public static Ellipse draw(Map map) {
        GWT.log("Ellipse");
        Options ellipseOptions = new Options();
        ellipseOptions.setProperty("color", "blue");
        //четвертый параметр разворачивает эллипс
        Ellipse ellipse = new Ellipse(new LatLng(50.420710000, 30.640718000), 2000, 1000, -100, ellipseOptions);
        ellipse.addTo(map);
        return ellipse;
    }
}
