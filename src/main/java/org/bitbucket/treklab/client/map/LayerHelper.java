package org.bitbucket.treklab.client.map;

import org.discotools.gwt.leaflet.client.Options;
import org.discotools.gwt.leaflet.client.layers.raster.TileLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LayerHelper
{

    private  static String[] urls={ "https://api.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicGFtcHVzaGtvIiwiYSI6ImNpbDg0aWFvajAwMXd3NGtyZThrMm83aHQifQ.4V4UOkBoW5ijyvLdjqKJdw",
            "https://api.mapbox.com/v4/mapbox.light/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicGFtcHVzaGtvIiwiYSI6ImNpbDg0aWFvajAwMXd3NGtyZThrMm83aHQifQ.4V4UOkBoW5ijyvLdjqKJdw",
            "http://mt0.google.com/vt/lyrs=m&x={x}&y={y}&z={z}"};

    private static Map<String, String> mapName = new HashMap<>();
    static {
        mapName.put("Mapbox Streets", "https://api.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicGFtcHVzaGtvIiwiYSI6ImNpbDg0aWFvajAwMXd3NGtyZThrMm83aHQifQ.4V4UOkBoW5ijyvLdjqKJdw");
        mapName.put("Mapbox Light", "https://api.mapbox.com/v4/mapbox.light/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicGFtcHVzaGtvIiwiYSI6ImNpbDg0aWFvajAwMXd3NGtyZThrMm83aHQifQ.4V4UOkBoW5ijyvLdjqKJdw");
        mapName.put("Google Map", "http://mt0.google.com/vt/lyrs=m&x={x}&y={y}&z={z}");
    }

    //получаем массив опций всех наших слоев
    public static ArrayList<Options> getTileLayersOptions()
    {
        //создание переменных для хранения опций соответствующих слоев
        ArrayList<Options> arrayTileLayerOptions = new ArrayList<Options>();

        for (Map.Entry<String, String> entry : mapName.entrySet()) {
            //Create mutable TileLayer options
            //создание изменяемых параметров Слоев
            Options options = new Options();
            options.setProperty("attribution", entry.getKey());
            arrayTileLayerOptions.add(options);
        }
/*        for (int i = 0; i < urls.length; i++)
        {
            //Create mutable TileLayer options
            //создание изменяемых параметров Слоев
            Options options = new Options();
            // TODO: 07.04.2016 Заменить массив ссылок на Map, чтобы "Карта №х" можно заменить на соответствующее имя
            options.setProperty("attribution", "Карта №" + Integer.toString(i+1));
            arrayTileLayerOptions.add(options);

        }*/
        return arrayTileLayerOptions;
    }

    //получаем массив всех наших слоев с добавленными к ним опциями
    public static ArrayList<TileLayer>getTileLayers()
    {
        ArrayList<TileLayer> tileLayers = new ArrayList<TileLayer>();
        //получение массива слоев карт
        ArrayList<Options> arrayTileLayerOptions = getTileLayersOptions();

        for (int i = 0; i < urls.length; i++)
        {
            TileLayer tileLayer = new TileLayer(urls[i], arrayTileLayerOptions.get(i));
            tileLayers.add(tileLayer);
        }

        return tileLayers;
    }
}
