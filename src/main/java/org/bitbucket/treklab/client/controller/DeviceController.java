package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.DeviceData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.Event;
import org.bitbucket.treklab.client.state.DeviceFollowHandler;
import org.bitbucket.treklab.client.state.DeviceVisibilityHandler;
import org.bitbucket.treklab.client.util.LoggerHelper;
import org.bitbucket.treklab.client.view.DeviceDialog;
import org.bitbucket.treklab.client.view.DeviceView;

/**
 * Контроллер панели отображения устройств
 */
public class DeviceController implements ContentController, DeviceView.DeviceHandler {
    private final DeviceView deviceView;
    private ListStore<Device> deviceStore;
    private final DeviceData deviceData;
    private final MapController mapController;
    private final StateController stateController;

    private static final String className = DeviceController.class.getSimpleName();

    /**
     * Этот метод возвращает графический интерфейс для отображения списка устройства и тулбара для кнопок усправления:
     * добавления и удаления устройств из списка, фильтра устройств по имени
     *
     * @return - графический интерфейс
     */
    @Override
    public ContentPanel getView() {
        return deviceView.getView();
    }

    /**
     * Конструктор контроллера
     *
     * @param globalDeviceStore - глобальный список устройств
     *                          В конструкторе инициализируем объект для вызова методов АПИ
     *                          и создаём графический интерфейс для отображения устройств
     * @param globalEventStore  - глобальный список событий
     * @param mapController     - контроллер карты
     */
    public DeviceController(ListStore<Device> globalDeviceStore,
                            ListStore<Event> globalEventStore,
                            MapController mapController,
                            StateController stateController,
                            DeviceVisibilityHandler deviceVisibilityHandler,
                            DeviceFollowHandler deviceFollowHandler) {

        this.deviceStore = globalDeviceStore;
        this.mapController = mapController;
        this.stateController = stateController;
        this.deviceView = new DeviceView(this,
                globalDeviceStore,
                globalEventStore,
                stateController,
                deviceVisibilityHandler,
                deviceFollowHandler);
        this.deviceData = new DeviceData();
    }

    /**
     * Этот метод получает устройства от сервера и заполняет список устройств
     */
    @Override
    public void run() {
        try {
            // вызываем метод для получения устройств
            deviceData.getDevices(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // в случае успешного ответа получаем список объектов
                        JsArray<Device> data = JsonUtils.safeEval(response.getText());
                        /** в цикле добавляем новые объекты или обновляем существующие */
                        /** если список пустой, добавляем в него все объекты из сервера */
                        if (deviceStore.size() <= 0) {
                            for (int i = 0; i < data.length(); i++) {
                                deviceStore.add(data.get(i));
                                deviceCheckBoxActionVisible(data.get(i));
                            }
                            startFocused();
                        } else if (deviceStore.size() <= data.length()) {
                            /** Если из сервера пришло объектов больше, чем есть в списке */{
                                for (int i = 0; i < data.length(); i++) {
                                    // проверяем соответствует ли каждому объекту в списке устройств устройство из сервера
                                    if (deviceStore.get(i) == null) {
                                        // если в списке нет устройства на позиции i - добавляем
                                        deviceStore.add(data.get(i));
                                    } else if (deviceStore.get(i).getId() == data.get(i).getId()) {
                                        // если устройства на позиции i есть в обоих списках - обновляем
                                        deviceStore.update(data.get(i));
                                    }
                                }
                            }
                        } else if (deviceStore.size() > data.length()) {
                            /** Если из сервера пришло объектов меньше, чем есть в списке */{
                                for (int i = 0; i < deviceStore.size(); i++) {
                                    // проверяем соответствует ли каждому объекту в списке устройств устройство из сервера
                                    if (data.get(i) == null) {
                                        // если нет, удаляем устройство из списка
                                        deviceStore.remove(i);
                                        // и делаем шаг назад, так как количество устройств в списке уменьшилось на 1
                                        i--;
                                    } else if (data.get(i) == deviceStore.get(i)) {
                                        // если на месте в списке устройств есть устройство и в списке из сервера - обновляем
                                        deviceStore.update(data.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        // при ошибке получения данных отображаем соответствующее сообщение с кодом ошибки (console!)
                        LoggerHelper.log(className, "Bad response from server. Response status code: " + response.getStatusCode());
                    }
                }
            });
        } catch (RequestException e) {
            LoggerHelper.log(className, "Error while getting devices. No response from server.", e);
        }
    }

    /**
     * Этот метод запускается при нажатии на кнопку "Обновить"
     */
    @Override
    public void onRefresh() {
        LoggerHelper.log(className, "'Refresh button' has been pressed");
        run();
    }

    /**
     * Этот метод добавляет объект в таблицу и на сервер
     */
    @Override
    public void onAdd() {
        LoggerHelper.log(className, "'Add button' has been pressed");
        // создаём временное устройство
        Device device = (Device) Device.createObject();
        // создаём диалог и передаём в конструктор временный объект устройства и список устройств
        new DeviceDialog(device, deviceStore).show();
    }

    @Override
    public void onUpdate(Device selectedItem) {
        LoggerHelper.log(className, "'Edit' has been pressed");
        new DeviceDialog(selectedItem, deviceStore).show();
    }

    /**
     * Этот метод удаляет объект из таблицы и сервера
     *
     * @param selectedItem - объект для удаления
     */
    @Override
    public void onRemove(final Device selectedItem) {
        LoggerHelper.log(className, "'Remove button' has been pressed");
        final ConfirmMessageBox confirm = new ConfirmMessageBox(
                "Подтверждение удаления устройства",
                "Это действие удалит все данные этого трекера, восстановление будет невозможно! \n" +
                        "Вы действительно хотите удалить устройство?");
        confirm.setResizable(false);
        confirm.setModal(true);
        confirm.setWidth(350);
        confirm.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(final DialogHideEvent event) {
                if (event.getHideButton() == Dialog.PredefinedButton.YES) {
                    LoggerHelper.log(className, "Button YES has been pressed");
                    try {
                        // запрос к серверу на удаление устройста
                        deviceData.removeDevice(selectedItem, new BaseRequestCallback() {
                            @Override
                            public void onResponseReceived(Request request, Response response) {
                                if (204 == response.getStatusCode()) {
                                    // если ответ от сервера правильный (в данном случае 204), удаляем устройство из таблицы
                                    deviceStore.remove(selectedItem);
                                    // устанавливаем кнопку удаления в выключенное состояние
                                    //deviceView.getRemoveDeviceButton().setEnabled(false);
                                    LoggerHelper.log(className, "Device " + selectedItem.getName() + " has been removed. Bye-bye motherfucker!");
                                } else {
                                    LoggerHelper.log(className, "Error while deleting device. " +
                                            "Error code: " + response.getStatusCode() +
                                            ". Error status message: " + response.getStatusText());
                                    new AlertMessageBox("Error", "Error while deleting device. " +
                                            "Error code: " + response.getStatusCode() +
                                            ". Error status message: " + response.getStatusText()).show();
                                }
                            }
                        });
                    } catch (RequestException e) {
                        LoggerHelper.log(className, "Error while deleting device. No response from server.", e);
                    }
                }
            }
        });
        confirm.show();
    }

