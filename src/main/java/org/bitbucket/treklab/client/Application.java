package org.bitbucket.treklab.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.controller.*;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.DeviceProperties;
import org.bitbucket.treklab.client.model.Event;
import org.bitbucket.treklab.client.model.EventProperties;
import org.bitbucket.treklab.client.view.ApplicationView;
import org.bitbucket.treklab.client.view.CenterView;
import org.bitbucket.treklab.client.view.WestView;

public class Application {

    private final NavController navController;
    private final DeviceController deviceController;
    private final EventController eventController;
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

    public Application() {
        DeviceProperties deviceProperties = GWT.create(DeviceProperties.class);
        final ListStore<Device> globalDeviceStore = new ListStore<>(deviceProperties.key());
        EventProperties eventProperties = GWT.create(EventProperties.class);
        final ListStore<Event> globalEventStore = new ListStore<>(eventProperties.key());

        navController = new NavController();
        mapController = new MapController();
        visibilityController = new VisibilityController(globalDeviceStore);
        followController = new FollowController(globalDeviceStore);
        stateController = new StateController();
        deviceController = new DeviceController(globalDeviceStore, globalEventStore, mapController, stateController, visibilityController, followController);
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
                    mapController.setDeviceMarker(selectedDevice);
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
