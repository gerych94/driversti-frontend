package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.info.Info;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.DeviceData;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.InfoRow;
import org.bitbucket.treklab.client.model.InfoRowProperties;
import org.bitbucket.treklab.client.model.Position;
import org.bitbucket.treklab.client.model.demo.*;
import org.bitbucket.treklab.client.util.LoggerHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DevicePropertiesDialog extends Composite {
    private static final InfoRowProperties prop = GWT.create(InfoRowProperties.class);
    private static final String className = DevicePropertiesDialog.class.getSimpleName();
    private static DevicePropertiesDialogUiBinder uiBinder = GWT.create(DevicePropertiesDialogUiBinder.class);
    @UiField
    Window window;
    @UiField
    TextField name;
    @UiField
    TextField imei;
    @UiField(provided = true)
    ComboBox<DemoGroup> demoGroupCombo;
    @UiField(provided = true)
    ComboBox<DemoDriver> demoDriverCombo;
    @UiField(provided = true)
    ComboBox<DemoTrailer> demoTrailerCombo;
    @UiField(provided = true)
    ComboBox<DemoGPS> demoGPSCombo;
    @UiField(provided = true)
    ComboBox<DemoOdometer> demoOdometerCombo;
    @UiField(provided = true)
    ComboBox<DemoMoto> demoMotoCombo;
    @UiField(provided = true)
    ComboBox<DemoMapIcon> demoMapIconCombo;
    @UiField(provided = true)
    ComboBox<DemoStoppedArrow> demoStoppedArrowCombo;
    @UiField(provided = true)
    ComboBox<DemoMovingArrow> demoMovingArrowCombo;
    @UiField(provided = true)
    ComboBox<DemoEngineArrow> demoEngineArrowCombo;
    @UiField(provided = true)
    ComboBox<DemoSOSArrow> demoSOSArrowCombo;
    // таблица и список для заполнения вкладки "ИНФО"
    @UiField
    Grid<InfoRow> infoRowGrid;
    @UiField(provided = true)
    ListStore<InfoRow> infoRowStore;
    @UiField(provided = true)
    ColumnModel<InfoRow> infoRowCM;
    @UiField
    GroupingView<InfoRow> infoRowView;
    private Device device;
    private ListStore<Device> deviceStore;
    // поочерёдно создаём списки для хранения данных комбобоксов
    private DemoGroupProperties demoGroupProperties = GWT.create(DemoGroupProperties.class);
    private ListStore<DemoGroup> demoGroupStore = new ListStore<>(demoGroupProperties.key());
    private DemoDriverProperties demoDriverProperties = GWT.create(DemoDriverProperties.class);
    private ListStore<DemoDriver> demoDriverStore = new ListStore<>(demoDriverProperties.key());
    private DemoTrailerProperties demoTrailerProperties = GWT.create(DemoTrailerProperties.class);
    private ListStore<DemoTrailer> demoTrailerStore = new ListStore<>(demoTrailerProperties.key());
    private DemoGPSProperties demoGPSProperties = GWT.create(DemoGPSProperties.class);
    private ListStore<DemoGPS> demoGPSStore = new ListStore<>(demoGPSProperties.key());
    private DemoOdometerProperties demoOdometerProperties = GWT.create(DemoOdometerProperties.class);
    private ListStore<DemoOdometer> demoOdometerStore = new ListStore<>(demoOdometerProperties.key());
    private DemoMotoProperties demoMotoProperties = GWT.create(DemoMotoProperties.class);
    private ListStore<DemoMoto> demoMotoStore = new ListStore<>(demoMotoProperties.key());
    private DemoMapIconProperties demoMapIconProperties = GWT.create(DemoMapIconProperties.class);
    private ListStore<DemoMapIcon> demoMapIconStore = new ListStore<>(demoMapIconProperties.key());
    private DemoStoppedArrowProperties demoStoppedArrowProperties = GWT.create(DemoStoppedArrowProperties.class);
    private ListStore<DemoStoppedArrow> demoStoppedArrowStore = new ListStore<>(demoStoppedArrowProperties.key());
    private DemoMovingArrowProperties demoMovingArrowProperties = GWT.create(DemoMovingArrowProperties.class);
    private ListStore<DemoMovingArrow> demoMovingArrowStore = new ListStore<>(demoMovingArrowProperties.key());
    private DemoEngineArrowProperties demoEngineArrowProperties = GWT.create(DemoEngineArrowProperties.class);
    private ListStore<DemoEngineArrow> demoEngineArrowStore = new ListStore<>(demoEngineArrowProperties.key());
    private DemoSOSArrowProperties demoSOSArrowProperties = GWT.create(DemoSOSArrowProperties.class);
    private ListStore<DemoSOSArrow> demoSOSArrowStore = new ListStore<>(demoSOSArrowProperties.key());

    public DevicePropertiesDialog(Device selectedItem, ListStore<Device> globalDeviceStore) {

        this.device = selectedItem;
        this.deviceStore = globalDeviceStore;

        // заполняем все комбобоксы данными
        demoGroupStore = new ListStore<>(demoGroupProperties.key());
        demoGroupCombo = new ComboBox<>(demoGroupStore, demoGroupProperties.nameLabel());
        demoGroupStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoGroup>() {
            @Override
            public void onAdd(StoreAddEvent<DemoGroup> event) {
                demoGroupCombo.setValue(demoGroupStore.get(0));
            }
        });
        demoGroupStore.add(new DemoGroup("Group 1"));
        demoGroupStore.add(new DemoGroup("Group 2"));
        demoGroupStore.add(new DemoGroup("Group 3"));

        demoDriverStore = new ListStore<>(demoDriverProperties.key());
        demoDriverCombo = new ComboBox<>(demoDriverStore, demoDriverProperties.nameLabel());
        demoDriverStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoDriver>() {
            @Override
            public void onAdd(StoreAddEvent<DemoDriver> event) {
                demoDriverCombo.setValue(demoDriverStore.get(0));
            }
        });
        demoDriverStore.add(new DemoDriver("Вася Васевич"));
        demoDriverStore.add(new DemoDriver("Петя Петевич"));
        demoDriverStore.add(new DemoDriver("Ваня Ваневич"));

        demoTrailerStore = new ListStore<>(demoTrailerProperties.key());
        demoTrailerCombo = new ComboBox<>(demoTrailerStore, demoTrailerProperties.nameLabel());
        demoTrailerStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoTrailer>() {
            @Override
            public void onAdd(StoreAddEvent<DemoTrailer> event) {
                demoTrailerCombo.setValue(demoTrailerStore.get(0));
            }
        });
        demoTrailerStore.add(new DemoTrailer("Trailer 1"));
        demoTrailerStore.add(new DemoTrailer("Trailer 2"));
        demoTrailerStore.add(new DemoTrailer("Trailer 3"));

        demoGPSStore = new ListStore<>(demoGPSProperties.key());
        demoGPSCombo = new ComboBox<>(demoGPSStore, demoGPSProperties.nameLabel());
        demoGPSStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoGPS>() {
            @Override
            public void onAdd(StoreAddEvent<DemoGPS> event) {
                demoGPSCombo.setValue(demoGPSStore.get(0));
            }
        });
        demoGPSStore.add(new DemoGPS("GPS устройство 1"));
        demoGPSStore.add(new DemoGPS("GPS устройство 2"));
        demoGPSStore.add(new DemoGPS("GPS устройство 3"));

        demoOdometerStore = new ListStore<>(demoOdometerProperties.key());
        demoOdometerCombo = new ComboBox<>(demoOdometerStore, demoOdometerProperties.nameLabel());
        demoOdometerStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoOdometer>() {
            @Override
            public void onAdd(StoreAddEvent<DemoOdometer> event) {
                demoOdometerCombo.setValue(demoOdometerStore.get(0));
            }
        });
        demoOdometerStore.add(new DemoOdometer("GPS"));

        demoMotoStore = new ListStore<>(demoMotoProperties.key());
        demoMotoCombo = new ComboBox<>(demoMotoStore, demoMotoProperties.nameLabel());
        demoMotoStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoMoto>() {
            @Override
            public void onAdd(StoreAddEvent<DemoMoto> event) {
                demoMotoCombo.setValue(demoMotoStore.get(0));
            }
        });
        demoMotoStore.add(new DemoMoto("ACC"));


        demoMapIconStore = new ListStore<>(demoMapIconProperties.key());
        demoMapIconCombo = new ComboBox<>(demoMapIconStore, demoMapIconProperties.nameLabel());
        demoMapIconStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoMapIcon>() {
            @Override
            public void onAdd(StoreAddEvent<DemoMapIcon> event) {
                demoMapIconCombo.setValue(demoMapIconStore.get(0));
            }
        });
        demoMapIconStore.add(new DemoMapIcon("Стрелка 1"));
        demoMapIconStore.add(new DemoMapIcon("Стрелка 2"));
        demoMapIconStore.add(new DemoMapIcon("Стрелка 3"));

        demoStoppedArrowStore = new ListStore<>(demoStoppedArrowProperties.key());
        demoStoppedArrowCombo = new ComboBox<>(demoStoppedArrowStore, demoStoppedArrowProperties.nameLabel());
        demoStoppedArrowStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoStoppedArrow>() {
            @Override
            public void onAdd(StoreAddEvent<DemoStoppedArrow> event) {
                demoStoppedArrowCombo.setValue(demoStoppedArrowStore.get(0));
            }
        });
        demoStoppedArrowStore.add(new DemoStoppedArrow("Красный"));
        demoStoppedArrowStore.add(new DemoStoppedArrow("Зеленый"));
        demoStoppedArrowStore.add(new DemoStoppedArrow("Синий"));

        demoMovingArrowStore = new ListStore<>(demoMovingArrowProperties.key());
        demoMovingArrowCombo = new ComboBox<>(demoMovingArrowStore, demoMovingArrowProperties.nameLabel());
        demoMovingArrowStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoMovingArrow>() {
            @Override
            public void onAdd(StoreAddEvent<DemoMovingArrow> event) {
                demoMovingArrowCombo.setValue(demoMovingArrowStore.get(0));
            }
        });
        demoMovingArrowStore.add(new DemoMovingArrow("Красный"));
        demoMovingArrowStore.add(new DemoMovingArrow("Зеленый"));
        demoMovingArrowStore.add(new DemoMovingArrow("Синий"));

        demoEngineArrowStore = new ListStore<>(demoEngineArrowProperties.key());
        demoEngineArrowCombo = new ComboBox<>(demoEngineArrowStore, demoEngineArrowProperties.nameLabel());
        demoEngineArrowStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoEngineArrow>() {
            @Override
            public void onAdd(StoreAddEvent<DemoEngineArrow> event) {
                demoEngineArrowCombo.setValue(demoEngineArrowStore.get(0));
            }
        });
        demoEngineArrowStore.add(new DemoEngineArrow("Off"));
        demoEngineArrowStore.add(new DemoEngineArrow("On"));

        demoSOSArrowStore = new ListStore<>(demoSOSArrowProperties.key());
        demoSOSArrowCombo = new ComboBox<>(demoSOSArrowStore, demoSOSArrowProperties.nameLabel());
        demoSOSArrowStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<DemoSOSArrow>() {
            @Override
            public void onAdd(StoreAddEvent<DemoSOSArrow> event) {
                demoSOSArrowCombo.setValue(demoSOSArrowStore.get(0));
            }
        });
        demoSOSArrowStore.add(new DemoSOSArrow("Off"));
        demoSOSArrowStore.add(new DemoSOSArrow("On"));

        // заполняем вкладку "ИНФО"
        infoRowStore = new ListStore<>(prop.key());

        ColumnConfig<InfoRow, String> colName = new ColumnConfig<>(prop.name(), 150, "Параметр");
        colName.setResizable(true);
        ColumnConfig<InfoRow, String> colValue = new ColumnConfig<>(prop.value(), 100, "Значение");
        colValue.setResizable(true);

        List<ColumnConfig<InfoRow, ?>> configList = new ArrayList<>();
        configList.add(colName);
        configList.add(colValue);

        infoRowCM = new ColumnModel<>(configList);
        fillGrid(selectedItem);

        uiBinder.createAndBindUi(this);

        infoRowView.setAutoExpandColumn(colValue);
        infoRowView.setStripeRows(true);

        name.setText(selectedItem.getName());
        imei.setText(selectedItem.getUniqueId());
    }

    public Window getWindow() {
        return window;
    }

    /**
     * Метод для заполнения вкладки "ИНФО" данными
     *
     * @param device - передаём устройство, данные которого заполняют таблицу
     */
    private void fillGrid(final Device device) {
        try {
            new PositionData().getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            if (positions.get(i).getDeviceId() == device.getId()) {
                                if (positions.get(i).getAddress() != null && !positions.get(i).getAddress().equals("")) {
                                    infoRowStore.add(new InfoRow(0, "Адрес", positions.get(i).getAddress()));
                                } else {
                                    infoRowStore.add(new InfoRow(0, "Адрес", ""));
                                }
                                if (positions.get(i).getDeviceTime() != null && !positions.get(i).getDeviceTime().equals("")) {
                                    Date tmp = new Date(positions.get(i).getDeviceTime());
                                    DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
                                    infoRowStore.add(new InfoRow(1, "Время устройства", fmt.format(tmp)));
                                }
                                if (positions.get(i).getAltitude() >= 0) {
                                    infoRowStore.add(new InfoRow(2, "Высота", positions.get(i).getAltitude() + " м"));
                                } else {
                                    infoRowStore.add(new InfoRow(2, "Высота", 0 + " м"));
                                }
                                infoRowStore.add(new InfoRow(3, "Долгота", String.valueOf(positions.get(i).getLongitude())));
                                infoRowStore.add(new InfoRow(4, "Широта", String.valueOf(positions.get(i).getLatitude())));
                                double speed = new BigDecimal(positions.get(i).getSpeed()).setScale(1, RoundingMode.UP).doubleValue();
                                infoRowStore.add(new InfoRow(5, "Скорость", speed + " км/ч"));
                                if (speed > 50) {
                                    Info.display("Overspeed!", "Allowed speed: 50 km/h. Current speed: " + speed + " km/h");
                                }
                                infoRowStore.add(new InfoRow(6, "Курс", positions.get(i).getCourse() + "°"));
                                if (positions.get(i).getProtocol() != null && !positions.get(i).getProtocol().equals("")) {
                                    infoRowStore.add(new InfoRow(7, "Протокол", positions.get(i).getProtocol()));
                                }
                                if (positions.get(i).getAttributes().getIp() != null && !positions.get(i).getAttributes().getIp().equals("")) {
                                    infoRowStore.add(new InfoRow(8, "IP", positions.get(i).getAttributes().getIp()));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getPriority()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getPriority() != 0*/) {
                                    infoRowStore.add(new InfoRow(9, "Приоритет", String.valueOf(positions.get(i).getAttributes().getPriority())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getSat()).equals("undefined")) {
                                    infoRowStore.add(new InfoRow(10, "Сат", String.valueOf(positions.get(i).getAttributes().getSat())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getEvent()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getEvent() != 0*/) {
                                    infoRowStore.add(new InfoRow(11, "Событие", String.valueOf(positions.get(i).getAttributes().getEvent())));
                                }
                                infoRowStore.add(new InfoRow(12, "Параметры", "-"));
                            }
                        }
                    } else {
                        new AlertMessageBox("Error", "Can't get position from server").show();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        window.show();
    }

    private void hide() {
        window.hide();
    }

    /**
     * Обработчик нажатия на кнопку "СОХРАНИТЬ"
     *
     * @param event - в данном случае не используем
     */
    @UiHandler("saveButton")
    public void onSaveClicked(SelectEvent event) {

        if (changed()) {
            final DeviceData deviceData = new DeviceData();
            Device tempDevice = Device.getClone(device);
            tempDevice.setName(name.getText());
            tempDevice.setUniqueId(imei.getText());
            try {
                deviceData.updateDevice(tempDevice, new BaseRequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        if (200 == response.getStatusCode()) {
                            JsArray<Device> array = JsonUtils.safeEval("[" + response.getText() + "]");
                            LoggerHelper.log(className, response.getText());
                            device = array.get(0);
                            deviceStore.update(device);
                        } else {
                            LoggerHelper.log(className, "Status code: " + response.getStatusCode());
                        }
                    }
                });
            } catch (RequestException e) {
                e.printStackTrace();
                LoggerHelper.log(className, " Can't get data from server", e);
            }
        }
        hide();
    }

    private boolean changed() {
        LoggerHelper.log(className, "changed()");
        return !(name.getText().equals(device.getName()) &&
                imei.getText().equals(device.getUniqueId()));
    }

    /**
     * Обработчик нажатия на кнопку "ОТМЕНА"
     *
     * @param event - в данном случае не используем
     */
    @UiHandler("cancelButton")
    public void onCancelClicked(SelectEvent event) {
        hide();
    }

    interface DevicePropertiesDialogUiBinder extends UiBinder<Widget, DevicePropertiesDialog> {
    }
}