package org.bitbucket.treklab.client.model;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PositionRowProperties extends PropertyAccess<PositionRow> {

    @Editor.Path("id")
    ModelKeyProvider<PositionRow> key();

    ValueProvider<PositionRow, String> name();
    ValueProvider<PositionRow, String> value();
}
