package io.gg.broker.jms;

/**
 * Created by gerald on 28/02/17.
 */
public class GJmsConsumerThread extends Thread{
    protected GJmsConsumerNeverRest consumer;


    public GJmsConsumerThread(GJmsConsumerNeverRest consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try {
            this.consumer.start();
        } catch (GJmsException e) {
            e.printStackTrace();
        }
    }

    public void end(){
        try {
            this.consumer.stop();
        } catch (GJmsException e) {
            e.printStackTrace();
        }
    }
}
