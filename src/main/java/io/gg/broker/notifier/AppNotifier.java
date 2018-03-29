package io.gg.broker.notifier;

import io.gg.broker.jms.messages.GJmsMessageNotifier;
import io.gg.broker.tools.Logger;
import io.gg.broker.jms.GJmsException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.Session;


/***
 * This is the main class started by apache deployement listener
 */
public class AppNotifier implements GConsumerNotifier.Protocol, GNotifierWebSocket.Protocol {


    private static Thread notifierThread = null;
    private static GConsumerNotifier consumer = null;

    private static final ListOfGNotifierConnection connections = new ListOfGNotifierConnection();


    /***
     * Start consumer in child thread
     */
    private void startConsumer(){
        if (notifierThread==null) {
            notifierThread = new Thread() {
                @Override
                public void run() {

                    super.run();
                    try {
                        AppNotifier.this.consumer = new GConsumerNotifier(AppNotifier.this);
                        AppNotifier.this.consumer.start();
                    } catch (GJmsException e) {
                    }
                }
            };
            notifierThread.start();
        }
    }

    /***
     * Stop thread of current consumer
     */
    private void stopConsumer() {
        if (notifierThread != null) {
            try {
                consumer.stop();
            } catch (GJmsException e) {
                e.printStackTrace();
            }
            notifierThread.interrupt();
        }
    }

    /***
     * Called from NotifierContextListener
     * Start the notifer to be delegate of websocket and start consuming ActiveMQ queue
     */
    public void start(){
        GNotifierWebSocket.delegate=this;
        this.startConsumer();

    }

    public void stop(){
        this.stopConsumer();
    }



    @Override
    public void onConnect(Session session, GNotifierWebSocket ws) {
        GNotifierConnection connection=new GNotifierConnection(session,ws);
        if (!connections.connectionExist(ws))
            connections.add(connection);
    }

    /***
     * Le client doit s'identifier avec son userId et son orderId pour etre considere comme à l'ecoute.
     * -> Ceci doit etre un peu securisé pour la prod.
     * @param message
     * @param ws
     */
    @Override
    public void onMessage(String message, GNotifierWebSocket ws) {
        Logger.log("notifier", "info", "New Message (in) : " + message);

        // Never trust the client
        try {
            JSONObject json = new JSONObject(message);
            GNotifierConnection.WzClientInfo clientInfo = new GNotifierConnection.WzClientInfo();

            if (json.has("userid") && json.has("orderid")) {
                clientInfo.userId = json.getString("userid");
                if (json.has("orderid")) {
                    clientInfo.orderId = json.getString("orderid");
                    connections.setClientInfo(ws, clientInfo);
                }
            }else{
                Logger.log("notifier", "no", "Bad JSON no userid/orderid ");
            }
        }catch (JSONException e){}
    }

    @Override
    public void onDisconnect(GNotifierWebSocket ws) {
        connections.remove(ws);
    }

    @Override
    public void onError(Throwable t, GNotifierWebSocket ws) {

    }

    /***
     * Called when a message comoing from broker.
     * @param message
     */
    @Override
    public void newMessageIncomingFromBroker(GJmsMessageNotifier message) {

        JSONObject json = new JSONObject();
        json.put("message", "command paid");
        json.put("status", "ok");
        JSONObject jsonDetails = new JSONObject();
        jsonDetails.put("userid",message.getUserId());
        jsonDetails.put("orderid",message.getOrderId());
        json.put("info",jsonDetails);

        Boolean sent = connections.broadcastToUser(message.getUserId(), message.getOrderId(), json.toString());

        // On acquite tout les messages afin de pouvoir traiter les suivants
        try {
            message.acknowledge();
        } catch (GJmsException e) {
            e.printStackTrace();
        }
    }
}
