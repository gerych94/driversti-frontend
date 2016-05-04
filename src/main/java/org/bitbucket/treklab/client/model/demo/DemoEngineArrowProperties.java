package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DemoEngineArrowProperties extends PropertyAccess<DemoEngineArrow> {

    @Editor.Path("id")
    ModelKeyProvider<DemoEngineArrow> key();

    ValueProvider<DemoEngineArrow, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoEngineArrow> nameLabel();

    ValueProvider<DemoEngineArrow, String> name();
}
