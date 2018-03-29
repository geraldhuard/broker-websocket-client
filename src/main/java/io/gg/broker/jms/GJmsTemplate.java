package io.gg.broker.jms;

import io.gg.broker.jms.messages.GJmsMessage;

import javax.jms.Message;

/**
 * Created by gerald on 09/11/16.
 *
 * Cette interface decrit comment utiliser le broker de message
 * Pour le moment, l'implementation est faite via ActiveMQ. (cf fonction statiques)
 *
 */
public interface GJmsTemplate {

    public interface WzConsumerProtocol{
        void onReceivedMessage(GJmsMessage msg);
        void process(GJmsMessage message);
        void onErrorCreateMessage(GJmsException e, Message message);
    }


    public void open(GJmsQueueInfo infos) throws GJmsException;
    public void producer(GJmsQueueInfo infos) throws GJmsException;
    public void consumer(GJmsQueueInfo infos) throws GJmsException;



    public void setConsumerDelegate(WzConsumerProtocol delegate);
    public void start() throws GJmsException;
    public void close() throws GJmsException;

    public void send(GJmsMessage message) throws GJmsException;

    public GJmsMessage consume() throws GJmsException;

    public static GJmsTemplate createConsumer(GJmsQueueInfo infos) throws GJmsException {
        GJmsTemplate broker=new GJmsActiveMQ();
        broker.consumer(infos);

        return broker;
    }
    public static GJmsTemplate createProducer(GJmsQueueInfo infos) throws GJmsException {
        GJmsTemplate broker=new GJmsActiveMQ();
        broker.producer(infos);
        return broker;
    }
    public static GJmsTemplate createConsumerViaRestHttp(GJmsQueueInfo infos) throws GJmsException {
        GJmsTemplate broker=new GJmsActiveMQviaRestHttp();
        broker.consumer(infos);

        return broker;
    }
    public static GJmsTemplate createProducerViaRestHttp(GJmsQueueInfo infos) throws GJmsException {
        GJmsTemplate broker=new GJmsActiveMQviaRestHttp();
        broker.producer(infos);

        return broker;
    }

}
