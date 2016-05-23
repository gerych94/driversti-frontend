package org.bitbucket.treklab.client.communication;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.bitbucket.treklab.client.model.Device;

import java.util.Date;

public class PositionData extends ServerData {

    /**
     * Этот метод позволяет получить позиции трекера для определённого устройства и за определённое время
     *
     * @param device   - устройство, позииции которого нас интересуют
     * @param from     - время и дата, с которого нужно сделать выборку.
     *                 ВНИМАНИЕ!
     *                 Дату записываем так - new Date(yyyy, mm, dd)
     *                 Все значения int
     *                 Год записываем в формате: год - 1900.
     *                 То есть, если интересует 2016 год, то вместо yyyy записываем 116 = 2016 - 1900 !!!!!
     *                 Месяц отсчитывается с нуля!!! То есть январь это месяц 0, декабрь - 11!
     *                 День: 1-31.
     * @param to       - время и дата, до которого нужно сделать выборку
     * @param callback - функция обратного вызова, которая, собственно, и обращается к серверу
     * @throws RequestException - метод может выбросить исключение RequestException при ошибке обращения к серверу
     */
    public void getPositions(Device device, Date from, Date to, RequestCallback callback) throws RequestException {
        DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sendRequest("/positions?deviceId=" + device.getId() + "&from=" + format.format(from) + "&to=" + format.format(to), null, RequestBuilder.GET, callback);
    }

    public void getPositions(RequestCallback callback) throws RequestException {
        String urlParameters = "/positions";
        sendRequest(urlParameters, null, RequestBuilder.GET, callback);
    }
}