    @Override
    public void onShowHistory(Device selectedItem) {
        LoggerHelper.log(className, "onShowHistory is not implemented");
    }

    /**
     * Этот метод вызывается при нажатии на строку в таблице устройств.
     * Он активирует кнопку для удаления выбранного устройства,
     * фокусирует на нём карту и отображает его дополнительные данные
     * в нижней таблице
     */
    @Override
    public void onSelected() {
        // получаем выбранное устройство
        Device selectedItem = deviceView.getDeviceGrid().getSelectionModel().getSelectedItem();
        LoggerHelper.log(className, "Device " + selectedItem.getName() + " was selected.");
        //deviceView.getRemoveDeviceButton().setEnabled(true);
        stateController.fillGrid(selectedItem);
    }

    /**
     * Этот метод срабатывает при двойном клике по строке в таблице устройств
     *
     * @param selectedItem - выбранное устройство в таблице
     */
    @Override
    public void doubleClicked(Device selectedItem) {
        LoggerHelper.log(className, "Row double clicked in Device Table");
        // проверка, что оно точно выбрано, а не null
        if (selectedItem != null) {
            //deviceView.getRemoveDeviceButton().setEnabled(true);
            mapController.focusedOnDevice(selectedItem);
        } /*else {
            // в противном случае деактивируем кнопку удаления
            //deviceView.getRemoveDeviceButton().setEnabled(false);
        }*/
    }

    //метод который реагирует на переключение чекбокса visible
    @Override
    public void deviceCheckBoxActionVisible(Device device) {
        //устанавливает - удаляет маркер для определенного устройства
        if (deviceView.getDeviceVisibilityHandler().isVisible(device)) {
            mapController.setDeviceMarker(device);
        } else {
            mapController.removeDeviceMarker(device);
        }
    }

    //метод который реагирует на переключение чекбокса follow
    @Override
    public void deviceCheckBoxActionFollow(Device device) {
        //работает только если включен чекбок visible
        if (deviceView.getDeviceVisibilityHandler().isVisible(device)) {
            mapController.refocusedDevice(device, deviceView.getDeviceFollowHandler().isFollow(device));
        }
    }

    //метод который запускает изначальное положение карты
    public void startFocused() {
        mapController.focusedOnAllMap();
    }

    public DeviceView getDeviceView() {
        return deviceView;
    }
}
