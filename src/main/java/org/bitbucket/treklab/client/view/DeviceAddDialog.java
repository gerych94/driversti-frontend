package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.DeviceData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.util.LoggerHelper;

public class DeviceAddDialog {
    interface DeviceDialogUiBinder extends UiBinder<Widget, DeviceAddDialog> {
    }

    private static DeviceDialogUiBinder ourUiBinder = GWT.create(DeviceDialogUiBinder.class);

    private static final String className = DeviceAddDialog.class.getSimpleName();

    private Device device;
    private ListStore<Device> deviceStore;
    private DeviceData deviceData;

    @UiField
    Window window;

    public Window getWindow() {
        return window;
    }

    @UiField
    VerticalLayoutContainer container;
    @UiField
    TextField nameField;
    @UiField
    TextField uniqueIdField;

    public DeviceAddDialog(Device selectedDevice, ListStore<Device> deviceStore) {
        this.device = selectedDevice;   // по сути это лишнее, как и устройство в конструкторе
                                        // так как за редактирование устройства отвечает новый DevicePropertiesDialog
        this.deviceStore = deviceStore;
        this.deviceData = new DeviceData();

        ourUiBinder.createAndBindUi(this);

        /**
         * Эти действия уже не нужны, так как за редактирование устройства
         * отвечает новый DevicePropertiesDialog
         */
        //this.nameField.setText(device.getName());
        //this.uniqueIdField.setText(device.getUniqueId());
    }

    public void show() {
        window.show();
    }

    public void hide() {
        window.hide();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(SelectEvent event) {
        // TODO: 28.03.2016 реализовать метод в контроллере (пересмотреть необходимость изменить параметры конструктора и создание временных устройств)
        Device tempDevice = (Device) Device.createObject();
        tempDevice.setName(nameField.getText());
        tempDevice.setUniqueId(uniqueIdField.getText());

        try {
            deviceData.addDevice(tempDevice, new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        device = JsonUtils.safeEval(response.getText());
                        deviceStore.add(device);
                        hide();
                        LoggerHelper.log(className, "New device has been added. Device name: " + device.getName());
                    } else if (400 == response.getStatusCode()) {
                        new AlertMessageBox("Ошибка добавления", "Такой ИМЕИ уже существует").show();
                    } else {
                        LoggerHelper.log(className, "Bad response from server. Status code: " + response.getStatusCode());
                        new AlertMessageBox("Adding device error", "Status code = " + response.getStatusCode()).show();
                    }
                }
            });
        } catch (RequestException e) {
            LoggerHelper.log(className, "Some error while connecting to the server", e);
        }


/*        // этот код выполняется, если мы обновляем существующий объект
        if (device.hasId()) {
            tempDevice.setId(device.getId());
            try {
                deviceData.updateDevice(tempDevice, new BaseRequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        if (200 == response.getStatusCode()) {
                            device = JsonUtils.safeEval(response.getText());
                            deviceStore.update(device);
                            hide();
                            LoggerHelper.log(className, "Device "+ device.getName() + " has been updated.");
                        } else if (400 == response.getStatusCode()) {
                            new AlertMessageBox("Ошибка обновления", "Такой ИМЕИ уже существует").show();
                            uniqueIdField.setText(device.getUniqueId());
                        } else {
                            new AlertMessageBox("Updating error", "Status code = " + response.getStatusCode()).show();
                        }
                    }
                });
            } catch (RequestException e) {
                e.printStackTrace();
            }
            // этот код выполняется, если мы добавляем новый объект
        } else {
            try {
                deviceData.addDevice(tempDevice, new BaseRequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        if (200 == response.getStatusCode()) {
                            device = JsonUtils.safeEval(response.getText());
                            deviceStore.add(device);
                            hide();
                            LoggerHelper.log(className, "New device has been added. Device name: " + device.getName());
                        } else if (400 == response.getStatusCode()) {
                            new AlertMessageBox("Ошибка добавления", "Такой ИМЕИ уже существует").show();
                        } else {
                            LoggerHelper.log(className, "Bad response from server. Status code: " + response.getStatusCode());
                            new AlertMessageBox("Adding device error", "Status code = " + response.getStatusCode()).show();
                        }
                    }
                });
            } catch (RequestException e) {
                LoggerHelper.log(className, "Some error while connecting to the server", e);
            }
        }*/
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(SelectEvent event) {
        hide();
    }
}