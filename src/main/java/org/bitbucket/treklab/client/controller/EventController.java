package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.Event;
import org.bitbucket.treklab.client.model.Position;
import org.bitbucket.treklab.client.util.DateHelper;
import org.bitbucket.treklab.client.util.LoggerHelper;

import java.util.Date;

public class EventController {

    private final ListStore<Device> deviceStore;
    private final ListStore<Event> eventStore;
    private boolean firstRun = true;
    private final PositionData positionData = new PositionData();
    private Date from;
    private Date to;

    private static final String classname = EventController.class.getSimpleName();

    public EventController(ListStore<Device> globalDeviceStore, ListStore<Event> globalEventStore) {
        this.deviceStore = globalDeviceStore;
        this.eventStore = globalEventStore;
    }

    public void run() {
        if (firstRun) {
            from = DateHelper.getUTCDate();
            try {
                positionData.getPositions(new BaseRequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        if (200 == response.getStatusCode()) {
                            JsArray<Position> data = JsonUtils.safeEval(response.getText());
                            for (int i = 0; i < data.length(); i++) {
                                if (data.get(i).getSpeed() > 30) {
                                    eventStore.add(new Event(data.get(i).getDeviceTime(), "Some device", "Overspeed"));
                                }
                            }
                        } else {
                            LoggerHelper.log(classname, "Server responded with code: " + response.getStatusCode());
                        }
                    }
                });
            } catch (RequestException e) {
                LoggerHelper.log(classname, "Can't connect to the server", e);
            }
            firstRun = false;
        } else {
            to = DateHelper.getUTCDate();
            for (int i = 0; i < deviceStore.size(); i++) {
                final Device device = deviceStore.get(i);
                try {
                    positionData.getPositions(device, from, to, new BaseRequestCallback() {
                        @Override
                        public void onResponseReceived(Request request, Response response) {
                            if (200 == response.getStatusCode()) {
                                JsArray<Position> data = JsonUtils.safeEval(response.getText());
                                for (int i = 0; i < data.length(); i++) {
                                    if (data.get(i).getSpeed() > 30) {
                                        LoggerHelper.log(classname, data.get(i).getDeviceTime());
                                        eventStore.add(new Event(data.get(i).getDeviceTime(), "Some device", "Overspeed"));
                                        LoggerHelper.log(classname, "Added new overspeed: " + data.get(i).getSpeed());
                                    }
                                }
                                from = to;
                            } else {
                                LoggerHelper.log(classname, "Server responded with code: " + response.getStatusCode());
                            }
                        }
                    });
                } catch (RequestException e) {
                    LoggerHelper.log(classname, "Can't connect to the server", e);
                }
            }
        }
    }
}
