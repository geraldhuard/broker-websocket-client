package io.gg.broker.notifier;

import io.gg.broker.jms.GJmsConsumerNeverRest;
import io.gg.broker.jms.GJmsQueueInfo;
import io.gg.broker.jms.messages.GJmsMessageNotifier;
import io.gg.broker.jms.GJmsException;
import io.gg.broker.jms.messages.GJmsMessage;

import javax.jms.Message;


public class GConsumerNotifier extends GJmsConsumerNeverRest {

    public interface Protocol{
        public void newMessageIncomingFromBroker(GJmsMessageNotifier message);
    }

    private Protocol delegate;


    public GConsumerNotifier(Protocol delegate) throws GJmsException {
        super();
        this.delegate=delegate;
        this.setQueueInfo(new GJmsQueueInfo(GJmsQueueInfo.QUEUE_NAME_NOTIFIER));
    }

    @Override
    public void process(GJmsMessage message) {
        if (this.delegate!=null && message instanceof GJmsMessageNotifier){
            this.delegate.newMessageIncomingFromBroker((GJmsMessageNotifier)message);
        }
    }

    @Override
    public void onErrorCreateMessage(GJmsException e, Message message) {

    }

    @Override
    public void postprocess(GJmsMessage message) {

    }
}
