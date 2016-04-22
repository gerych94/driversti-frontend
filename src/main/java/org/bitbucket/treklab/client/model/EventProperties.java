package org.bitbucket.treklab.client.model;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface EventProperties extends PropertyAccess<Event> {
    @Editor.Path("id")
    ModelKeyProvider<Event> key();

    ValueProvider<Event, String> time();
    ValueProvider<Event, String> deviceName();
    ValueProvider<Event, String> message();
}
