package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Этот интерфейс используется для доступа к полям класса DemoStoppedArrow.java
 */
public interface DemoStoppedArrowProperties extends PropertyAccess<DemoStoppedArrow> {

    @Editor.Path("id")
    ModelKeyProvider<DemoStoppedArrow> key();

    ValueProvider<DemoStoppedArrow, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoStoppedArrow> nameLabel();

    ValueProvider<DemoStoppedArrow, String> name();
}
