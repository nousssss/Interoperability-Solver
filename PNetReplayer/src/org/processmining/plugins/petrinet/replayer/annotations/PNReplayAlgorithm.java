/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aadrians Oct 21, 2011
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
//@Inherited
public @interface PNReplayAlgorithm {
	boolean isBasic() default false;
}
