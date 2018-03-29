package io.gg.broker.jms;

/**
 * Created by gerald on 09/11/16.
 */
public class GJmsObject {


    protected GJmsTemplate broker;



    public void end(){
        try {
            this.broker.close();
        }catch (GJmsException e){}
    }



}
