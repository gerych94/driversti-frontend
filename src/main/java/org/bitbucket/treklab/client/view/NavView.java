package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class NavView {
    interface NavViewUiBinder extends UiBinder<Widget, NavView> {
    }

    private static NavViewUiBinder uiBinder = GWT.create(NavViewUiBinder.class);

    @UiField
    ContentPanel contentPanel;

    public ContentPanel getView() {
        return contentPanel;
    }

    public NavView() {
        uiBinder.createAndBindUi(this);
    }
}