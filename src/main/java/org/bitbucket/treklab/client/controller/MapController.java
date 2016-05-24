package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.Viewport;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.Geofence;
import org.bitbucket.treklab.client.model.Position;
import org.bitbucket.treklab.client.util.LoggerHelper;
import org.bitbucket.treklab.client.util.Observable;
import org.bitbucket.treklab.client.util.Observer;
import org.bitbucket.treklab.client.util.ServerDataHolder;
import org.bitbucket.treklab.client.view.MapView;
import org.discotools.gwt.leaflet.client.Options;
import org.discotools.gwt.leaflet.client.layers.vector.Polyline;
import org.discotools.gwt.leaflet.client.layers.vector.PolylineOptions;
import org.discotools.gwt.leaflet.client.marker.Marker;
import org.discotools.gwt.leaflet.client.types.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MapController implements Observer {
    private final MapView mapView; // объект, отвечающий за визуальное отображение
    private final PositionData positionData; // объект, отвечающий получение данных Position
    private HashMap<Integer, Boolean> deviceHashMapFollow = new HashMap<>();
    private HashMap<Integer, LatLng> previousPositionMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Polyline>> devicePolyLineHashMap = new HashMap<>();
    private PolylineOptions polylineOptions;

    private final Observable observable;

    private static final String POSITIONS_KEY = "positions";
    private static final String DEVICES_KEY = "devices";
    private HashMap<Integer, Position> markerPosition = new HashMap<>();
    private HashMap<Integer, Boolean> flagMap = new HashMap<>();

    private HashMap<Integer, Boolean> startDrawFlag = new HashMap<>();
    private ListStore<Geofence> geofenceStore;

    private static final String className = MapController.class.getSimpleName();


    public Viewport getView() {
        return mapView.getView();
    }

    public MapController(ListStore<Geofence> globalGeofenceStore, ServerDataHolder instance) {
        this.geofenceStore = globalGeofenceStore;
        mapView = new MapView(globalGeofenceStore);
        positionData = new PositionData();
        this.observable = instance;
        observable.registerObserver(this);
    }

    @Override
    public void update(String key, String value) {
        if (key.equals(POSITIONS_KEY)) {
            //перебор координат всех девайсов которые пришли от сервера
            JsArray<Position> positions = JsonUtils.safeEval(value);
            LoggerHelper.log(className, " " + value);
            for (int i = 0; i < positions.length(); i++) {
                Position position = positions.get(i);
                //каждый раз сохрянеться позиция маркера что бы знать где последний раз был девайс
                markerPosition.put(position.getDeviceId(), position);
                //если активирован флаг рисования для этого девайса начинаеться прорисовка маршрута
                if (startDrawFlag.get(position.getDeviceId()) == null) {
                    startDrawFlag.put(position.getDeviceId(), false);
                } else if (startDrawFlag.get(position.getDeviceId())) {
                    drawRoad(position, position.getDeviceId());
                }
            }
        }
    }


    //метод который зумирует к девайсу при нажатии на него в таблице
    public void focusedOnDevice(final Device device) {
        try {
            positionData.getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            if (device.getId() == positions.get(i).getDeviceId()) {
                                final Position devicePosition = positions.get(i);
                                LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
                                mapView.getMap().setView(latLng, 15, true);
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

    //метод который инициализирует колекцию для сохранения всего маршрута по которому двигался девайс
    public void setStartPosition(int deviceId) {
        if (devicePolyLineHashMap.get(deviceId) == null) {
            devicePolyLineHashMap.put(deviceId, new ArrayList<Polyline>());
        }
        polylineOptions = getPolyLineOptions();
    }

    //метод корторый отвечает за прорисовку маршрута
    public void drawRoad(Position devicePosition, int deviceId) {
        LoggerHelper.log(className, " idPosition " + devicePosition.getId() + " IdDevice" + devicePosition.getDeviceId());
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(deviceId);
        LatLng previous = previousPositionMap.get(deviceId);
        Position position = markerPosition.get(deviceId);
        Marker marker = mapView.getMarkers().get(deviceId);
        if (previous == null) {
            previous = new LatLng(position.getLatitude(), position.getLongitude());
        }
        LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
        if (!previous.equals(latLng)) {
            marker.setLatLng(latLng);
            if (!flagMap.get(deviceId)) {
                marker.addTo(mapView.getMap());
            }
            Polyline poly = new Polyline(new LatLng[]{previous, latLng}, polylineOptions);
            if (!flagMap.get(deviceId)) {
                poly.addTo(mapView.getMap());
            }
            polylineArrayList.add(poly);
            previous = latLng;
        }
        previousPositionMap.put(deviceId, previous);
    }

    //метод который убирает маркер
    public void removeDeviceMarker(final Device device) {
        deviceHashMapFollow.put(device.getId(), false);
        mapView.getMap().removeLayer(mapView.getMarkers().get(device.getId()));
    }

    //метод который убирает нарисованный маршрут
    public void removeWay(final Device device) {
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(device.getId());
        if (polylineArrayList != null) {
            for (Polyline polyline : polylineArrayList) {
                mapView.getMap().removeLayer(polyline);
            }
        }
        deviceHashMapFollow.put(device.getId(), false);
        if (!flagMap.get(device.getId())) {
            startDrawFlag.put(device.getId(), false);
            polylineArrayList.clear();
            previousPositionMap.remove(device.getId());
        }
    }

    //метод который фокусирует стартовое положение карты
    public void focusedOnAllMap() {
        try {
            positionData.getPositions(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        int startZoom = 13;
                        int counter = 0;
                        double bufLat = 0;
                        double bufLong = 0;
                        JsArray<Position> positions = JsonUtils.safeEval(response.getText());
                        for (int i = 0; i < positions.length(); i++) {
                            final Position devicePosition = positions.get(i);
                            markerPosition.put(positions.get(i).getDeviceId(), devicePosition);
                            // startDrawFlag.put(devicePosition.getDeviceId(), false);
                            LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
//                            IconOptions iconOptions=new IconOptions();
//                            iconOptions.setIconUrl(resources.getMarkerIconRed().getSafeUri().asString());
//                            Icon icon=new Icon(iconOptions);
//                            MarkerOptions markerOptions=new MarkerOptions();
//                            markerOptions.setIcon(icon);
//                            Marker marker = new Marker(latLng,markerOptions);
//                            marker.setIcon(icon);
//                            marker.setOptions(new Options());
                            Marker marker = new Marker(latLng, new Options());
                            //  marker.setOptions(markerOptions);
                            mapView.getMarkers().put(positions.get(i).getDeviceId(), marker);
                            marker.addTo(mapView.getMap());
                            bufLat += devicePosition.getLatitude();
                            bufLong += devicePosition.getLongitude();
                            deviceHashMapFollow.put(devicePosition.getDeviceId(), false);
                        }
                        int countOfFollow = positions.length();
                        bufLat /= (double) countOfFollow;
                        bufLong /= (double) countOfFollow;
                        LatLng latLng = new LatLng(bufLat, bufLong);
                        mapView.getMap().setView(latLng, startZoom, true);
                        while (counter != positions.length()) {
                            counter = 0;
                            mapView.getMap().setView(latLng, startZoom, true);
                            for (int i = 0; i < positions.length(); i++) {
                                final Position devicePosition = positions.get(i);
                                LatLng latLng2 = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
                                if (mapView.getMap().getBounds().contains(latLng2)) {
                                    counter++;
                                }
                            }
                            startZoom--;
                        }
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

    // метод который занимаеться рефокусом карты в зависимости от выбранных чекбоксов follow
    public void refocusedDevice(final Device device, final boolean flag) {
        int startZoom = mapView.getMap().getZoom();
        int counter = 0;
        double bufLat = 0;
        double bufLong = 0;
        int countOfFollow = 0;
        deviceHashMapFollow.put(device.getId(), flag);
        Set<Integer> positionSet = markerPosition.keySet();
        for (Integer i : positionSet) {
            Position position = markerPosition.get(i);
            if (deviceHashMapFollow.get(position.getDeviceId())) {
                bufLat += position.getLatitude();
                bufLong += position.getLongitude();
                countOfFollow++;
            }
        }
        if (countOfFollow == 0) {
            mapView.getMap().setView(mapView.getCenterLatLng(), startZoom, true);
        } else {
            bufLat /= (double) countOfFollow;
            bufLong /= (double) countOfFollow;
            LatLng latLng = new LatLng(bufLat, bufLong);
            mapView.getMap().setView(latLng, startZoom, true);
            while (counter != countOfFollow) {
                counter = 0;
                mapView.getMap().setView(latLng, startZoom, true);
                for (Integer i : positionSet) {
                    Position position = markerPosition.get(i);
                    if (deviceHashMapFollow.get(position.getDeviceId())) {
                        LatLng latLng2 = new LatLng(position.getLatitude(), position.getLongitude());
                        if (mapView.getMap().getBounds().contains(latLng2)) {
                            counter++;
                        }
                    }
                }
                startZoom--;
            }
        }
    }

    //  настройки для линии которой рисуется маршрут
    private PolylineOptions getPolyLineOptions() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.setOpacity(0.3);
        polylineOptions.setWeight(4);
        return polylineOptions;
    }

    // метод который вызываеться при установке чекбокса visible в false при включённом follow
    public void setFlag(Device device) {
        flagMap.put(device.getId(), true);
        // flag = true;
        removeDeviceMarker(device);
        removeWay(device);
    }

    // метод который отвечает за прорисовку истории маршрута
    public void drawHistory(Device device) {
        flagMap.put(device.getId(), false);
        // flag = false;
        refocusedDevice(device, true);
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(device.getId());
        for (Polyline polyline : polylineArrayList) {
            polyline.addTo(mapView.getMap());
        }
    }


    // метод который отвечает за установку маркера на карту
    public void drawDeviceMarker(final Device device) {
        Position devicePosition = markerPosition.get(device.getId());
        LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
        Marker marker = mapView.getMarkers().get(device.getId());
        marker.setLatLng(latLng);
        marker.addTo(mapView.getMap());
    }

    //метод который устанавливает флаг для старта прорисовки маршрута
    public void setStartDrawFlag(Device device) {
        flagMap.put(device.getId(), false);
        drawDeviceMarker(device);
        refocusedDevice(device, true);
        setStartPosition(device.getId());
        startDrawFlag.put(device.getId(), true);
    }
}
