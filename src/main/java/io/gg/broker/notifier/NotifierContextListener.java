package io.gg.broker.notifier;

import io.gg.broker.jms.messages.GJmsMessageNotifier;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/***
 * This class is used to start listening on deployement war
 */
@WebListener
public class NotifierContextListener implements ServletContextListener, GConsumerNotifier.Protocol {


    private AppNotifier app=new AppNotifier();


    /***
     * Called when initalized
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.app.start();
    }

    /***
     * Called when destroy
     * @param servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        this.app.stop();
    }

    @Override
    public void newMessageIncomingFromBroker(GJmsMessageNotifier message) {

    }
}
