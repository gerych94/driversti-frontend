package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.Viewport;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.Position;
import org.bitbucket.treklab.client.util.DateHelper;
import org.bitbucket.treklab.client.view.MapView;
import org.discotools.gwt.leaflet.client.Options;
import org.discotools.gwt.leaflet.client.layers.vector.Polyline;
import org.discotools.gwt.leaflet.client.layers.vector.PolylineOptions;
import org.discotools.gwt.leaflet.client.marker.Marker;
import org.discotools.gwt.leaflet.client.types.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MapController {
    private final MapView mapView; // объект, отвечающий за визуальное отображение
    private final PositionData positionData; // объект, отвечающий получение данных Position
    private HashMap<Integer, DeviceFollowState> deviceHashMap = new HashMap<>();
    private HashMap<Integer, LatLng> previousPositionMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Polyline>> devicePolyLineHashMap = new HashMap<>();
    private PolylineOptions polylineOptions;
    Timer timer = null;

    public Viewport getView() {
        return mapView.getView();
    }

    public MapController() {
        mapView = new MapView();
        positionData = new PositionData();
    }

    private class DeviceFollowState {
        boolean state = true;
    }

    //метод который зумирует к девайсу при нажатии на него в таблице
    public void focusedOnDevice(final Device device) {
        try {
            positionData.getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // получаем массив объектов Position
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        // перебираем массив и ищем объект Position соответствующий выбранному device
                        for (int i = 0; i < positions.length(); i++) {
                            if (device.getId() == positions.get(i).getDeviceId()) {
                                // получаем необходимый Position и вытягиваем координаты
                                final Position devicePosition = positions.get(i);
                                LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
                                // обновление координат маркера устройства и центрирование карты на выбранном устройстве
                                // TODO: 13.04.2016 проверить нужен ли следующий метод
                                mapView.getMarkers().get(device.getId()).setLatLng(latLng);
                                mapView.getMap().setView(latLng, 16, true);
                            }
                        }
                    } else {
                        // отображение сообщения об ошибке получения данных
                        new AlertMessageBox("Position Error", "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: " + device.getLastUpdate()).show();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    //метод который рисует маркер
    public void setDeviceMarker(final Device device) {
        try {
            // создаём запрос на сервер для получения последних объектов Position для всех девайсов
            positionData.getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // в случае успешного запроса получаем массив объектов Position
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        // перебираем массив и ищем объект Position соответствующий выбранному device
                        for (int i = 0; i < positions.length(); i++) {
                            if (device.getId() == positions.get(i).getDeviceId()) {
                                final Position devicePosition = positions.get(i);
                                LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
                                // получаем маркер, который соответствуем выбранному Device
                                Marker marker = mapView.getMarkers().get(device.getId());
                                // если он null - создаём новый и добавляем в HashMap
                                if (marker == null) {
                                    marker = new Marker(latLng, new Options());
                                    mapView.getMarkers().put(device.getId(), marker);
                                } else {
                                    // иначе обновляем координаты
                                    marker.setLatLng(latLng);
                                }
                                // отображаем маркер на карте
                                marker.addTo(mapView.getMap());
                            }
                        }
                    } else {
                        new AlertMessageBox("Position Error", "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: " + device.getLastUpdate()).show();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    public void setStartPosition(final Device device) {
        if (previousPositionMap.get(device.getId()) == null) {
            previousPositionMap.put(device.getId(), mapView.getMarkers().get(device.getId()).getLatLng());
        }
        if (devicePolyLineHashMap.get(device.getId()) == null) {
            devicePolyLineHashMap.put(device.getId(), new ArrayList<Polyline>());
        }
        polylineOptions = getPolyLineOptions();
    }

    public void drawRoad(final Device device, Date previousDate, Date currentDate) {
        try {
            setDeviceMarker(device);
            // создаём запрос на сервер для получения последних объектов Position для всех девайсов
            positionData.getPositions(device, previousDate, currentDate, new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(device.getId());
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());

                        LatLng previousPosition = previousPositionMap.get(device.getId());
                        LatLng currentPosition;
                        Polyline poly;
                        if (positions.length() > 1) {
                            for (int i = 0; i < positions.length(); i++) {
                                Position pos = positions.get(i);
                                currentPosition = new LatLng(pos.getLatitude(), pos.getLongitude());
                                poly = new Polyline(new LatLng[]{previousPosition, currentPosition}, polylineOptions);
                                poly.addTo(mapView.getMap());
                                polylineArrayList.add(poly);
                                previousPosition = currentPosition;
                            }
                        }
                        currentPosition = mapView.getMarkers().get(device.getId()).getLatLng();
                        if (!currentPosition.equals(previousPosition)) {
                            poly = new Polyline(new LatLng[]{previousPosition, currentPosition}, polylineOptions);
                            poly.addTo(mapView.getMap());
                            polylineArrayList.add(poly);
                            previousPosition = currentPosition;
                        }
                        previousPositionMap.put(device.getId(), previousPosition);
                    } else {
                        new AlertMessageBox("Position Error", "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: " + device.getLastUpdate()).show();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    //метод который убирает маркер
    public void removeDeviceMarker(final Device device) {
        DeviceFollowState deviceFollowState = deviceHashMap.get(device.getId());
        deviceFollowState.state = false;
        mapView.getMap().removeLayer(mapView.getMarkers().get(device.getId()));
        refocusedDevice(device, false);
    }

    public void removeWay(final Device device) {
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(device.getId());
        // new AlertMessageBox("Time","time3="+polylineArrayList+ " layer="+ mapView.getMap().hasLayer(polylineArrayList.get(0))).show();
        if (polylineArrayList != null) {
            for (Polyline polyline : polylineArrayList) {
                mapView.getMap().removeLayer(polyline);
            }
            polylineArrayList.clear();
        }
        previousPositionMap.remove(device.getId());
    }

    //метод который фокусирует стартовое положение карты
    public void focusedOnAllMap() {
        try {
            positionData.getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        int startZoom = 14;
                        int counter = 0;
                        double bufLat = 0;
                        double bufLong = 0;
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            final Position devicePosition = positions.get(i);
                            bufLat += devicePosition.getLatitude();
                            bufLong += devicePosition.getLongitude();
                            DeviceFollowState deviceFollowState = new DeviceFollowState();
                            deviceFollowState.state = false;
                            deviceHashMap.put(devicePosition.getDeviceId(), deviceFollowState);
                        }

                        int countOfFollow = positions.length();
                        bufLat /= (double) countOfFollow;
                        bufLong /= (double) countOfFollow;
                        LatLng latLng = new LatLng(bufLat, bufLong);
                        mapView.getMap().setView(latLng, startZoom, true);
                        while (counter != positions.length()) {
                            counter = 0;
                            mapView.getMap().setView(latLng, startZoom, true);
                            //  mapView.getMap().setZoom(startZoom);
                            for (int i = 0; i < positions.length(); i++) {
                                final Position devicePosition = positions.get(i);
                                LatLng latLng2 = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
                                if (mapView.getMap().getBounds().contains(latLng2)) {
                                    counter++;
                                }
                            }
                            startZoom--;
                        }
                        // mapView.getMap().setView(latLng,startZoom,true);
                    } else {
                        new AlertMessageBox("Position Error", "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: "/*device.getLastUpdate()*/).show();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    public void refocusedDevice(final Device device, final boolean flag) {
        try {
            positionData.getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        int startZoom = 15;
                        int counter = 0;
                        double bufLat = 0;
                        double bufLong = 0;
                        int countOfFollow = 0;
                        DeviceFollowState deviceFollowState = deviceHashMap.get(device.getId());
                        deviceFollowState.state = flag;
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            if (deviceHashMap.get(positions.get(i).getDeviceId()).state) {
                                bufLat += positions.get(i).getLatitude();
                                bufLong += positions.get(i).getLongitude();
                                countOfFollow++;
                            }
                        }
                        if (countOfFollow == 0) {
                            mapView.getMap().setView(mapView.getCenterLatLng(), 6, true);
                        } else {
                            bufLat /= (double) countOfFollow;
                            bufLong /= (double) countOfFollow;
                            LatLng latLng = new LatLng(bufLat, bufLong);
                            mapView.getMap().setView(latLng, startZoom, true);
                            while (counter != countOfFollow) {
                                counter = 0;
                                mapView.getMap().setView(latLng, startZoom, true);
                                //  mapView.getMap().setZoom(startZoom);
                                for (int i = 0; i < positions.length(); i++) {
                                    final Position devicePosition = positions.get(i);
                                    if (deviceHashMap.get(devicePosition.getDeviceId()).state) {
                                        LatLng latLng2 = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
                                        if (mapView.getMap().getBounds().contains(latLng2)) {
                                            counter++;
                                        }
                                    }
                                }
                                startZoom--;
                            }
                        }
                        if (flag) {
                            setDeviceMarker(device);
                            setStartPosition(device);
                            final Date[] previousDate = {null};
                            timer = new Timer() {
                                @Override
                                public void run() {
                                    Date currentDate = DateHelper.getUTCDate();
                                    if (previousDate[0] != null) {
                                        drawRoad(device, previousDate[0], currentDate);
                                    }
                                    previousDate[0] = currentDate;
                                    scheduleRepeating(10000);
                                }
                            };
                            timer.scheduleRepeating(1);
                        } else {
                            removeWay(device);
                            timer.cancel();
                        }
                    } else {
                        new AlertMessageBox("Position Error", "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: " + device.getLastUpdate()).show();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private PolylineOptions getPolyLineOptions() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.setOpacity(0.3);
        polylineOptions.setWeight(4);
        return polylineOptions;
    }
}
