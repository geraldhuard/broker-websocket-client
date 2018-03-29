package io.gg.broker.jms;

import io.gg.broker.jms.messages.GJmsMessage;
import io.gg.broker.jms.messages.GJmsMessageFactory;


import javax.jms.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by gerald on 09/11/16.
 */
public class GJmsActiveMQviaRestHttp implements GJmsTemplate {

    public static final int TIMEOUT=1000;
    public static final int TIME_BETWEEN_TWO_CONSUMATION=500;

    private URLConnection connection;
    private MessageConsumer messageConsumer = null;

    private WzConsumerProtocol delegateConsumer = null;
    private Boolean consumerConsume=false;

    private GJmsQueueInfo openedQueueInfos;


    @Override
    public void open(GJmsQueueInfo infos) throws GJmsException {
        this.openedQueueInfos = infos;
    }


    @Override
    public void producer(GJmsQueueInfo infos) throws GJmsException {
        this.openedQueueInfos = infos;
    }

    @Override
    public void consumer(GJmsQueueInfo infos) throws GJmsException {
        this.openedQueueInfos = infos;
    }


    @Override
    public void start() throws GJmsException {
        if (this.delegateConsumer!=null){
            this.startConsumation();
        }
    }


    @Override
    public void send(GJmsMessage message) throws GJmsException {
        /*
        //curl -XPOST "http://admin:admin@localhost:8161/api/message?destination=queue://testGG&trackable=true"
        Map<String, String> properties = message.buildMapProperties();
        try {

            String qs="";
            for (String key : properties.keySet()){
                qs+="&"+key+"="+properties.get(key);
            }

            HttpURLConnection connection = this.openedQueueInfos.getConnectionToUrl(qs);
            connection.setRequestMethod("POST");

            int responseCode = connection.getResponseCode();

            if (responseCode!=200){
                throw new GJmsException("Error on call : "+ this.openedQueueInfos.getUrlBase() +" - "+responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }






    public GJmsMessage consume() {
        return null;
    }


    private HttpURLConnection connect(){
        return this.openedQueueInfos.getConnectionToUrl("&oneShot=true");
    }


    @Override
    public void setConsumerDelegate(WzConsumerProtocol delegate) {
        this.delegateConsumer=delegate;
    }


    private void startConsumation() throws GJmsException {
        this.consumerConsume=true;

        //this.consumePendingMessage();

        /*** Nouveau message ***/
        while (this.consumerConsume){
            try {

                HttpURLConnection con=this.connect();

                if (con.getResponseCode()==200) {
                    Map<String, List<String>> headers = con.getHeaderFields();

                    GJmsMessage msg = GJmsMessageFactory.createFromHeaders(headers);
                    if (msg != null) {
                        this.delegateConsumer.onReceivedMessage(msg);
                    }
                }else{
                }
                Thread.sleep(TIME_BETWEEN_TWO_CONSUMATION);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    public void close() throws GJmsException {
        this.consumerConsume=false;
    }
}
