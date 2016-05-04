package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Этот интерфейс используется для доступа к полям класса DemoMoto.java
 */
public interface DemoMotoProperties extends PropertyAccess<DemoMoto> {
    @Editor.Path("id")
    ModelKeyProvider<DemoMoto> key();

    ValueProvider<DemoMoto, Integer> id();

    @Editor.Path("name")
    LabelProvider<DemoMoto> nameLabel();

    ValueProvider<DemoMoto, String> name();
}
