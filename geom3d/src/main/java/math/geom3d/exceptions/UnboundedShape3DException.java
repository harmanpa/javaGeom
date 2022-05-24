/**
 *
 */
package math.geom3d.exceptions;

import math.geom3d.Shape3D;

/**
 * Exception thrown when an unbounded shape is involved in an operation that
 * assumes a bounded shape.
 *
 * @author dlegland
 */
public class UnboundedShape3DException extends RuntimeException {

    private Shape3D shape;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnboundedShape3DException(Shape3D shape) {
        this.shape = shape;
    }

    public Shape3D getShape() {
        return shape;
    }
}
