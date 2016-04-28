package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import org.bitbucket.treklab.client.model.Device;

public class DevicePropertiesDialog extends Composite {
    interface DevicePropertiesDialogUiBinder extends UiBinder<Widget, DevicePropertiesDialog> {
    }

    private static DevicePropertiesDialogUiBinder uiBinder = GWT.create(DevicePropertiesDialogUiBinder.class);

    @UiField
    Window window;

    public Window getWindow() {
        return window;
    }

    public DevicePropertiesDialog(Device selectedItem, ListStore<Device> deviceStore) {
        uiBinder.createAndBindUi(this);
    }

    public void show() {
        window.show();
    }

    public void hide() {
        window.hide();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(SelectEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(SelectEvent event) {
        hide();
    }
}