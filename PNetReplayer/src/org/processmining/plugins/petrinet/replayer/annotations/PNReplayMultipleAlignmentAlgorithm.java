/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aadrians
 * Mar 2, 2013
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PNReplayMultipleAlignmentAlgorithm {
}
