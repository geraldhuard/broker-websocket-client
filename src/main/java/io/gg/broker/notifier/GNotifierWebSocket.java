package io.gg.broker.notifier;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;


/**
 * Created by gerald on 20/12/16.
 *
 * Cette classe permet de declarer une instance de websocket
 * pour recuperer via activemq les notifications.
 */

@ServerEndpoint(value = "/websock")
public class GNotifierWebSocket {

    public interface Protocol{
        public void onConnect(Session session, GNotifierWebSocket ws);
        public void onMessage(String message, GNotifierWebSocket ws);
        public void onDisconnect(GNotifierWebSocket ws);
        public void onError(Throwable t,GNotifierWebSocket ws);
    }

    public static Protocol delegate;

    /***
     * Le constructeur est appellé à chaque nouvelle connection cliente.
     */
    public GNotifierWebSocket() {}



    /***
     * Un client arrive
     * @param session
     */
    @OnOpen
    public void start(Session session) {

        if (delegate!=null)
            delegate.onConnect(session,this);

    }

    /***
     * Un client s'en va
     */
    @OnClose
    public void end() {
        if (delegate!=null)
            delegate.onDisconnect(this);
    }

    /***
     * Un message arrive
     * @param message
     */
    @OnMessage
    public void incoming(String message) {

        if (delegate!=null)
            delegate.onMessage(message, this);

    }


    /***
     * Gestion d'erreur
     * @param t
     * @throws Throwable
     */
    @OnError
    public void onError(Throwable t) throws Throwable {
        if (delegate!=null)
            delegate.onError(t,this);
    }

}
