package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Этот интерфейс используется для доступа к полям класса DemoTrailer.java
 */
public interface DemoTrailerProperties extends PropertyAccess<DemoTrailer> {

    @Editor.Path("id")
    ModelKeyProvider<DemoTrailer> key();

    ValueProvider<DemoTrailer, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoTrailer> nameLabel();

    ValueProvider<DemoTrailer, String> name();
}
