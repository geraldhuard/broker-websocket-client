package io.gg.broker.jms;

import io.gg.broker.jms.messages.GJmsMessage;
import io.gg.broker.jms.messages.GJmsMessageFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by gerald on 09/11/16.
 */
public class GJmsActiveMQ implements GJmsTemplate {

    public static final int TIMEOUT=1000;
    public static final int TIME_BETWEEN_TWO_CONSUMATION=0;

    private Connection connection;
    private ActiveMQSession session;
    private MessageProducer messageProducer = null;
    private MessageConsumer messageConsumer = null;
    private Queue queue;
    private QueueReceiver queueReceiver = null;
    private QueueBrowser queueBrowser = null;


    private WzConsumerProtocol delegateConsumer = null;
    private Boolean consumerConsume=false;

    private GJmsQueueInfo openedQueueInfos;


    @Override
    public void open(GJmsQueueInfo infos) throws GJmsException {

        // create a Connection Factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(infos.url);

        try {
            // create a Connection
            this.connection = connectionFactory.createConnection();
            this.connection.start();

            // create a Session
            //this.session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.session = (ActiveMQSession) connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // create the Destination to which messages will be sent
            this.queue = session.createQueue(infos.queueName + "?consumer.prefetchSize=0");

            this.openedQueueInfos = infos;

        }catch (JMSException e){
            throw new GJmsException(e);
        }
        // create a Message Producer for sending messages
    }


    @Override
    public void producer(GJmsQueueInfo infos) throws GJmsException {
        try {
            if (this.queue == null) this.open(infos);
            this.messageProducer = session.createProducer(queue);
        }catch (JMSException e){
            throw new GJmsException(e);
        }
    }

    @Override
    public void consumer(GJmsQueueInfo infos) throws GJmsException {
        try {
            if (this.queue==null)this.open(infos);
            this.messageConsumer = session.createConsumer(queue);


        }catch (JMSException e){


            throw new GJmsException(e);
        }
    }


    @Override
    public void start() throws GJmsException {
        if (this.messageConsumer!=null) {
            if (this.delegateConsumer!=null){
                this.startConsumation();
            }
        }

    }


    @Override
    public void send(GJmsMessage message) throws GJmsException {
        try {
            Message messageToSend = message.buildMessageWithSession(this.session);
            if (messageToSend != null)
                messageProducer.send(messageToSend);
        }catch (JMSException e){
            throw new GJmsException(e);
        } catch (GJmsException e) {
            e.printStackTrace();
        }
    }





    public GJmsMessage consume() {
        Message message = null;
        try {

            message = messageConsumer.receive();
            if (message != null) {
                try {
                    return GJmsMessageFactory.createFromMessage(message);
                }catch (GJmsException e){
                    this.delegateConsumer.onErrorCreateMessage(e, message);
                }
            }
            else
                return null;
        }catch (JMSException e){
            /*** Si le consumer a été fermé ****/
            if (e.getMessage().equals("The Consumer is closed")) {
                this.consumerConsume = false;

                System.out.println("Consumer closed... try to re-open");
                if (this.openedQueueInfos != null) {
                    try {
                        this.open(this.openedQueueInfos);
                    } catch (GJmsException e1) {
                        e1.printStackTrace();
                    }
                }
                /**********************************/
            } else
                this.delegateConsumer.onErrorCreateMessage(new GJmsException(e), message);
        }
        return null;
    }

    @Override
    public void setConsumerDelegate(WzConsumerProtocol delegate) {
        this.delegateConsumer=delegate;
    }




    private void consumePendingMessage() throws GJmsException {
        try {
            /*** Check s'il reste des messages non consommés ***/
            this.queueBrowser = this.session.createBrowser(this.queue);

            Enumeration enu = this.queueBrowser.getEnumeration();
            List list = new ArrayList();
            while (enu.hasMoreElements()) {
                Message message = (Message) enu.nextElement();
                if (message != null) {
                    this.delegateConsumer.onReceivedMessage(GJmsMessageFactory.createFromMessage(message));
                    message.setJMSMessageID("consumedPending");
                    message.acknowledge();
                }
            }
        }catch (JMSException e){
            throw new GJmsException(e);
        }
    }

    private void startConsumation() throws GJmsException {
        this.consumerConsume=true;

        //this.consumePendingMessage();


        /*** Nouveau message ***/
        while (this.consumerConsume){
            try {

                GJmsMessage msg=this.consume();
                if (msg!=null) {
                    System.out.println("## New message "+msg);
                    this.delegateConsumer.onReceivedMessage(msg);
                }

                Thread.sleep(TIME_BETWEEN_TWO_CONSUMATION);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    private void startConsumation2() throws GJmsException {
        try {


            while (this.consumerConsume) {
                Message msg = queueReceiver.receive();

                if (msg!=null)
                    this.delegateConsumer.onReceivedMessage(GJmsMessageFactory.createFromMessage(msg));
            }


        }catch (JMSException e){
            throw new GJmsException(e);
        }

    }




    @Override
    public void close() throws GJmsException {
        this.consumerConsume=false;

        try {
            if (this.messageConsumer != null) this.messageConsumer.close();
            if (this.messageProducer != null) this.messageProducer.close();

            if (this.session != null) this.session.close();
            if (this.connection != null) this.connection.close();
        }catch (JMSException e){
            throw new GJmsException(e);
        }
    }
}
