package org.bitbucket.treklab.client.model.demo;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Этот интерфейс используется для доступа к полям класса DemoGPS.java
 */
public interface DemoGPSProperties extends PropertyAccess<DemoGPS> {

    @Editor.Path("id")
    ModelKeyProvider<DemoGPS> key();

    ValueProvider<DemoGPS, Integer> id();
    @Editor.Path("name")
    LabelProvider<DemoGPS> nameLabel();

    ValueProvider<DemoGPS, String> name();
}
