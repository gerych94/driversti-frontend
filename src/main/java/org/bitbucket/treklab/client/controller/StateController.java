package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.info.Info;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.*;
import org.bitbucket.treklab.client.view.StateView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Этот контроллер отвечает за действия панели состояния
 */
public class StateController implements ContentController {

    private final StateView stateView;
    private static final PositionRowProperties prop = GWT.create(PositionRowProperties.class);
    private final ListStore<PositionRow> rowStore = new ListStore<>(prop.key());

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
                                    rowStore.add(new PositionRow(0, "Адрес", positions.get(i).getAddress()));
                                } else {
                                    rowStore.add(new PositionRow(0, "Адрес", ""));
                                }
                                if (positions.get(i).getDeviceTime() != null && !positions.get(i).getDeviceTime().equals("")) {
                                    Date tmp = new Date(positions.get(i).getDeviceTime());
                                    DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
                                    rowStore.add(new PositionRow(1, "Время устройства", fmt.format(tmp)));
                                }
                                if (positions.get(i).getAltitude() >= 0) {
                                    rowStore.add(new PositionRow(2, "Высота", positions.get(i).getAltitude() + " м"));
                                } else {
                                    rowStore.add(new PositionRow(2, "Высота", 0 + " м"));
                                }
                                rowStore.add(new PositionRow(3, "Долгота", String.valueOf(positions.get(i).getLongitude())));
                                rowStore.add(new PositionRow(4, "Широта", String.valueOf(positions.get(i).getLatitude())));
                                double speed = new BigDecimal(positions.get(i).getSpeed()).setScale(3, RoundingMode.UP).doubleValue();
                                rowStore.add(new PositionRow(5, "Скорость", speed + " км/ч"));
                                if (speed > 30) {
                                    Info.display("Overspeed!", "Allowed speed: 30 km/h. Current speed: " + speed + " km/h");
                                }
                                if (positions.get(i).getCourse() != 0) {
                                    rowStore.add(new PositionRow(6, "Курс", positions.get(i).getCourse() + "°"));
                                }
                                if (positions.get(i).getProtocol() != null && !positions.get(i).getProtocol().equals("")) {
                                    rowStore.add(new PositionRow(7, "Протокол", positions.get(i).getProtocol()));
                                }
                                if (positions.get(i).getAttributes().getIp() != null && !positions.get(i).getAttributes().getIp().equals("")) {
                                    rowStore.add(new PositionRow(8, "IP", positions.get(i).getAttributes().getIp()));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getPriority()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getPriority() != 0*/) {
                                    rowStore.add(new PositionRow(9, "Приоритет", String.valueOf(positions.get(i).getAttributes().getPriority())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getSat()).equals("undefined")) {
                                    rowStore.add(new PositionRow(10, "Сат", String.valueOf(positions.get(i).getAttributes().getSat())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getEvent()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getEvent() != 0*/) {
                                    rowStore.add(new PositionRow(11, "Событие", String.valueOf(positions.get(i).getAttributes().getEvent())));
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
                                    rowStore.update(new PositionRow(0, "Адрес", positions.get(i).getAddress()));
                                }
                                if (positions.get(i).getDeviceTime() != null && !positions.get(i).getDeviceTime().equals("")) {
                                    Date tmp = new Date(positions.get(i).getDeviceTime());
                                    DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd hh:mm:ss");
                                    rowStore.update(new PositionRow(1, "Время устройства", fmt.format(tmp)));
                                }
                                /** Обновление объектов реализовать пока не удалось.
                                 * Скорее всего причина ошибок в несоответствии их ID, которые генерируются COUNTER'ом */
                                if (positions.get(i).getAltitude() >= 0) {
                                    rowStore.update(new PositionRow(2, "Высота", positions.get(i).getAltitude() + " м"));
                                } else {
                                    rowStore.update(new PositionRow(2, "Высота", 0 + " м"));
                                }
                                rowStore.update(new PositionRow(3, "Долгота", String.valueOf(positions.get(i).getLongitude())));
                                rowStore.update(new PositionRow(4, "Широта", String.valueOf(positions.get(i).getLatitude())));
                                stateView.getRowStore().update(new PositionRow(5, "Скорость",
                                        new BigDecimal(positions.get(i).getSpeed()).setScale(3, RoundingMode.UP).doubleValue() + " км/ч"));
                                if (positions.get(i).getCourse() != 0) {
                                    if (rowStore.hasRecord(new PositionRow(6, "Курс", positions.get(i).getCourse() + "°"))) {
                                        rowStore.update(new PositionRow(6, "Курс", positions.get(i).getCourse() + "°"));
                                    } else {
                                        rowStore.add(new PositionRow(6, "Курс", positions.get(i).getCourse() + "°"));
                                    }
                                }
                                if (positions.get(i).getProtocol() != null && !positions.get(i).getProtocol().equals("")) {
                                    rowStore.update(new PositionRow(7, "Протокол", positions.get(i).getProtocol()));
                                }
                                if (positions.get(i).getAttributes().getIp() != null && !positions.get(i).getAttributes().getIp().equals("")) {
                                    rowStore.update(new PositionRow(8, "IP", positions.get(i).getAttributes().getIp()));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getPriority()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getPriority() != 0*/) {
                                    rowStore.update(new PositionRow(9, "Приоритет", String.valueOf(positions.get(i).getAttributes().getPriority())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getSat()).equals("undefined")) {
                                    rowStore.update(new PositionRow(10, "Сат", String.valueOf(positions.get(i).getAttributes().getSat())));
                                }
                                if (!String.valueOf(positions.get(i).getAttributes().getEvent()).equals("undefined") /*&&
                                        positions.get(i).getAttributes().getEvent() != 0*/) {
                                    rowStore.update(new PositionRow(11, "Событие", String.valueOf(positions.get(i).getAttributes().getEvent())));
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
}
