package io.gg.broker.jms.messages;

import io.gg.broker.jms.GJmsException;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.Map;

/**
 * Created by gerald on 22/11/16.
 */
public class GJmsMessageFactory {

    public static GJmsMessage createFromMessage(Message message) throws GJmsException {
        try {
            String messageType=message.getStringProperty(GJmsMessage.PROPERTYNAME_TYPE_MESSAGE);

            GJmsMessage wzmessage=null;
            wzmessage=new GJmsMessageNotifier(message);

            if (wzmessage==null)
                System.out.println("## Consumer ERROR - Message de type ["+messageType+"] not created in GJmsMessageFactory !!!!");

            return wzmessage;

        } catch (JMSException e){
            e.printStackTrace();
        }
        return null;
    }

    public static GJmsMessage createFromHeaders(Map<String, List<String>> headers) throws GJmsException {
        GJmsMessage wzmessage=null;

        List<String> list=headers.get("WzJmsMessageType");
        String messageType=list.get(0);



        return wzmessage;

    }



}
