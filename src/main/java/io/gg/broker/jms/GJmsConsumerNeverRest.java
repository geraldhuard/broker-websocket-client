package io.gg.broker.jms;

import io.gg.broker.jms.messages.GJmsMessage;

/**
 * Created by gerald on 09/11/16.
 */
public abstract class GJmsConsumerNeverRest extends GJmsConsumer implements GJmsTemplate.WzConsumerProtocol{


    protected GJmsMessage jmsMessage;
    public GJmsConsumerNeverRest() throws GJmsException {
        super();
    }

    public GJmsConsumerNeverRest(GJmsQueueInfo info) throws GJmsException {
        super(info);

        //this.setDelegate(this).start();
        //this.start();

    }

    public void start() throws GJmsException {
        this.setDelegate(this).start();
        System.out.print("Started on queue !!!");
    }

    public void stop() throws GJmsException {
        this.broker.close();
    }


    public void acknowledgeMessage() throws GJmsException {
        this.jmsMessage.acknowledge();
    }
    @Override
    public void onReceivedMessage(GJmsMessage msg) {
        try {

            this.manageMessage(msg);
            this.preprocess(this.getJmsMessage());
            this.process(this.getJmsMessage());
            this.postprocess(this.getJmsMessage());

            Thread.sleep(1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public GJmsMessage getJmsMessage() {
        return jmsMessage;
    }

    public void manageMessage(GJmsMessage message){
        this.jmsMessage=message;
    }

    public void preprocess(GJmsMessage message){
    }

    public void postprocess(GJmsMessage message){
        try {
            this.jmsMessage.acknowledge();
        } catch (GJmsException e) {
            e.printStackTrace();
        }
    }

}
