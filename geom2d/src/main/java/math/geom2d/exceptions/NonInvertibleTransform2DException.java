/**
 * 
 */

package math.geom2d.exceptions;

import math.geom2d.exceptions.Geom2DException;
import math.geom2d.transform.Transform2D;

/**
 * Exception thrown when trying to compute an inverse transform of a transform
 * that does not allows this feature.
 * @author dlegland
 */
public class NonInvertibleTransform2DException extends Geom2DException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Transform2D transform;
    
    public NonInvertibleTransform2DException(Transform2D transform) {
        super("");
    	this.transform = transform;
    }
    
    public Transform2D getTransform() {
    	return transform;
    }
}
