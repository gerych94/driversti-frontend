package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.OverSpeedEvent;
import org.bitbucket.treklab.client.util.Observer;

import java.util.Date;

public class EventController implements Observer {

    private static final String classname = EventController.class.getSimpleName();
    private final ListStore<Device> deviceStore;
    private final ListStore<OverSpeedEvent> eventStore;
    private final PositionData positionData = new PositionData();
    private boolean firstRun = true;
    private Date from;
    private Date to;

    private static final String EVENTS_KEY = "events";
    private static final String className = EventController.class.getSimpleName();

    public EventController(ListStore<Device> globalDeviceStore, ListStore<OverSpeedEvent> globalEventStore) {
        this.deviceStore = globalDeviceStore;
        this.eventStore = globalEventStore;
    }

    @Override
    public void update(String key, String value) {
        if (key.equals(EVENTS_KEY)) {
            JsArray<OverSpeedEvent> eventJsArray = JsonUtils.safeEval(value);
            for (int i = 0; i < eventJsArray.length(); i++) {
                // TODO: 26.05.2016 если первый запуск, провести проверку на пустой список. Или внедрить дополнительный метод нотификации при ключе first
                eventStore.update(eventJsArray.get(i));
            }
        }
    }

    /*public void run() {
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
                                    eventStore.add(new OverSpeedEvent(data.get(i).getDeviceTime(), "Some device", "Overspeed"));
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
                                        eventStore.add(new OverSpeedEvent(data.get(i).getDeviceTime(), "Some device", "Overspeed"));
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
    }*/
}
