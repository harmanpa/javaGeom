/**
 *
 */
package math.geom2d.exceptions;

import math.geom2d.Shape2D;
import math.geom2d.exceptions.Geom2DException;

/**
 * Exception thrown when an unbounded shape is involved in an operation that
 * assumes a bounded shape.
 *
 * @author dlegland
 */
public class UnboundedShape2DException extends RuntimeException {

    private Shape2D shape;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnboundedShape2DException(Shape2D shape) {
        this.shape = shape;
    }

    public Shape2D getShape() {
        return shape;
    }
}
