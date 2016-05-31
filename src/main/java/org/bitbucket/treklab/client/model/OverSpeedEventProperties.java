package org.bitbucket.treklab.client.model;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

public interface OverSpeedEventProperties extends PropertyAccess<OverSpeedEvent> {
    @Editor.Path("id")
    ModelKeyProvider<OverSpeedEvent> key();

    ValueProvider<OverSpeedEvent, Integer> id();
    ValueProvider<OverSpeedEvent, Date> time();
    ValueProvider<OverSpeedEvent, Integer> deviceId();
    ValueProvider<OverSpeedEvent, Double> speed();
}
