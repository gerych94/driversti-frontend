package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.info.Info;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.GeofenceData;
import org.bitbucket.treklab.client.model.Coordinate;
import org.bitbucket.treklab.client.model.Geofence;
import org.bitbucket.treklab.client.model.Type;
import org.bitbucket.treklab.client.util.LoggerHelper;
import org.bitbucket.treklab.client.view.GeofenceAddDialog;
import org.bitbucket.treklab.client.view.MapView;
import org.discotools.gwt.leaflet.client.draw.events.DrawCreatedEvent;
import org.discotools.gwt.leaflet.client.draw.events.DrawEditedEvent;
import org.discotools.gwt.leaflet.client.layers.ILayer;
import org.discotools.gwt.leaflet.client.layers.others.FeatureGroup;
import org.discotools.gwt.leaflet.client.layers.others.LayerGroup;
import org.discotools.gwt.leaflet.client.layers.vector.Circle;
import org.discotools.gwt.leaflet.client.layers.vector.Polygon;
import org.discotools.gwt.leaflet.client.types.LatLng;

import java.util.ArrayList;

public class GeofenceController implements MapView.GeofenceHandler {

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

    @Override
    public void onAdd(DrawCreatedEvent event, FeatureGroup drawnItems, MapView mapView) {
        ILayer layer = event.getLayer();
        //drawnItems.addLayer(layer);
        String layerType = event.getLayerType().toUpperCase(); // получаем тип геозоны
        final Type type = Type.valueOf(layerType); // приводим к enum
        Geofence geofence = (Geofence) Geofence.createObject(); // создаём пустую геозону
        geofence.setType(type); // присваиваем тип геозоны
        switch (layerType) {
            // если геозона "КРУГ"
            case "CIRCLE":
                Circle circle = (Circle) layer; // приводим тип геозоны
                drawnItems.addLayer(circle);
                LatLng circleLatLng = circle.getLatLng(); // получаем координаты центра круга (геозоны)
                double radius = circle.getRadius(); // получаем радиус геозоны
                LoggerHelper.log(className, radius);
                Coordinate circleCoordinate = (Coordinate) Coordinate.createObject(); // создаём пустой объект координат
                circleCoordinate.setLongitude(circleLatLng.lng()); // присваиваем координатам долготу
                circleCoordinate.setLatitude(circleLatLng.lat()); // присваиваем координатам широту
                ArrayList<Coordinate> circleCoordinates = new ArrayList<>(); // создаём список координат (для геозоны)
                circleCoordinates.add(circleCoordinate); // в список координат добавляем координаты центра круга
                geofence.setCoordinates(circleCoordinates); // присваиваем геозоне список координат
                geofence.setRadius(radius); // присваиваем геозоне радиус
                break;
            // если геозона "ПОЛИГОН"
            case "POLYGON":
                Polygon polygon = (Polygon) layer; // приводим тип геозоны
                drawnItems.addLayer(polygon);
                LatLng[] polygonLatLngs = polygon.getLatLngs(); // получаем массив координат полигона (геозоны)
                ArrayList<Coordinate> polygonCoordinates = new ArrayList<>(); // создаём список координат (для геозоны)
                for (LatLng polygonLatLng : polygonLatLngs) {
                    // в цикле проходимся по всем парам координат точек геозоны
                    Coordinate polygonCoordinate = (Coordinate) Coordinate.createObject(); // создаём пустой объект координат
                    polygonCoordinate.setLongitude(polygonLatLng.lng()); // присваиваем координатам долготу
                    polygonCoordinate.setLatitude(polygonLatLng.lat()); // присваиваем координатам широту
                    polygonCoordinates.add(polygonCoordinate); // в список координат добавляем координаты точек полигона
                }
                geofence.setCoordinates(polygonCoordinates); // присваиваем геозоне список координат
                break;
        }
        // вызываем диалог добавления новой геозоны и передаём ей созданную геозону и список геозон
        new GeofenceAddDialog(geofence, geofenceStore, mapView, layer).show();
    }

    @Override
    public void onEdit(final DrawEditedEvent event) {
        LayerGroup layers = event.getLayers();
        ILayer[] array = layers.getLayers();
        for (int i = 0; i < array.length; i++) {
            LoggerHelper.log(className, array[i].getJSObject().getProperty("_mRadius") + "");
        }
    }

    @Override
    public void onRemove(final Geofence selectedGeofence) {
        LoggerHelper.log(className, "'Remove button' has been pressed");
        final ConfirmMessageBox confirm = new ConfirmMessageBox(
                "Подтверждение удаления геозоны",
                "Это действие удалит выбранную геозону без возможности восстановления! \n" +
                        "Вы действительно хотите удалить геозону?");
        confirm.setResizable(false);
        confirm.setModal(true);
        confirm.setWidth(350);
        confirm.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton() == Dialog.PredefinedButton.YES) {
                    LoggerHelper.log(className, "Button YES has been pressed");
                    try {
                        // запрос к серверу на удаление устройста
                        geofenceData.removeGeofence(selectedGeofence, new BaseRequestCallback() {
                            @Override
                            public void onResponseReceived(Request request, Response response) {
                                if (204 == response.getStatusCode()) {
                                    // если ответ от сервера правильный (в данном случае 204), удаляем геозону из таблицы
                                    geofenceStore.remove(selectedGeofence);
                                    Info.display("Уведомление", "Геозона " + selectedGeofence.getName() + " успешно удалено!");
                                    LoggerHelper.log(className, "Device " + selectedGeofence.getName() + " has been removed. Bye-bye motherfucker!");
                                } else {
                                    Info.display("Ошибка", "Не удалось удалить геозону  " + selectedGeofence.getName());

                                    LoggerHelper.log(className, "Error while deleting device. " +
                                            "Error code: " + response.getStatusCode() +
                                            ". Error status message: " + response.getStatusText());
                                }
                            }
                        });
                    } catch (RequestException e) {
                        LoggerHelper.log(className, "Error while deleting device. No response from server.", e);
                    }
                }
            }
        });
        confirm.show();
    }
}
