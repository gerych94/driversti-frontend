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
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.info.Info;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.InfoRow;
import org.bitbucket.treklab.client.model.InfoRowProperties;
import org.bitbucket.treklab.client.model.Position;
import org.bitbucket.treklab.client.model.demo.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DevicePropertiesDialog extends Composite {
    interface DevicePropertiesDialogUiBinder extends UiBinder<Widget, DevicePropertiesDialog> {
    }

    private static DevicePropertiesDialogUiBinder uiBinder = GWT.create(DevicePropertiesDialogUiBinder.class);

    @UiField
    Window window;

    public Window getWindow() {
        return window;
    }

    private DemoGroupProperties demoGroupProperties = GWT.create(DemoGroupProperties.class);
    ListStore<DemoGroup> demoGroupStore = new ListStore<>(demoGroupProperties.key());
    @UiField(provided = true)
    ComboBox<DemoGroup> demoGroupCombo;

    private DemoDriverProperties demoDriverProperties = GWT.create(DemoDriverProperties.class);
    ListStore<DemoDriver> demoDriverStore = new ListStore<>(demoDriverProperties.key());
    @UiField(provided = true)
    ComboBox<DemoDriver> demoDriverCombo;

    private DemoTrailerProperties demoTrailerProperties = GWT.create(DemoTrailerProperties.class);
    ListStore<DemoTrailer> demoTrailerStore = new ListStore<>(demoTrailerProperties.key());
    @UiField(provided = true)
    ComboBox<DemoTrailer> demoTrailerCombo;

    private DemoGPSProperties demoGPSProperties = GWT.create(DemoGPSProperties.class);
    ListStore<DemoGPS> demoGPSStore = new ListStore<>(demoGPSProperties.key());
    @UiField(provided = true)
    ComboBox<DemoGPS> demoGPSCombo;

    private DemoOdometerProperties demoOdometerProperties = GWT.create(DemoOdometerProperties.class);
    ListStore<DemoOdometer> demoOdometerStore = new ListStore<>(demoOdometerProperties.key());
    @UiField(provided = true)
    ComboBox<DemoOdometer> demoOdometerCombo;

    private DemoMotoProperties demoMotoProperties = GWT.create(DemoMotoProperties.class);
    ListStore<DemoMoto> demoMotoStore = new ListStore<>(demoMotoProperties.key());
    @UiField(provided = true)
    ComboBox<DemoMoto> demoMotoCombo;


    private DemoMapIconProperties demoMapIconProperties = GWT.create(DemoMapIconProperties.class);
    ListStore<DemoMapIcon> demoMapIconStore = new ListStore<>(demoMapIconProperties.key());
    @UiField(provided = true)
    ComboBox<DemoMapIcon> demoMapIconCombo;

    private DemoStoppedArrowProperties demoStoppedArrowProperties = GWT.create(DemoStoppedArrowProperties.class);
    ListStore<DemoStoppedArrow> demoStoppedArrowStore = new ListStore<>(demoStoppedArrowProperties.key());
    @UiField(provided = true)
    ComboBox<DemoStoppedArrow> demoStoppedArrowCombo;

    private DemoMovingArrowProperties demoMovingArrowProperties = GWT.create(DemoMovingArrowProperties.class);
    ListStore<DemoMovingArrow> demoMovingArrowStore = new ListStore<>(demoMovingArrowProperties.key());
    @UiField(provided = true)
    ComboBox<DemoMovingArrow> demoMovingArrowCombo;

    private DemoEngineArrowProperties demoEngineArrowProperties = GWT.create(DemoEngineArrowProperties.class);
    ListStore<DemoEngineArrow> demoEngineArrowStore = new ListStore<>(demoEngineArrowProperties.key());
    @UiField(provided = true)
    ComboBox<DemoEngineArrow> demoEngineArrowCombo;

    private DemoSOSArrowProperties demoSOSArrowProperties = GWT.create(DemoSOSArrowProperties.class);
    ListStore<DemoSOSArrow> demoSOSArrowStore = new ListStore<>(demoSOSArrowProperties.key());
    @UiField(provided = true)
    ComboBox<DemoSOSArrow> demoSOSArrowCombo;


    @UiField
    Grid<InfoRow> infoRowGrid;
    @UiField(provided = true)
    ListStore<InfoRow> infoRowStore;
    @UiField(provided = true)
    ColumnModel<InfoRow> infoRowCM;

    @UiField
    GroupingView<InfoRow> infoRowView;

    private static final InfoRowProperties prop = GWT.create(InfoRowProperties.class);

    public DevicePropertiesDialog(Device selectedItem, ListStore<Device> deviceStore) {

        this.demoGroupStore = new ListStore<>(demoGroupProperties.key());
        this.demoGroupStore.add(new DemoGroup("Group 1"));
        this.demoGroupStore.add(new DemoGroup("Group 2"));
        this.demoGroupStore.add(new DemoGroup("Group 3"));
        this.demoGroupCombo = new ComboBox<>(demoGroupStore, demoGroupProperties.nameLabel());

        this.demoDriverStore = new ListStore<>(demoDriverProperties.key());
        this.demoDriverStore.add(new DemoDriver("Вася"));
        this.demoDriverStore.add(new DemoDriver("Петя"));
        this.demoDriverStore.add(new DemoDriver("Ваня"));
        this.demoDriverCombo = new ComboBox<>(demoDriverStore, demoDriverProperties.nameLabel());

        this.demoTrailerStore = new ListStore<>(demoTrailerProperties.key());
        this.demoTrailerStore.add(new DemoTrailer("Trailer 1"));
        this.demoTrailerStore.add(new DemoTrailer("Trailer 2"));
        this.demoTrailerStore.add(new DemoTrailer("Trailer 3"));
        this.demoTrailerCombo = new ComboBox<>(demoTrailerStore, demoTrailerProperties.nameLabel());

        this.demoGPSStore = new ListStore<>(demoGPSProperties.key());
        this.demoGPSStore.add(new DemoGPS("GPS устройство 1"));
        this.demoGPSStore.add(new DemoGPS("GPS устройство 2"));
        this.demoGPSStore.add(new DemoGPS("GPS устройство 3"));
        this.demoGPSCombo = new ComboBox<>(demoGPSStore, demoGPSProperties.nameLabel());

        this.demoOdometerStore = new ListStore<>(demoOdometerProperties.key());
        this.demoOdometerStore.add(new DemoOdometer("GPS"));
        this.demoOdometerCombo = new ComboBox<>(demoOdometerStore, demoOdometerProperties.nameLabel());

        this.demoMotoStore = new ListStore<>(demoMotoProperties.key());
        this.demoMotoStore.add(new DemoMoto("ACC"));
        this.demoMotoCombo = new ComboBox<>(demoMotoStore, demoMotoProperties.nameLabel());


        this.demoMapIconStore = new ListStore<>(demoMapIconProperties.key());
        this.demoMapIconStore.add(new DemoMapIcon("Стрелка 1"));
        this.demoMapIconStore.add(new DemoMapIcon("Стрелка 2"));
        this.demoMapIconStore.add(new DemoMapIcon("Стрелка 3"));
        this.demoMapIconCombo = new ComboBox<>(demoMapIconStore, demoMapIconProperties.nameLabel());

        this.demoStoppedArrowStore = new ListStore<>(demoStoppedArrowProperties.key());
        this.demoStoppedArrowStore.add(new DemoStoppedArrow("Красный"));
        this.demoStoppedArrowStore.add(new DemoStoppedArrow("Зеленый"));
        this.demoStoppedArrowStore.add(new DemoStoppedArrow("Синий"));
        this.demoStoppedArrowCombo = new ComboBox<>(demoStoppedArrowStore, demoStoppedArrowProperties.nameLabel());

        this.demoMovingArrowStore = new ListStore<>(demoMovingArrowProperties.key());
        this.demoMovingArrowStore.add(new DemoMovingArrow("Красный"));
        this.demoMovingArrowStore.add(new DemoMovingArrow("Зеленый"));
        this.demoMovingArrowStore.add(new DemoMovingArrow("Синий"));
        this.demoMovingArrowCombo = new ComboBox<>(demoMovingArrowStore, demoMovingArrowProperties.nameLabel());

        this.demoEngineArrowStore = new ListStore<>(demoEngineArrowProperties.key());
        this.demoEngineArrowStore.add(new DemoEngineArrow("Off"));
        this.demoEngineArrowStore.add(new DemoEngineArrow("On"));
        this.demoEngineArrowCombo = new ComboBox<>(demoEngineArrowStore, demoEngineArrowProperties.nameLabel());

        this.demoSOSArrowStore = new ListStore<>(demoSOSArrowProperties.key());
        this.demoSOSArrowStore.add(new DemoSOSArrow("Off"));
        this.demoSOSArrowStore.add(new DemoSOSArrow("On"));
        this.demoSOSArrowCombo = new ComboBox<>(demoSOSArrowStore, demoSOSArrowProperties.nameLabel());


        this.infoRowStore = new ListStore<>(prop.key());

        ColumnConfig<InfoRow, String> colName = new ColumnConfig<>(prop.name(), 150, "Параметр");
        colName.setResizable(true);
        ColumnConfig<InfoRow, String> colValue = new ColumnConfig<>(prop.value(), 100, "Значение");
        colValue.setResizable(true);

        List<ColumnConfig<InfoRow, ?>> configList = new ArrayList<>();
        configList.add(colName);
        configList.add(colValue);

        this.infoRowCM = new ColumnModel<>(configList);
        fillGrid(selectedItem);

        uiBinder.createAndBindUi(this);

        this.infoRowView.setAutoExpandColumn(colValue);
        this.infoRowView.setStripeRows(true);
    }

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