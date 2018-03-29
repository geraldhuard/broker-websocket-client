package io.gg.broker.jms.messages;

import io.gg.broker.tools.Logger;
import io.gg.broker.jms.GJmsException;
import io.gg.broker.jms.GJmsQueueInfo;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by gerald on 20/12/16.
 *
 * Cette classe permet de definir un message etant produit depuis les differents consumer/api
 * et sont consommés par le server /notifier de websocket puis acheminés jusqu'au studio
 *
 */
public class GJmsMessageNotifier extends GJmsMessage {


    @GJmsMessageProperty
    private String userId;

    @GJmsMessageProperty
    private String orderId;



    public GJmsMessageNotifier() {
        super();
    }

    public GJmsMessageNotifier(Message message) throws GJmsException {
        super(message);

        try {
            this.userId = message.getStringProperty("userid");
            this.orderId = message.getStringProperty("orderid");


            Logger.log("USERID: " + this.userId + " ORDER:" + this.orderId);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getQueueName() {
        return GJmsQueueInfo.QUEUE_NAME_NOTIFIER;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }
}
