package io.gg.broker.notifier;


import javax.websocket.Session;

/**
 * This class correspond to a connection between websocket and client
 */
public class GNotifierConnection {



    public static class WzClientInfo{
        String userId;
        String orderId;
    }


    protected WzClientInfo clientInfo;
    protected Session session;
    protected GNotifierWebSocket client;


    public GNotifierConnection(Session session, GNotifierWebSocket client) {
        this.session = session;
        this.client = client;
    }


    public GNotifierWebSocket getClient() {
        return client;
    }

    public void setClientInfo(WzClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public Session getSession() {
        return session;
    }


    public boolean hasClientInfo(String userId, String orderId) {
        return  (this.clientInfo != null && this.clientInfo.userId.equals(userId) && this.clientInfo.orderId.equals(orderId));
    }
}
