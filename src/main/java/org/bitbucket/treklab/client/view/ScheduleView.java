package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class ScheduleView {
    private static ScheduleViewUiBinder ourUiBinder = GWT.create(ScheduleViewUiBinder.class);
    @UiField
    ContentPanel contentPanel;

    public ScheduleView() {
        ourUiBinder.createAndBindUi(this);
    }

    public ContentPanel getView() {
        return contentPanel;
    }

    interface ScheduleViewUiBinder extends UiBinder<Widget, ScheduleView> {
    }
}