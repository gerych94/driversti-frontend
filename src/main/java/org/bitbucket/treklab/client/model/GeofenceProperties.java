package org.bitbucket.treklab.client.model;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.List;

public interface GeofenceProperties extends PropertyAccess<Geofence> {

    @Editor.Path("id")
    ModelKeyProvider<Geofence> key();

    ValueProvider<Geofence, Integer> id();

    ValueProvider<Geofence, String> name();

    ValueProvider<Geofence, String> description();

    ValueProvider<Geofence, List<Coordinate>> coordinates();

    ValueProvider<Geofence, Double> radius();

    ValueProvider<Geofence, Integer> userId();

    ValueProvider<Geofence, String> empty();
}
