/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Spliterator.OfDouble;
import math.geom2d.Point2D;
import math.geom2d.exceptions.Geom2DException;
import math.geom3d.Point3D;
import math.geom3d.fitting.Plane3DFitter;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class FitPlaneTest {

//    @Test
//    public void testCircle() {
//        final double radius = 70.0;
//        final Point2D[] observedPoints = new Point2D[]{
//            new Point2D(30.0, 68.0),
//            new Point2D(50.0, -6.0),
//            new Point2D(110.0, -20.0),
//            new Point2D(35.0, 15.0),
//            new Point2D(45.0, 97.0)
//        };
//
//        // the model function components are the distances to current estimated center,
//        // they should be as close as possible to the specified radius
//        MultivariateJacobianFunction distancesToCurrentCenter = new MultivariateJacobianFunction() {
//            public Pair<RealVector, RealMatrix> value(final RealVector point) {
//
//                Point2D center = new Point2D(point.getEntry(0), point.getEntry(1));
//
//                RealVector value = new ArrayRealVector(observedPoints.length);
//                RealMatrix jacobian = new Array2DRowRealMatrix(observedPoints.length, 2);
//
//                for (int i = 0; i < observedPoints.length; ++i) {
//                    Point2D o = observedPoints[i];
//                    double modelI = o.distance(center);
//                    value.setEntry(i, modelI);
//                    // derivative with respect to p0 = x center
//                    jacobian.setEntry(i, 0, (center.getX() - o.getX()) / modelI);
//                    // derivative with respect to p1 = y center
//                    jacobian.setEntry(i, 1, (center.getY() - o.getY()) / modelI);
//                }
//
//                return new Pair<RealVector, RealMatrix>(value, jacobian);
//
//            }
//        };
//
//        // the target is to have all points at the specified radius from the center
//        double[] prescribedDistances = new double[observedPoints.length];
//        Arrays.fill(prescribedDistances, radius);
//
//        // least squares problem to solve : modeled radius should be close to target radius
//        LeastSquaresProblem problem = new LeastSquaresBuilder().
//                start(new double[]{100.0, 50.0}).
//                model(distancesToCurrentCenter).
//                target(prescribedDistances).
//                lazyEvaluation(false).
//                maxEvaluations(1000).
//                maxIterations(1000).
//                build();
//        LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
//        Point2D fittedCenter = new Point2D(optimum.getPoint().getEntry(0), optimum.getPoint().getEntry(1));
//        System.out.println("fitted center: " + fittedCenter.getX() + " " + fittedCenter.getY());
//        System.out.println("RMS: " + optimum.getRMS());
//        System.out.println("evaluations: " + optimum.getEvaluations());
//        System.out.println("iterations: " + optimum.getIterations());
//    }
    @Test
    public void testPlane() throws Geom2DException {
        // Construct a plane to generate accurate fitting data
        Plane3D examplePlane = Plane3D.createXZPlane();
        // Generate some random 2D points and turn into 3D
        Random r = new Random();
        int n = 80;
        List<Point3D> observedPoints = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            observedPoints.add(examplePlane.point(new Point2D(r.nextDouble(), r.nextDouble())));
        }

        Plane3DFitter fitter = new Plane3DFitter();
        Plane3D plane = fitter.fit(observedPoints);
        System.out.println(examplePlane.normal());
        System.out.println(examplePlane.dist());
        System.out.println(plane.normal());
        System.out.println(plane.dist());
        System.out.println(plane.normal().angle(examplePlane.normal()));
        // the model function components are the distances to current estimated center,
        // they should be as close as possible to the specified radius
//        MultivariateJacobianFunction planeError = new MultivariateJacobianFunction() {
//            public Pair<RealVector, RealMatrix> value(final RealVector point) {
//                RealVector value = new ArrayRealVector(n + 1);
//                RealMatrix jacobian = new Array2DRowRealMatrix(n + 1, 4);
//
//                for (int i = 0; i < n; ++i) {
//                    Point3D o = observedPoints.get(i);
//                    double modelI = point.getEntry(0) * o.getX()
//                            + point.getEntry(1) * o.getY()
//                            + point.getEntry(2) * o.getZ()
//                            - point.getEntry(3);
//                    value.setEntry(i, modelI);
//                    jacobian.setEntry(i, 0, o.getX());
//                    jacobian.setEntry(i, 1, o.getY());
//                    jacobian.setEntry(i, 2, o.getZ());
//                    jacobian.setEntry(i, 3, -1);
//                }
//                double modelI = point.getEntry(0) * point.getEntry(0)
//                        + point.getEntry(1) * point.getEntry(1)
//                        + point.getEntry(2) * point.getEntry(2)
//                        - 1;
//                value.setEntry(n, modelI);
//                jacobian.setEntry(n, 0, point.getEntry(0));
//                jacobian.setEntry(n, 1, point.getEntry(1));
//                jacobian.setEntry(n, 2, point.getEntry(2));
//                jacobian.setEntry(n, 3, 0);
//
//                return new Pair<RealVector, RealMatrix>(value, jacobian);
//
//            }
//        };
//
//        // the target is to have all points at the specified radius from the center
//        double[] prescribedErrors = new double[n + 1];
//        Arrays.fill(prescribedErrors, 0.0);
//
//        // least squares problem to solve : modeled radius should be close to target radius
//        LeastSquaresProblem problem = new LeastSquaresBuilder().
//                start(new double[]{0, 0, 1, 0}).
//                model(planeError).
//                target(prescribedErrors).
//                lazyEvaluation(false).
//                maxEvaluations(1000).
//                maxIterations(1000).
//                build();
//        LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
//        Plane3D fittedPlane = Plane3D.fromABCD(new double[]{
//            optimum.getPoint().getEntry(0), optimum.getPoint().getEntry(1),
//            optimum.getPoint().getEntry(2), optimum.getPoint().getEntry(3)});
//        System.out.println("fitted plane normal: " + fittedPlane.normal());
//        System.out.println("RMS: " + optimum.getRMS());
//        System.out.println("evaluations: " + optimum.getEvaluations());
//        System.out.println("iterations: " + optimum.getIterations());
    }
}
