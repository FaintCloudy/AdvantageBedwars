package me.faintcloudy.bedwars.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InheritedCommand {
    SenderType sender() default SenderType.ALL;
    String permission() default "";
    String value();
    String usage() default "";
    String description() default "无";
}
