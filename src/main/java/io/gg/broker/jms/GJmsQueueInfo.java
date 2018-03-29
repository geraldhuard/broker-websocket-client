package io.gg.broker.jms;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gerald on 09/11/16.
 */
public class GJmsQueueInfo {

    public enum Mode{
        ACTIVEMQ_TCP_CLASSIC,
        ACTIVEMQ_REST_MODE
    }


    /**** NE PAS MODIFIER PLEASE ***/
    public final static String QUEUE_URL_TCP = "tcp://broker:61616";
    public final static String QUEUE_URL_HTTP = "http://broker:8161";
    public final static String QUEUE_URL = QUEUE_URL_TCP;
    public static final String QUEUE_NAME_NOTIFIER = "notifications";

    public final static String QUEUE_NAME_DEFAULT="DefaultQueue";

    public String queueName;
    public String url;
    public String username;
    public String password;
    public Mode mode = Mode.ACTIVEMQ_TCP_CLASSIC;

    public GJmsQueueInfo() {
        this.queueName = QUEUE_NAME_DEFAULT;
    }

    public GJmsQueueInfo(String queueName) {
        this.queueName = queueName;
        this.url = QUEUE_URL;
    }

    public GJmsQueueInfo(String queueName, String url) {
        this.queueName = queueName;
        this.url = url;
    }

    public GJmsQueueInfo(String queueName, String url, Mode mode) {
        this.queueName = queueName;
        this.url = url;
        this.mode = mode;
    }
    public GJmsQueueInfo(String queueName, String url, Mode mode, String username, String password) {
        this.queueName = queueName;
        this.url = url;
        this.mode = mode;

        this.username=username;
        this.password=password;
    }

    public Mode getMode() {
        return mode;
    }

    public String getUrlBase(){
        return this.url+"/api/message?destination=queue://"+this.queueName;
    }

    public HttpURLConnection getConnectionToUrl(String strToAddToQueryString) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(this.getUrlBase() + strToAddToQueryString).openConnection();
            if (this.username!=null && this.password!=null) {
                String userpass = this.username + ":" + this.password;
                String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
                connection.setRequestProperty("Authorization", basicAuth);
            }

            return connection;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
