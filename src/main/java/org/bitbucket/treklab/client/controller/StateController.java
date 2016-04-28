package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.info.Info;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.Position;
import org.bitbucket.treklab.client.model.InfoRow;
import org.bitbucket.treklab.client.model.InfoRowProperties;
import org.bitbucket.treklab.client.view.StateView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Этот контроллер отвечает за действия панели состояния
 */
public class StateController implements ContentController, StateView.StateHandler {

    private final StateView stateView;
    private static final InfoRowProperties prop = GWT.create(InfoRowProperties.class);
    private final ListStore<InfoRow> rowStore = new ListStore<>(prop.key());

    @Override
    public ContentPanel getView() {
        return stateView.getView();
    }

    @Override
    public void run() {

    }

    public StateController() {
        this.stateView = new StateView(rowStore);
    }

    public void fillGrid(final Device device) {
        stateView.getRowStore().clear();
        try {
            new PositionData().getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            if (positions.get(i).getDeviceId() == device.getId()) {
                                if (positions.get(i).getAddress() != null && !positions.get(i).getAddress().equals("")) {
                                    rowStore.add(new InfoRow(0, "Адрес", positions.get(i).getAddress()));
                                } else {
                                    rowStore.add(new InfoRow(0, "Адрес", ""));
                                }
                                if (positions.get(i).getDeviceTime() != null && !positions.get(i).getDeviceTime().equals("")) {
                                    Date tmp = new Date(positions.get(i).getDeviceTime());
                                    DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
                                    rowStore.add(new InfoRow(1, "Время устройства", fmt.format(tmp)));
                                }
                                if (positions.get(i).getAltitude() >= 0) {
                                    rowStore.add(new InfoRow(2, "Высота", positions.get(i).getAltitude() + " м"));
                                } else {
                                    rowStore.add(new InfoRow(2, "Высота", 0 + " м"));
                                }
                                rowStore.add(new InfoRow(3, "Долгота", String.valueOf(positions.get(i).getLongitude())));
                                rowStore.add(new InfoRow(4, "Широта", String.valueOf(positions.get(i).getLatitude())));
                                double speed = new BigDecimal(positions.get(i).getSpeed()).setScale(1, RoundingMode.UP).doubleValue();
                                rowStore.add(new InfoRow(5, "Скорость", speed + " км/ч"));
                                if (speed > 50) {
                                    Info.display("Overspeed!", "Allowed speed: 50 km/h. Current speed: " + speed + " km/h");
                                }
                                rowStore.add(new InfoRow(6, "Курс", positions.get(i).getCourse() + "°"));
                                if (positions.get(i).getProtocol() != null && !positions.get(i).getProtocol().equals("")) {
                                    rowStore.add(new InfoRow(7, "Протокол", positions.get(i).getProtocol()));
                                }
                                if (positions.get(i).getAttributes().getIp() != null && !positions.get(i).getAttributes().getIp().equals("")) {
                                    rowStore.add(new InfoRow(8, "IP", positions.get(i).getAttributes().getIp()));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getPriority()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getPriority() != 0*/) {
                                    rowStore.add(new InfoRow(9, "Приоритет", String.valueOf(positions.get(i).getAttributes().getPriority())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getSat()).equals("undefined")) {
                                    rowStore.add(new InfoRow(10, "Сат", String.valueOf(positions.get(i).getAttributes().getSat())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getEvent()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getEvent() != 0*/) {
                                    rowStore.add(new InfoRow(11, "Событие", String.valueOf(positions.get(i).getAttributes().getEvent())));
                                }
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

    public StateView getStateView() {
        return stateView;
    }

    /**
     * Этот метод пока не работает должным образом,
     * так как мы не можем объектам строк присваивать постоянно одни и те же ИД
     */
    public void update(final Device selectedItem) {
        try {
            new PositionData().getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            if (positions.get(i).getDeviceId() == selectedItem.getId()) {
                                // проходимся по всем существующим строкам начиная с первой (нулевой)
                                if (positions.get(i).getAddress() != null && !positions.get(i).getAddress().equals("")) {
                                    rowStore.update(new InfoRow(0, "Адрес", positions.get(i).getAddress()));
                                }
                                if (positions.get(i).getDeviceTime() != null && !positions.get(i).getDeviceTime().equals("")) {
                                    Date tmp = new Date(positions.get(i).getDeviceTime());
                                    DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
                                    rowStore.update(new InfoRow(1, "Время устройства", fmt.format(tmp)));
                                }
                                /** Обновление объектов реализовать пока не удалось.
                                 * Скорее всего причина ошибок в несоответствии их ID, которые генерируются COUNTER'ом */
                                if (positions.get(i).getAltitude() >= 0) {
                                    rowStore.update(new InfoRow(2, "Высота", positions.get(i).getAltitude() + " м"));
                                } else {
                                    rowStore.update(new InfoRow(2, "Высота", 0 + " м"));
                                }
                                rowStore.update(new InfoRow(3, "Долгота", String.valueOf(positions.get(i).getLongitude())));
                                rowStore.update(new InfoRow(4, "Широта", String.valueOf(positions.get(i).getLatitude())));
                                double speed = new BigDecimal(positions.get(i).getSpeed()).setScale(1, RoundingMode.UP).doubleValue();
                                stateView.getRowStore().update(new InfoRow(5, "Скорость", speed + " км/ч"));
                                if (speed > 50) {
                                    Info.display("Overspeed!", "Allowed speed: 50 km/h. Current speed: " + speed + " km/h");
                                }
                                rowStore.update(new InfoRow(6, "Курс", positions.get(i).getCourse() + "°"));
                                if (positions.get(i).getProtocol() != null && !positions.get(i).getProtocol().equals("")) {
                                    rowStore.update(new InfoRow(7, "Протокол", positions.get(i).getProtocol()));
                                }
                                if (positions.get(i).getAttributes().getIp() != null && !positions.get(i).getAttributes().getIp().equals("")) {
                                    rowStore.update(new InfoRow(8, "IP", positions.get(i).getAttributes().getIp()));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getPriority()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getPriority() != 0*/) {
                                    rowStore.update(new InfoRow(9, "Приоритет", String.valueOf(positions.get(i).getAttributes().getPriority())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getSat()).equals("undefined")) {
                                    rowStore.update(new InfoRow(10, "Сат", String.valueOf(positions.get(i).getAttributes().getSat())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getEvent()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getEvent() != 0*/) {
                                    rowStore.update(new InfoRow(11, "Событие", String.valueOf(positions.get(i).getAttributes().getEvent())));
                                }
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

    @Override
    public void onTabSelected(SelectionEvent<Widget> event) {
        stateView.getRowStore().clear();
        TabPanel tabPanel = (TabPanel) event.getSource();
        Widget w = event.getSelectedItem();
        TabItemConfig tab = tabPanel.getConfig(w);
        switch (tab.getText()) {
            case "Objects":
                stateView.getRowCM().setColumnHeader(0, new SafeHtml() {
                    private static final long serialVersionUID = 5328351854632677932L;

                    @Override
                    public String asString() {
                        return "Параметр";
                    }
                });
                break;
            case "Events":
                stateView.getRowCM().setColumnHeader(0, new SafeHtml() {
                    private static final long serialVersionUID = -6460540087293779203L;

                    @Override
                    public String asString() {
                        return "Время";
                    }
                });
                break;
        }
    }
}
