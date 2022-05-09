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
import math.geom2d.Tolerance2D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 * @param <T>
 */
public class Shape3DFitter<T extends Shape3D> extends AbstractFitter<T, Point3D> {

    protected Shape3DFitter(int nParameters, Function<double[], T> constructor, Function<T, double[]> destructor, Function<List<Point3D>, T> guesser, boolean centred) {
        super(nParameters, constructor, destructor, (shape, point) -> shape.distance(point), guesser, centred ? new CentredPrePost<T>() : null);
    }

    protected static boolean valuesSame(List<Point3D> points, ToDoubleFunction<Point3D> getter) {
        DoubleSummaryStatistics stats = points.stream().mapToDouble(getter).summaryStatistics();
        return stats.getMax() - stats.getMin() <= Tolerance2D.get();
    }

    protected static double average(List<Point3D> points, ToDoubleFunction<Point3D> getter) {
        return points.stream().mapToDouble(getter).average().orElse(0);
    }

    protected static Point3D center(List<Point3D> points) {
        return new Point3D(average(points, Point3D::getX), average(points, Point3D::getY), average(points, Point3D::getZ));
    }

    protected static class CentredPrePost<T extends Shape3D> implements PrePost<T, Point3D> {

        private AffineTransform3D transform;

        @Override
        public List<Point3D> pre(List<Point3D> target) {
            Vector3D center = center(target).asVector();
            AffineTransform3D centerer = AffineTransform3D.createTranslation(center.opposite());
            transform = AffineTransform3D.createTranslation(center);
            return target.stream().map(p -> p.transform(centerer)).collect(Collectors.toList());
        }

        @Override
        public T post(T result) {
            return (T) result.transform(transform);
        }

    }
}
