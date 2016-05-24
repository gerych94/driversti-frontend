package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.ListStore;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.GeofenceData;
import org.bitbucket.treklab.client.model.Geofence;
import org.bitbucket.treklab.client.util.LoggerHelper;

public class GeofenceController {

    private final ListStore<Geofence> geofenceStore;
    private final GeofenceData geofenceData;

    private static final String className = GeofenceController.class.getSimpleName();

    public GeofenceController(ListStore<Geofence> globalGeofenceStore) {
        this.geofenceStore = globalGeofenceStore;
        this.geofenceData = new GeofenceData();
    }

    public void run() {
        try {
            geofenceData.getGeofences(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // в случае успешного ответа получаем список объектов
                        JsArray<Geofence> geofenceJsArray = JsonUtils.safeEval(response.getText());
                        LoggerHelper.log(className, geofenceJsArray.length() + "");
                        /** в цикле добавляем новые геозоны или обновляем существующие */
                        /** если список пустой, добавляем в него все объекты из сервера */
                        if (geofenceStore.size() <= 0) {
                            for (int i = 0; i < geofenceJsArray.length(); i++) {
                                geofenceStore.add(geofenceJsArray.get(i));
                            }
                        } else if (geofenceStore.size() <= geofenceJsArray.length()) {
                            /** Если из сервера пришло объектов больше, чем есть в списке */{
                                for (int i = 0; i < geofenceJsArray.length(); i++) {
                                    // проверяем соответствует ли каждой геозоне в списке геозон геозона из сервера
                                    if (geofenceStore.get(i) == null) {
                                        // если в списке нет геозоны на позиции i - добавляем
                                        geofenceStore.add(geofenceJsArray.get(i));
                                        LoggerHelper.log(className, "");
                                    } else if (geofenceStore.get(i).getId() == geofenceJsArray.get(i).getId()) {
                                        // если геозона на позиции i есть в обоих списках - обновляем
                                        geofenceStore.update(geofenceJsArray.get(i));
                                    }
                                }
                            }
                        } else if (geofenceStore.size() > geofenceJsArray.length()) {
                            /** Если из сервера пришло объектов меньше, чем есть в списке */{
                                for (int i = 0; i < geofenceStore.size(); i++) {
                                    // проверяем соответствует ли каждому объекту в списке устройств устройство из сервера
                                    if (geofenceJsArray.get(i) == null) {
                                        // если нет, удаляем устройство из списка
                                        geofenceStore.remove(i);
                                        LoggerHelper.log(className, "");
                                        // и делаем шаг назад, так как количество устройств в списке уменьшилось на 1
                                        i--;
                                    } else if (geofenceJsArray.get(i) == geofenceStore.get(i)) {
                                        // если на месте в списке устройств есть устройство и в списке из сервера - обновляем
                                        geofenceStore.update(geofenceJsArray.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        // при ошибке получения данных отображаем соответствующее сообщение с кодом ошибки (console!)
                        LoggerHelper.log(className, "Bad response from server. Response status code: " + response.getStatusCode());
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
