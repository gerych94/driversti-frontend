package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DemoMovingArrowProperties extends PropertyAccess<DemoMovingArrow> {

    @Editor.Path("id")
    ModelKeyProvider<DemoMovingArrow> key();

    ValueProvider<DemoMovingArrow, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoMovingArrow> nameLabel();

    ValueProvider<DemoMovingArrow, String> name();
}
