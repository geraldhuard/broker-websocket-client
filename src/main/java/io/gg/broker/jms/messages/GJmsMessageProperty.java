package io.gg.broker.jms.messages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gerald on 22/11/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //can use in method only.
public @interface GJmsMessageProperty {
    public final static String TAKE_SAME_FIELD_NAME="";

    String name() default TAKE_SAME_FIELD_NAME;

    Class subclass() default Object.class;

}
