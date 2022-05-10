/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Tolerance2D;
import math.geom2d.Vector2D;

/**
 *
 * @author peter
 * @param <T>
 */
public class Shape2DFitter<T extends Shape2D> extends AbstractFitter<T, Point2D> {

    public Shape2DFitter(int nParameters, Function<double[], T> constructor, Function<T, double[]> destructor, Function<List<Point2D>, T> guesser, boolean centred) {
        super(nParameters, constructor, destructor, (shape, point) -> shape.distance(point), guesser, centred ? new CentredPrePost<>() : null);
    }

    protected static boolean valuesSame(List<Point2D> points, ToDoubleFunction<Point2D> getter) {
        DoubleSummaryStatistics stats = points.stream().mapToDouble(getter).summaryStatistics();
        return stats.getMax() - stats.getMin() <= Tolerance2D.get();
    }

    protected static double average(List<Point2D> points, ToDoubleFunction<Point2D> getter) {
        return points.stream().mapToDouble(getter).average().orElse(0);
    }

    protected static Point2D center(List<Point2D> points) {
        return new Point2D(average(points, Point2D::getX), average(points, Point2D::getY));
    }

    protected static class CentredPrePost<T extends Shape2D> implements PrePost<T, Point2D> {

        private AffineTransform2D transform;

        @Override
        public List<Point2D> pre(List<Point2D> target) {
            Vector2D center = new Vector2D(center(target));
            AffineTransform2D centerer = AffineTransform2D.createTranslation(center.opposite());
            transform = AffineTransform2D.createTranslation(center);
            return target.stream().map(p -> p.transform(centerer)).collect(Collectors.toList());
        }

        @Override
        public T post(T result) {
            return (T) result.transform(transform);
        }

    }
}
