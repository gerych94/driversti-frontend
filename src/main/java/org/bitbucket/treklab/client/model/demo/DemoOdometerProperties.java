package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Этот интерфейс используется для доступа к полям класса DemoGroup.java
 */
public interface DemoOdometerProperties extends PropertyAccess<DemoGroup> {
    @Editor.Path("id")
    ModelKeyProvider<DemoOdometer> key();

    ValueProvider<DemoOdometer, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoOdometer> nameLabel();

    ValueProvider<DemoOdometer, String> name();
}
