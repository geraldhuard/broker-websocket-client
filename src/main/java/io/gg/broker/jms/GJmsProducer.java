package io.gg.broker.jms;

import io.gg.broker.jms.messages.GJmsMessage;

/**
 * Created by gerald on 09/11/16.
 */
public class GJmsProducer extends GJmsObject {

    public GJmsProducer(GJmsQueueInfo info) throws GJmsException {
        if (info.getMode().equals(GJmsQueueInfo.Mode.ACTIVEMQ_TCP_CLASSIC))
            this.broker= GJmsTemplate.createProducer(info);
        if (info.getMode().equals(GJmsQueueInfo.Mode.ACTIVEMQ_REST_MODE))
            this.broker= GJmsTemplate.createProducerViaRestHttp(info);
    }

    public GJmsProducer(String queueName) throws GJmsException {
        this.broker= GJmsTemplate.createProducer(new GJmsQueueInfo(queueName));
    }


    public void send(GJmsMessage message) throws GJmsException {
        this.broker.send(message);
    }
}
