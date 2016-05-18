package org.bitbucket.treklab.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.communication.socket.SocketListener;
import org.bitbucket.treklab.client.controller.*;
import org.bitbucket.treklab.client.model.*;
import org.bitbucket.treklab.client.view.ApplicationView;
import org.bitbucket.treklab.client.view.CenterView;
import org.bitbucket.treklab.client.view.WestView;
import org.realityforge.gwt.websockets.client.WebSocket;

public class Application {

    private final NavController navController;
    private final DeviceController deviceController;
    private final EventController eventController;
    private final GeofenceController geofenceController;
    private final StateController stateController;
    private final MapController mapController;
    private final ScheduleController scheduleController;
    private final VisibilityController visibilityController;
    private final FollowController followController;
    private final static DataService dataServiceController = DataServiceController.getInstance();

    private ApplicationView view;

    private final CenterView centerView;
    private final WestView westView;

    private boolean isDeviceSelected = false;

    private static final String className = Application.class.getSimpleName();

    public Application() {
        DeviceProperties deviceProperties = GWT.create(DeviceProperties.class);
        final ListStore<Device> globalDeviceStore = new ListStore<>(deviceProperties.key());
        EventProperties eventProperties = GWT.create(EventProperties.class);
        final ListStore<Event> globalEventStore = new ListStore<>(eventProperties.key());
        GeofenceProperties geofenceProperties = GWT.create(GeofenceProperties.class);
        final ListStore<Geofence> globalGeofenceStore = new ListStore<>(geofenceProperties.key());

        navController = new NavController();
        mapController = new MapController(globalGeofenceStore);
        visibilityController = new VisibilityController(globalDeviceStore);
        followController = new FollowController(globalDeviceStore);
        stateController = new StateController();
        deviceController = new DeviceController(globalDeviceStore, globalEventStore, globalGeofenceStore, mapController, stateController, visibilityController, followController);
        eventController = new EventController(globalDeviceStore, globalEventStore);
        geofenceController = new GeofenceController(globalGeofenceStore);
        scheduleController = new ScheduleController();

        centerView = new CenterView(mapController.getView(), scheduleController.getView());
        westView = new WestView(deviceController.getView(), stateController.getView());

        view = new ApplicationView(navController.getView(),
                westView.getView(),
                centerView.getView());
    }

    public void run() {
        RootPanel.get().add(view);

        deviceController.run();

        final WebSocket webSocket = WebSocket.newWebSocketIfSupported();
        if (webSocket != null) {
            webSocket.setListener(new SocketListener());
            webSocket.connect("ws://185.69.152.120:8082/api/socket");
        } else {
            Window.alert( "WebSocket not available!" );
        }


        eventController.run();
        Timer timer = new Timer() {
            @Override
            public void run() {
                deviceController.run();
                Device selectedDevice = deviceController.getDeviceView().getDeviceGrid().getSelectionModel().getSelectedItem();
                isDeviceSelected = (selectedDevice != null);
                if (isDeviceSelected) {
                    stateController.update(selectedDevice);
                    //stateController.fillGrid(selectedDevice);
                    //mapController.setDeviceMarker(selectedDevice);
                    //mapController.focusedOnDevice(selectedDevice);
                    scheduleRepeating(10000);
                } else {
                    stateController.getStateView().getRowStore().clear();
                    scheduleRepeating(30000);
                }
                eventController.run();
            }
        };
        timer.schedule(1000);
        //timer.schedule(50);
    }

    public static DataService getDataServiceController() {
        return dataServiceController;
    }
}
