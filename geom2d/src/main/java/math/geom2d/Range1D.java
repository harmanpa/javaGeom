/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom2d;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;

/**
 *
 * @author peter
 */
public class Range1D implements Comparable<Range1D> {

    private final double min;
    private final double max;

    public Range1D(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static Range1D fromStatistics(DoubleSummaryStatistics dss) {
        return new Range1D(dss.getMin(), dss.getMax());
    }

    public static Range1D fromValues(DoubleStream values) {
        return fromStatistics(values.summaryStatistics());
    }

    public static Range1D bounds(Shape2D shape, Vector2D direction) {
        Box2D box = shape.transform(AffineTransform2D.createRotation(Angle2D.angle(direction.normalize(), new Vector2D(0, 1))))
                .boundingBox();
        return new Range1D(box.getMinY(), box.getMaxY());
    }

    public double getMin() {
        return min;
    }

    @JsonIgnore
    public double getMid() {
        return (getMin() + getMax()) / 2.0;
    }

    @JsonIgnore
    public double getLength() {
        return getMax() - getMin();
    }

    public double getMax() {
        return max;
    }

    /**
     * Returns true if the ranges overlap
     *
     * @param other
     * @return
     */
    public boolean isOverlapping(Range1D other) {
        return this.getMax() >= other.getMin() && this.getMin() <= other.getMax();
    }

    /**
     * Calculates the smallest gap between the ranges, or a negative value for
     * the smallest overlap
     *
     * @param other
     * @return
     */
    public double distance(Range1D other) {
        if (getMax() <= other.getMin()) {
            return other.getMin() - getMax();
        } else if (getMin() >= other.getMax()) {
            return getMin() - other.getMax();
        } else {
            return -1 * Math.min(getMax() - other.getMin(), other.getMax() - getMin());
        }
    }

    @Override
    public int compareTo(Range1D other) {
        if (getMax() <= other.getMin()) {
            return -1;
        } else if (getMin() >= other.getMax()) {
            return 1;
        } else {
            return 0;
        }
    }

}
