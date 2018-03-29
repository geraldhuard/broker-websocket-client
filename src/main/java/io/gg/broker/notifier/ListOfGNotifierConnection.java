package io.gg.broker.notifier;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by gerald on 21/12/16.
 */
public class ListOfGNotifierConnection {
    protected ArrayList<GNotifierConnection> connections = new ArrayList<>();


    public ListOfGNotifierConnection() {
    }


    public void add(GNotifierConnection connection){
        this.connections.add(connection);
    }

    public GNotifierConnection getConnection(GNotifierWebSocket c){
        for (int i=0;i<this.connections.size();i++){
            GNotifierConnection con=this.connections.get(i);
            if (con.getClient().equals(c)){
                return con;
            }

        }
        return null;
    }

    public ArrayList<GNotifierConnection> getConnectionsByUserAndEntity(String userId, String orderId){

        ArrayList<GNotifierConnection> connections=new ArrayList<GNotifierConnection>();

        for (int i=0;i<this.connections.size();i++){
            GNotifierConnection con=this.connections.get(i);

            if (con.hasClientInfo(userId,orderId)){
                connections.add(con);
            }
        }
        return connections;
    }

    public boolean connectionExist(GNotifierWebSocket c){
        return this.getConnection(c)!=null;
    }


    public void setClientInfo(GNotifierWebSocket c, GNotifierConnection.WzClientInfo clientInfo){
        GNotifierConnection connection=this.getConnection(c);
        if (connection != null){
            connection.setClientInfo(clientInfo);
        }
    }

    public void remove(GNotifierWebSocket c){
        this.connections.remove(this.getConnection(c));
    }


    public void broadcast(String msg) {
        for (GNotifierConnection connection : this.connections) {
            try {
                synchronized (connection) {
                    connection.getSession().getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                //log.debug("Chat Error: Failed to send message to client", e);
                this.connections.remove(connection);
                try {
                    connection.getSession().close();
                } catch (IOException e1) {
                }
            }
        }
    }


    public boolean broadcastToUser(String userId, String orderId, String message) {

        Boolean messageSent = false;
        for (GNotifierConnection connection : this.getConnectionsByUserAndEntity(userId, orderId)) {
            try {
                synchronized (connection) {

                    connection.getSession().getBasicRemote().sendText(message);
                    messageSent = true;
                }
            } catch (IOException e) {
                //log.debug("Chat Error: Failed to send message to client", e);
                this.connections.remove(connection);
                try {
                    connection.getSession().close();
                } catch (IOException e1) {
                }
            }
        }
        return messageSent;
    }



}
