package org.bitbucket.treklab.client.model;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DeviceProperties extends PropertyAccess<Device> {
    @Editor.Path("id")
    ModelKeyProvider<Device> key();

    ValueProvider<Device, Integer> id();

    ValueProvider<Device, String> name();

    ValueProvider<Device, String> uniqueId();

    ValueProvider<Device, String> status();

    ValueProvider<Device, String> lastUpdate();

    ValueProvider<Device, Integer> positionId();

    ValueProvider<Device, Double> maxSpeed();

    ValueProvider<Device, String> dateTime();

    ValueProvider<Device, ImageResource> statusImage();

    ValueProvider<Device, Device> device();

    ValueProvider<Device, String> empty();
}
