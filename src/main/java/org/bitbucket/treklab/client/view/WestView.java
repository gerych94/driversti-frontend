package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class WestView extends Composite {
    private static WestViewUiBinder ourUiBinder = GWT.create(WestViewUiBinder.class);
    @UiField
    ContentPanel contentPanel;
    @UiField(provided = true)
    ContentPanel deviceView;
    @UiField(provided = true)
    ContentPanel stateView;

    public WestView(ContentPanel deviceView,
                    ContentPanel stateView) {
        this.deviceView = deviceView;
        this.stateView = stateView;
        ourUiBinder.createAndBindUi(this);
    }

    public ContentPanel getView() {
        return contentPanel;
    }

    interface WestViewUiBinder extends UiBinder<Widget, WestView> {
    }
}