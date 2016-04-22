package org.bitbucket.treklab.client.map;

import org.discotools.gwt.leaflet.client.marker.label.LabelOptions;

public class LabelHelper {
    public static LabelOptions createLabelOptions()
    {
        final LabelOptions labelOptions = new LabelOptions();
        //прикрепляет обработчик, который позволяет показывать/скрывать
        //метку при наведении мыши
        labelOptions.setNoHide(false);
        labelOptions.setWithoutDefaultStyle(true);
        //положение метки относительно маркера
        //по умолчанию right
        //допустимые значения left|right|auto
        //при значении auto, в зависимости от направления, выбирается оптимальная позиция
        //метки по отношению к маркеру
        labelOptions.setProperty("direction", "auto");

        //прозрачность метки прикрепляемой к маркеру(по умолчанию это 1)
        labelOptions.setProperty("opacity", "1");
        //labelOptions.setProperty("noHide", true);


        return labelOptions;
    }
}
