package io.gg.broker.jms;

import io.gg.broker.jms.messages.GJmsMessage;

/**
 * Created by gerald on 09/11/16.
 */
public class GJmsConsumer extends GJmsObject {

    protected GJmsQueueInfo queueInfos;

    public GJmsConsumer() throws GJmsException {
        this.broker=null;
    }
    public GJmsConsumer(String queueName) throws GJmsException {
        this.setQueueInfo(new GJmsQueueInfo(queueName));
    }
    public GJmsConsumer(GJmsQueueInfo info) throws GJmsException {
        this.setQueueInfo(info);
    }

    public void setQueueInfo(GJmsQueueInfo info) throws GJmsException {
        queueInfos=info;

        if (info.getMode().equals(GJmsQueueInfo.Mode.ACTIVEMQ_TCP_CLASSIC))
            this.broker= GJmsTemplate.createConsumer(info);
        if (info.getMode().equals(GJmsQueueInfo.Mode.ACTIVEMQ_REST_MODE))
            this.broker= GJmsTemplate.createConsumerViaRestHttp(info);

    }

    public GJmsTemplate setDelegate(GJmsTemplate.WzConsumerProtocol delegate){
        this.broker.setConsumerDelegate(delegate);
        return this.broker;
    }


    public GJmsQueueInfo getQueueInfos() {
        return queueInfos;
    }

    public GJmsMessage consume() throws GJmsException {
        return this.broker.consume();
    }
 }
