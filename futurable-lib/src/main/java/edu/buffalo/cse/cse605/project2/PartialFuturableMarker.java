package edu.buffalo.cse.cse605.project2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a method call that marks a hashCombination, coordinate pair, etc to be ready to be read by downstairs consumer.
 * 
 * This method is exactly like the {@link PartialFuturableSetter} except does not expect a value to be set just marked as clean.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartialFuturableMarker {
}
