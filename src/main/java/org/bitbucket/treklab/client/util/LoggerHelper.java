package org.bitbucket.treklab.client.util;

import com.google.gwt.core.client.GWT;

import java.util.Date;

/**
 *  Этот класс позволяет получать логи событий в консоль браузера в удобном формате - "ClassName HH:mm:SS - message"
 *  Чтобы им воспользоваться, необходимо вызвать статический метод log(parameters).
 *  В качестве параметров передаём имя класса, в котором вызывается метод, сообщение (которое хотим увидеть в консоли)
 *  и/или переменную ошибки.
 *  Данный класс с методом log(parameters) оборачивает собой GWT.log(parameters) и предоставляет логи в
 *  более удобном для восприятия формате
 *
 *  @author driversti (Yurii Chekhotskyi)
 *  @version 1.0
 *
 */
public class LoggerHelper {

    public static void log(String className, String message) {
        GWT.log(getMessage(className, message));
    }

    public static void log(String className, String message, Throwable e) {
        GWT.log(getMessage(className, message), e);
    }

    private static String getMessage(String className, String message) {
        Date now = new Date();
        String hour = getH(now);
        String minutes = getM(now);
        String seconds = getS(now);
        return className + " " + hour + ":" + minutes + ":" + seconds + " - " + message;
    }

    private static String getH(Date now) {
        if (now.getHours() < 10) {
            return "0" + now.getHours();
        }
        return String.valueOf(now.getHours());
    }

    private static String getM(Date now) {
        if (now.getMinutes() < 10) {
            return "0" + now.getMinutes();
        }
        return String.valueOf(now.getMinutes());
    }

    private static String getS(Date now) {
        if (now.getSeconds() < 10) {
            return "0" + now.getSeconds();
        }
        return String.valueOf(now.getSeconds());
    }
}
