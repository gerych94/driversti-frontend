package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Этот интерфейс используется для доступа к полям класса DemoMapIcon.java
 */
public interface DemoMapIconProperties extends PropertyAccess<DemoMapIcon> {

    @Editor.Path("id")
    ModelKeyProvider<DemoMapIcon> key();

    ValueProvider<DemoMapIcon, Integer> id();

    @Editor.Path("name")
    LabelProvider<DemoMapIcon> nameLabel();

    ValueProvider<DemoMapIcon, String> name();
}
