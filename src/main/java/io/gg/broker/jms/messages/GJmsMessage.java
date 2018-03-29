package io.gg.broker.jms.messages;

import io.gg.broker.jms.GJmsException;
import io.gg.broker.jms.GJmsProducer;
import io.gg.broker.jms.GJmsQueueInfo;
import org.apache.activemq.ActiveMQSession;
import org.hashids.Hashids;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by gerald on 09/11/16.
 */
public abstract class GJmsMessage {

    public static String PROPERTYNAME_TYPE_MESSAGE = "TypeOfMessage";

    private String identifier;
    private Message jmsMessage;
    private GJmsQueueInfo queueInfos;


    public GJmsMessage() {
        this.initIdentifier();
    }

    public GJmsMessage(Message msg){
        this.jmsMessage = msg;
    }


    public void initIdentifier() {
        if (this.identifier == null) {
            Hashids hashids = new Hashids("JMS_PID", 1);
            long currentTimeMillis = System.currentTimeMillis();
            // Génération du hash
            String hash = hashids.encode(currentTimeMillis);
            this.identifier = hash;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public void acknowledge() throws GJmsException {
        try {
            this.jmsMessage.acknowledge();
        } catch (JMSException e) {
            throw new GJmsException(e);
        }
    }

    public Message buildMessageWithSession(ActiveMQSession session) throws GJmsException {

        Message msg = null;

        try {

                msg = session.createTextMessage("");

        } catch (JMSException e) {
            throw new GJmsException(e);
        }

        return msg;

    }



    @Override
    public String toString() {

        String text = this.getClass().getName().toUpperCase();

        return text;
    }

    public GJmsMessage setQueueInfo(GJmsQueueInfo queueInfos){
        if (queueInfos!=null) {
            this.queueInfos = queueInfos;

            /** On securise afinn de bien reprendre le queueName du message **/
            if (!this.queueInfos.queueName.equals(this.getQueueName()))
                this.queueInfos.queueName = this.getQueueName();
        }

        return this;
    }

    public GJmsQueueInfo getQueueInfo(){
        if (this.queueInfos==null){
            this.queueInfos=new GJmsQueueInfo(this.getQueueName());
        }
        return this.queueInfos;
    }

    public abstract String getQueueName();


    /***
     * Produit le message courant sur la queue définie
     * @return
     * @throws GJmsException
     */
    public GJmsMessage produce() throws GJmsException {
        GJmsProducer producer = new GJmsProducer(new GJmsQueueInfo(this.getQueueName()));
        producer.send(this);
        producer.end();
        //this.setState(MessageStatus.IN_QUEUE);
        return this;
    }

    /***
     * Produit le message courant sur la queue définie
     * @return
     * @throws GJmsException
     */
    public GJmsMessage produceViaRest() throws GJmsException {
        GJmsProducer producer = new GJmsProducer(this.getQueueInfo());
        producer.send(this);
        producer.end();
        //this.setState(MessageStatus.IN_QUEUE);
        return this;
    }



}
