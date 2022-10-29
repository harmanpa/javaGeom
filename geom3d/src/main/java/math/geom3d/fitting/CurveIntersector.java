/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.Arrays;
import java.util.List;
import math.geom2d.Tolerance2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.line.LinearShape3D;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;

/**
 *
 * @author peter
 */
public class CurveIntersector extends AbstractLeastSquares {

    public static Point3D intersect(Curve3D... curves) {
        return intersect(Arrays.asList(curves));
    }

    public static Point3D intersect(Point3D start, Curve3D... curves) {
        return intersect(start, Arrays.asList(curves));
    }

    public static Point3D intersect(List<Curve3D> curves) {
        return intersect(midpoint(curves), curves);
    }

    public static Point3D intersect(Point3D start, List<Curve3D> curves) {
        if (curves.size() <= 1) {
            return null;
        }
        for (int i = 0; i < curves.size(); i++) {
            for (int j = i + 1; j < curves.size(); j++) {
                if (parallel(curves.get(i), curves.get(j))) {
                    return null;
                }
            }
        }
        return new CurveIntersector().intersectInternal(curves, start);
    }

    protected static Point3D midpoint(List<Curve3D> curves) {
        return Point3D.midpoint(curves.stream()
                .map(curve -> Point3D.midpoint(curve.firstPoint(), curve.lastPoint()))
                .toArray(Point3D[]::new));
    }

    protected static boolean parallel(Curve3D a, Curve3D b) {
        if (a instanceof LinearShape3D && b instanceof LinearShape3D) {
            return Vector3D.isColinear(((LinearShape3D) a).direction(), ((LinearShape3D) b).direction());
        }
        return false;
    }

    protected Point3D intersectInternal(List<Curve3D> curves, Point3D start) {
        double[] prescribedErrors = new double[curves.size() - 1];
        Arrays.fill(prescribedErrors, 0.0);
        LeastSquaresProblem problem = new LeastSquaresBuilder().
                start(curves.stream().mapToDouble(curve -> curve.project(start)).toArray()).
                model(fj(
                        f(curves),
                        1e-2,
                        curves.size() - 1,
                        curves.size())
                ).
                target(prescribedErrors).
                lazyEvaluation(false).
                checkerPair(new SimpleVectorValueChecker(1e-6, 1e-5, 1000)).
                maxIterations(1001).
                maxEvaluations(10000).
                build();
        try {
            LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
//            System.out.println("RMS: " + optimum.getRMS());
//            System.out.println("evaluations: " + optimum.getEvaluations());
//            System.out.println("iterations: " + optimum.getIterations());
            if (optimum.getRMS() <= Math.pow(Tolerance2D.get(), 2)) {
                for (int i = 0; i < curves.size(); i++) {
                    if (curves.get(i).getT0() > optimum.getPoint().toArray()[i]
                            || curves.get(i).getT1() < optimum.getPoint().toArray()[i]) {
                        // Point was not contained in one of the lines
                        return null;
                    }
                }
                return curves.get(0).point(optimum.getPoint().toArray()[0]);
            }
        } catch (TooManyEvaluationsException | TooManyIterationsException ex) {
        }
        return null;
    }

    protected MultivariateVectorFunction f(List<Curve3D> curves) {
        return (double[] x) -> {
            double[] out = new double[curves.size() - 1];
            for (int j = 0; j < curves.size() - 1; j++) {
                out[j] = curves.get(j + 1).point(x[j + 1]).distance(curves.get(0).point(x[0]));
            }
            return out;
        };
    }
}
