package io.gg.broker.jms;

import javax.jms.JMSException;
import java.io.IOException;

/**
 * Created by gerald on 10/11/16.
 */
public class GJmsException extends java.lang.Throwable{
    private JMSException jmsException;
    private IOException ioException;

    public GJmsException(String reason) {
        super(reason);
    }


    public GJmsException(JMSException e) {
        super(e.getMessage());
        this.jmsException=e;
    }
    public GJmsException(IOException e) {
        super(e.getMessage());
        this.ioException=e;
    }


}
