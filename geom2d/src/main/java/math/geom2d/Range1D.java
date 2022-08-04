/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom2d;

/**
 *
 * @author peter
 */
public class Range1D {

    private final double min;
    private final double max;

    public Range1D(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static Range1D bounds(Shape2D shape, Vector2D direction) {
        Box2D box = shape.transform(AffineTransform2D.createRotation(Angle2D.angle(direction.normalize(), new Vector2D(0, 1))))
                .boundingBox();
        return new Range1D(box.getMinY(), box.getMaxY());
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

}
