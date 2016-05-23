package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.ListStore;
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

import java.util.*;

public class MapController implements Observer {
    private static final String POSITIONS_KEY = "positions";
    private static final String DEVICES_KEY = "devices";
    private static final String className = MapController.class.getSimpleName();
    private final MapView mapView; // объект, отвечающий за визуальное отображение
    private final PositionData positionData; // объект, отвечающий получение данных Position
    private final Observable observable;
    HashMap<Integer, Date> dateMap = new HashMap<>();
    HashMap<Integer, Boolean> flagMap = new HashMap<>();
    private HashMap<Integer, DeviceFollowState> deviceHashMapFollow = new HashMap<>();
    private HashMap<Integer, LatLng> previousPositionMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Polyline>> devicePolyLineHashMap = new HashMap<>();
    private PolylineOptions polylineOptions;
    private ListStore<Geofence> geofenceStore;
    private HashMap<Integer, ArrayList<Position>> devicePolylineHashMapInTime = new HashMap<>();
    private Set<Integer> hashSetPosition = new HashSet<>();
    private HashMap<Integer, Position> markerPosition = new HashMap<>();
    private int previousPos;
    private Date date;
    private boolean flag;
    private HashMap<Integer, Boolean> startDrawFlag = new HashMap<>();

    public MapController(ServerDataHolder instance, ListStore<Geofence> globalGeofenceStore) {
        this.geofenceStore = globalGeofenceStore;
        mapView = new MapView(globalGeofenceStore);
        positionData = new PositionData();
        this.observable = instance;
        observable.registerObserver(this);
    }

    public Viewport getView() {
        return mapView.getView();
    }

    @Override
    public void update(String key, String value) {
        if (key.equals(POSITIONS_KEY)) {
            JsArray<Position> positions = JsonUtils.safeEval(value);
            Position position = positions.length() > 1 ? positions.get(positions.length() - 1) : positions.get(0);
            int deviceId = position.getDeviceId();
            markerPosition.put(deviceId, position);
            if (startDrawFlag.get(deviceId)) {
                drawRoad(positions, deviceId);
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
                        LoggerHelper.log(className, "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: " + device.getLastUpdate());
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    //метод который рисует маркер
    public void setDeviceMarker(final Device device) {
        Position devicePosition = markerPosition.get(device.getId());
        LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
        Marker marker = mapView.getMarkers().get(device.getId());
        marker.setLatLng(latLng);
        if (!flag) {
            marker.addTo(mapView.getMap());
        }
    }

    public void setStartPosition(int deviceId) {
        if (devicePolyLineHashMap.get(deviceId) == null) {
            devicePolyLineHashMap.put(deviceId, new ArrayList<Polyline>());
        }
        polylineOptions = getPolyLineOptions();
    }

    public void drawRoad(JsArray<Position> positions, int deviceId) {
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(deviceId);
        LatLng previous = previousPositionMap.get(deviceId);
        Position position = markerPosition.get(deviceId);
        Marker marker = mapView.getMarkers().get(deviceId);
        int length = positions.length();
        if (previous == null) {
            previous = new LatLng(position.getLatitude(), position.getLongitude());
        }
        LatLng latLng = new LatLng(positions.get(length - 1).getLatitude(), positions.get(length - 1).getLongitude());
        if (positions.length() > 1) {
            marker.setLatLng(latLng);
            marker.addTo(mapView.getMap());
            for (int i = 0; i < length; i++) {
                LatLng current = new LatLng(positions.get(i).getLatitude(), positions.get(i).getLongitude());
                Polyline poly = new Polyline(new LatLng[]{previous, current}, polylineOptions);
                poly.addTo(mapView.getMap());
                polylineArrayList.add(poly);
                previous = current;
            }
        }
        if (!previous.equals(latLng)) {
            marker.setLatLng(latLng);
            marker.addTo(mapView.getMap());
            Polyline poly = new Polyline(new LatLng[]{previous, latLng}, polylineOptions);
            poly.addTo(mapView.getMap());
            polylineArrayList.add(poly);
            previous = latLng;
        }
        previousPositionMap.put(deviceId, previous);
    }

    //метод который убирает маркер
    public void removeDeviceMarker(final Device device) {
        DeviceFollowState deviceFollowState = deviceHashMapFollow.get(device.getId());
        deviceFollowState.state = false;
        mapView.getMap().removeLayer(mapView.getMarkers().get(device.getId()));
    }

    public void removeWay(final Device device) {
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(device.getId());
        if (polylineArrayList != null) {
            for (Polyline polyline : polylineArrayList) {
                mapView.getMap().removeLayer(polyline);
            }
        }
        if (!flag) {
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
                            DeviceFollowState deviceFollowState = new DeviceFollowState();
                            deviceFollowState.state = false;
                            deviceHashMapFollow.put(devicePosition.getDeviceId(), deviceFollowState);
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
                        LoggerHelper.log(className, "Can't retrieve positions <br>"
                                + "Status code: " + response.getStatusCode() + "<br>, deviceLastUpdate: "/*device.getLastUpdate()*/);
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    public void refocusedDevice(final Device device, final boolean flag) {
        int startZoom = mapView.getMap().getZoom();
        int counter = 0;
        double bufLat = 0;
        double bufLong = 0;
        int countOfFollow = 0;
        DeviceFollowState deviceFollowState = deviceHashMapFollow.get(device.getId());
        deviceFollowState.state = flag;
        Set<Integer> positionSet = markerPosition.keySet();
        for (Integer i : positionSet) {
            Position position = markerPosition.get(i);
            if (deviceHashMapFollow.get(position.getDeviceId()).state) {
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
                    if (deviceHashMapFollow.get(position.getDeviceId()).state) {
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

    private PolylineOptions getPolyLineOptions() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.setOpacity(0.3);
        polylineOptions.setWeight(4);
        return polylineOptions;
    }

    public void setFlag(Device device) {
        flag = true;
        removeDeviceMarker(device);
        removeWay(device);
    }

    public void drawHistory(Device device) {
        flag = false;
        refocusedDevice(device, true);
        ArrayList<Polyline> polylineArrayList = devicePolyLineHashMap.get(device.getId());
        for (Polyline polyline : polylineArrayList) {
            polyline.addTo(mapView.getMap());
        }
    }

    public void drawDeviceMarker(final Device device) {
        Position devicePosition = markerPosition.get(device.getId());
        LatLng latLng = new LatLng(devicePosition.getLatitude(), devicePosition.getLongitude());
        Marker marker = mapView.getMarkers().get(device.getId());
        marker.setLatLng(latLng);
        marker.addTo(mapView.getMap());
    }

    public void setStartDrawFlag(Device device) {
        refocusedDevice(device, true);
        drawDeviceMarker(device);
        setStartPosition(device.getId());
        startDrawFlag.put(device.getId(), true);
    }

    private class DeviceFollowState {
        boolean state = true;
    }
}
