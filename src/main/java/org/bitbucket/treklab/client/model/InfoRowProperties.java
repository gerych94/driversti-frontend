package org.bitbucket.treklab.client.model;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface InfoRowProperties extends PropertyAccess<InfoRow> {

    @Editor.Path("id")
    ModelKeyProvider<InfoRow> key();

    ValueProvider<InfoRow, String> name();

    ValueProvider<InfoRow, String> value();
}
