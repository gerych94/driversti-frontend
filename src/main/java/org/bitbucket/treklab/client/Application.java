package org.bitbucket.treklab.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.communication.socket.SocketListener;
import org.bitbucket.treklab.client.controller.*;
import org.bitbucket.treklab.client.model.*;
import org.bitbucket.treklab.client.util.ServerDataHolder;
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
        OverSpeedEventProperties overSpeedEventProperties = GWT.create(OverSpeedEventProperties.class);
        final ListStore<OverSpeedEvent> globalEventStore = new ListStore<>(overSpeedEventProperties.key());
        GeofenceProperties geofenceProperties = GWT.create(GeofenceProperties.class);
        final ListStore<Geofence> globalGeofenceStore = new ListStore<>(geofenceProperties.key());
        ServerDataHolder instance = ServerDataHolder.getInstance();

        geofenceController = new GeofenceController(globalGeofenceStore);
        navController = new NavController();
        mapController = new MapController(geofenceController, globalGeofenceStore, instance);
        visibilityController = new VisibilityController(globalDeviceStore);
        followController = new FollowController(globalDeviceStore);
        stateController = new StateController(instance);
        deviceController = new DeviceController(globalDeviceStore,
                globalEventStore,
                globalGeofenceStore,
                mapController,
                stateController,
                visibilityController,
                followController,
                geofenceController,
                instance);
        eventController = new EventController(globalDeviceStore, globalEventStore);
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
        geofenceController.run();

        final WebSocket webSocket = WebSocket.newWebSocketIfSupported();
        if (webSocket != null) {
            webSocket.setListener(new SocketListener());
            webSocket.connect("ws://185.69.152.120:8082/api/socket");
        } else {
            Window.alert( "WebSocket not available!" );
        }

        //eventController.run();
    }

    public static DataService getDataServiceController() {
        return dataServiceController;
    }
}
