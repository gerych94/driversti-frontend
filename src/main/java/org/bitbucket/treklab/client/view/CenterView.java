package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class CenterView extends Composite {
    private static CenterViewUiBinder ourUiBinder = GWT.create(CenterViewUiBinder.class);
    @UiField(provided = true)
    final Viewport mapView;
    @UiField
    ContentPanel contentPanel;
    @UiField(provided = true)
    ContentPanel scheduleView;
    @UiField
    BorderLayoutContainer.BorderLayoutData southData;
    public CenterView(final Viewport mapV, ContentPanel scheduleView) {
        this.mapView = mapV;
        this.scheduleView = scheduleView;

        ourUiBinder.createAndBindUi(this);

        this.southData.setCollapseMini(true);
        this.southData.setCollapsed(true);
        //this.southData.setCollapseHidden(true);
    }

    public ContentPanel getView() {
        return contentPanel;
    }

    interface CenterViewUiBinder extends UiBinder<Widget, CenterView> {
    }
}