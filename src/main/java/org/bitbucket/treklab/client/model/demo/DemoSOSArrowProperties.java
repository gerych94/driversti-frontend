package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface DemoSOSArrowProperties extends PropertyAccess<DemoSOSArrow> {

    @Editor.Path("id")
    ModelKeyProvider<DemoSOSArrow> key();

    ValueProvider<DemoSOSArrow, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoSOSArrow> nameLabel();

    ValueProvider<DemoSOSArrow, String> name();
}
