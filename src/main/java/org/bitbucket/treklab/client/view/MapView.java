package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.Viewport;
import org.bitbucket.treklab.client.map.LayerHelper;
import org.bitbucket.treklab.client.map.MyLayerHandler;
import org.discotools.gwt.leaflet.client.Options;
import org.discotools.gwt.leaflet.client.controls.ControlOptions;
import org.discotools.gwt.leaflet.client.controls.Position;
import org.discotools.gwt.leaflet.client.controls.draw.Draw;
import org.discotools.gwt.leaflet.client.controls.draw.DrawControlOptions;
import org.discotools.gwt.leaflet.client.controls.layers.Layers;
import org.discotools.gwt.leaflet.client.draw.edit.EditOptions;
import org.discotools.gwt.leaflet.client.draw.events.DrawCreatedEvent;
import org.discotools.gwt.leaflet.client.draw.events.DrawEditedEvent;
import org.discotools.gwt.leaflet.client.draw.events.handler.DrawEvents;
import org.discotools.gwt.leaflet.client.events.handler.EventHandler;
import org.discotools.gwt.leaflet.client.events.handler.EventHandlerManager;
import org.discotools.gwt.leaflet.client.layers.ILayer;
import org.discotools.gwt.leaflet.client.layers.others.FeatureGroup;
import org.discotools.gwt.leaflet.client.layers.others.LayerGroup;
import org.discotools.gwt.leaflet.client.layers.raster.TileLayer;
import org.discotools.gwt.leaflet.client.map.Map;
import org.discotools.gwt.leaflet.client.map.MapOptions;
import org.discotools.gwt.leaflet.client.marker.Marker;
import org.discotools.gwt.leaflet.client.types.LatLng;
import org.discotools.gwt.leaflet.client.widget.MapWidget;

import java.util.ArrayList;
import java.util.HashMap;

public class MapView {

    private Viewport viewport;
    private ContentPanel panel;
    private static final String MAP_ID = "map";
    private final LatLng centerLatLng = new LatLng(48.9, 34.9);
    // TODO: 04.04.2016 Рассмотреть возможность добавления маркера в список маркеров и отображения их на карте
    private HashMap<Integer, Marker> markers = new HashMap<>();
    private Marker marker = new Marker(new LatLng(0.0, 0.0), new Options());
    private Map map;

    public MapView() {
        //создаем компонент для заполнения всего доступного пространства
        viewport = new Viewport();
        //создаем контент-панель
        panel = new ContentPanel();
        //отключаем видимость заголовка контент-панели
        panel.setHeaderVisible(false);
        //добавляем контент панель во вьюпорт
        viewport.add(panel, new MarginData(0));

        //добавляем новый виджет для отображения карты
        MapWidget mapWidget = new MapWidget();
        //добавляем к контент панели виджет
        panel.add(mapWidget);

        //запускаем в следующем event loop Javascript команду по добавлению карты
        //если добавлять карту сразу же после создания виджета,
        //то не она не занимает все положенное ей пространство,
        //а в данном случае сначала будет создан и займет положенное место виджет
        //а потом в него будет помещена и заполнит его полностью наша карта
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                createMap(MAP_ID);
            }
        });
    }

    private void createMap(final String mapId) {
        //Создаём опции карты
        MapOptions mapOptions = new MapOptions();
        mapOptions.setCenter(centerLatLng);
        mapOptions.setZoom(6);
        //создаем карту с указанным id контейнера и с настройками mapOptions
        map = new Map(mapId, mapOptions);
        //получаем коллекцию слоев используемых нами в картах
        ArrayList<TileLayer> tileLayers = LayerHelper.getTileLayers();
        // выбранный слой устанавливаем для карты
        map.addLayer(tileLayers.get(0));
        /** You must call ‘invalidateSize’ to force the map to redraw otherwise the map is kind of half-drawn.
         * http://www.netthreads.co.uk/2013/07/02/gwt-trafficmap-gwt-leaflet-map-example */
        map.invalidateSize(true);
        addDrawControl(map);
        //метод для переключения между слоями карт
        switchLayers(map, tileLayers);
    }

    private void switchLayers(Map map, ArrayList<TileLayer> tileLayers) {
        Options bases = new Options();
        for (int i = 0; i < tileLayers.size(); i++) {
            switch (i) {
                case 0:
                    bases.setProperty("Mapbox Streets", tileLayers.get(i));
                    break;
                case 1:
                    bases.setProperty("Mapbox Light", tileLayers.get(i));
                    break;
                case 2:
                    bases.setProperty("Google Maps", tileLayers.get(i));
                    break;
            }
        }
        Options overlays = new Options();

        ControlOptions controlOptions = new ControlOptions();
        controlOptions.setPosition(Position.TOP_RIGHT);

        //TODO разобрать что значат параметры bases, overlays, controlOptions
        Layers control = new Layers(bases, overlays, controlOptions);
        control.addTo(map);
    }

    private void addDrawControl(Map map) {

        final FeatureGroup drawnItems = new FeatureGroup();
        map.addLayer(drawnItems);
        DrawControlOptions drawControlOptions = new DrawControlOptions();
        drawControlOptions.setPosition(Position.TOP_LEFT);
        EditOptions editOptions = new EditOptions();
        editOptions.setFeatureGroup(drawnItems);
        drawControlOptions.setEditOptions(editOptions);
        Draw draw = new Draw(drawControlOptions);

        map.addControl(draw);
        EventHandlerManager.addEventHandler(
                map,
                DrawEvents.draw_created,
                new EventHandler<DrawCreatedEvent>() {
                    @Override
                    public void handle(
                            DrawCreatedEvent event) {
                        ILayer layer = event.getLayer();
                        drawnItems.addLayer(layer);
                    }
                });
        EventHandlerManager.addEventHandler(
                map,
                DrawEvents.draw_edited,
                new EventHandler<DrawEditedEvent>() {
                    @Override
                    public void handle(
                            DrawEditedEvent event) {
                        LayerGroup layers = event.getLayers();
                        MyLayerHandler myLayerHandler = new MyLayerHandler();
                        layers.forEachLayer(myLayerHandler);
                    }
                });
    }


    public Viewport getView() {
        return viewport;
    }

    public ContentPanel getPanel() {
        return panel;
    }

    public Map getMap() {
        return map;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public HashMap<Integer, Marker> getMarkers() {
        return markers;
    }

    public LatLng getCenterLatLng() {
        return centerLatLng;
    }
}
