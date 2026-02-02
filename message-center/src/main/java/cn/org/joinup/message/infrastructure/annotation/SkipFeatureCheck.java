package cn.org.joinup.message.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to skip feature access check in FeatureAccessInterceptor.
 * Use this on controller methods that are public or have their own auth mechanism (e.g., token).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipFeatureCheck {
}
