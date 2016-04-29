package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DemoDriverProperties extends PropertyAccess<DemoDriver> {

    @Editor.Path("id")
    ModelKeyProvider<DemoDriver> key();

    ValueProvider<DemoDriver, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoDriver> nameLabel();

    ValueProvider<DemoDriver, String> name();
}
