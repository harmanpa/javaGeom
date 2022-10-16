/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.csg.fitting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import math.geom3d.Box3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.CSG;
import math.geom3d.csg.primitives.Cube;
import math.geom3d.csg.primitives.Cylinder;
import math.geom3d.csg.primitives.Primitive;
import math.geom3d.csg.primitives.Sphere;
import math.geom3d.quickhull.QuickHullException;
import math.geom3d.transform.AffineTransform3D;
import math.geom3s.Vector3S;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

/**
 *
 * @author peter
 */
public class PrimitiveFitter {

    public static TransformedPrimitive fit(CSG mesh) {
        TransformedPrimitive best;
        double bestError;
        TransformedPrimitive sphere = fitSphere(mesh);
        double sphereError = PrimitiveFactory.error(mesh, sphere);
        best = sphere;
        bestError = sphereError;
        TransformedPrimitive cube = fitCube(mesh);
        double cubeError = PrimitiveFactory.error(mesh, cube);
        if (cubeError < bestError) {
            best = cube;
            bestError = cubeError;
        }
        TransformedPrimitive cylinder = fitCylinder(mesh);
        double cylinderError = PrimitiveFactory.error(mesh, cylinder);
        if (cylinderError < bestError) {
            best = cylinder;
            bestError = cylinderError;
        }
        return best;
    }

    public static TransformedPrimitive fitSphere(CSG mesh) {
        return fit(mesh, sphereFactory());
    }

    public static TransformedPrimitive fitCube(CSG mesh) {
        return fit(mesh, cubeFactory());
    }

    public static TransformedPrimitive fitCylinder(CSG mesh) {
        return fit(mesh, cylinderFactory());
    }

    static TransformedPrimitive fit(CSG mesh, PrimitiveFactory factory) {
        return fit(mesh, factory, new BOBYQAOptimizer(factory.dimension() + 2));
    }

    static TransformedPrimitive fit(CSG mesh, PrimitiveFactory factory, MultivariateOptimizer optimiser) {
        List<OptimizationData> parameters = new ArrayList<>();
        PrimitiveLimits limits = factory.limits(mesh.boundingBox());
        parameters.add(new InitialGuess(limits.getInitialGuess()));
        parameters.add(new SimpleBounds(limits.getLowerBounds(), limits.getUpperBounds()));
        parameters.add(GoalType.MINIMIZE);
        parameters.add(new MaxEval(500));
        parameters.add(new MaxIter(200));
        parameters.add(new ObjectiveFunction((double[] x) -> factory.error(mesh, x)));
        try {
            PointValuePair pvp = optimiser.optimize(parameters.toArray(new OptimizationData[0]));
            return factory.make(pvp.getPoint());
        } catch (MathIllegalStateException ex) {
            return null;
        }
    }

    static PrimitiveFactory sphereFactory() {
        return new PrimitiveFactory(
                (Box3D bounds) -> new PrimitiveLimits(
                        new double[]{
                            bounds.getMinX(),
                            bounds.getMinY(),
                            bounds.getMinZ(),
                            0.0
                        },
                        new double[]{
                            bounds.getMaxX(),
                            bounds.getMaxY(),
                            bounds.getMaxZ(),
                            (bounds.getWidth() + bounds.getHeight()) / 2
                        },
                        new double[]{
                            bounds.getCenter().getX(),
                            bounds.getCenter().getY(),
                            bounds.getCenter().getZ(),
                            (bounds.getWidth() + bounds.getHeight()) / 4
                        }),
                (double[] x) -> new Sphere(x[0]),
                1,
                false);
    }

    static PrimitiveFactory cylinderFactory() {
        return new PrimitiveFactory(
                (Box3D bounds) -> new PrimitiveLimits(
                        new double[]{
                            bounds.getMinX(),
                            bounds.getMinY(),
                            bounds.getMinZ(),
                            0.0,
                            -Math.PI,
                            0.0,
                            0.0
                        },
                        new double[]{
                            bounds.getMaxX(),
                            bounds.getMaxY(),
                            bounds.getMaxZ(),
                            Math.PI,
                            Math.PI,
                            Math.hypot(bounds.getWidth(), bounds.getHeight()),
                            Math.hypot(bounds.getWidth(), bounds.getHeight())
                        },
                        new double[]{
                            bounds.getCenter().getX(),
                            bounds.getCenter().getY(),
                            bounds.getMinZ(),
                            0,
                            0,
                            bounds.getWidth() / 2,
                            bounds.getHeight()
                        }),
                (double[] x) -> new Cylinder(x[0], x[1]),
                2,
                true);
    }

    static PrimitiveFactory cubeFactory() {
        return new PrimitiveFactory(
                (Box3D bounds) -> new PrimitiveLimits(
                        new double[]{
                            bounds.getMinX(),
                            bounds.getMinY(),
                            bounds.getMinZ(),
                            0.0,
                            -Math.PI,
                            0.0,
                            0.0,
                            0.0
                        },
                        new double[]{
                            bounds.getMaxX(),
                            bounds.getMaxY(),
                            bounds.getMaxZ(),
                            Math.PI,
                            Math.PI,
                            Math.hypot(Math.hypot(bounds.getWidth(), bounds.getHeight()), bounds.getDepth()),
                            Math.hypot(Math.hypot(bounds.getWidth(), bounds.getHeight()), bounds.getDepth()),
                            Math.hypot(Math.hypot(bounds.getWidth(), bounds.getHeight()), bounds.getDepth())
                        },
                        new double[]{
                            bounds.getCenter().getX(),
                            bounds.getCenter().getY(),
                            bounds.getCenter().getZ(),
                            0.0,
                            0.0,
                            bounds.getWidth(),
                            bounds.getHeight(),
                            bounds.getDepth()
                        }),
                (double[] x) -> new Cube(x[0], x[1], x[2]),
                3,
                true);
    }

    public static class TransformedPrimitive {

        private final Primitive primitive;
        private final AffineTransform3D transform;

        TransformedPrimitive(Primitive primitive, AffineTransform3D transform) {
            this.primitive = primitive;
            this.transform = transform;
        }

        public Primitive getPrimitive() {
            return primitive;
        }

        public AffineTransform3D getTransform() {
            return transform;
        }

    }

    public static class PrimitiveFactory {

        private final Function<Box3D, PrimitiveLimits> limits;
        private final Function<double[], Primitive> f;
        private final int primitiveDimension;
        private final boolean includeRotation;

        PrimitiveFactory(Function<Box3D, PrimitiveLimits> limits, Function<double[], Primitive> f, int primitiveDimension, boolean includeRotation) {
            this.limits = limits;
            this.f = f;
            this.primitiveDimension = primitiveDimension;
            this.includeRotation = includeRotation;
        }

        public PrimitiveLimits limits(Box3D bounds) {
            return limits.apply(bounds);
        }

        public boolean includeRotation() {
            return includeRotation;
        }

        public int dimension() {
            return primitiveDimension + (includeRotation() ? 5 : 3);
        }

        /**
         * Uses first 3 (without rotation) or 5 (with rotation) parameters to
         * make a 3D transform
         *
         * @param parameters
         * @return
         */
        public AffineTransform3D makeTransform(double[] parameters) {
            AffineTransform3D translation = AffineTransform3D.createTranslation(parameters[0], parameters[1], parameters[2]);
            if (includeRotation()) {
                AffineTransform3D rotation = Vector3S.fromCartesian(new Vector3D(0, 0, 1)).transformTo(new Vector3S(parameters[3], parameters[4]));
                return translation.preConcatenate(rotation);
            }
            return translation;
        }

        public TransformedPrimitive make(double[] parameters) {
            return new TransformedPrimitive(
                    f.apply(Arrays.copyOfRange(parameters, parameters.length - primitiveDimension, parameters.length)),
                    makeTransform(parameters));
        }

        public double error(CSG mesh, double[] parameters) {
            return error(mesh, make(parameters));
        }

        public static double error(CSG mesh, TransformedPrimitive primitive) {
            if (primitive != null) {
                try {
                    CSG primitiveMesh = primitive.getPrimitive().toCSG()
                            .transform(primitive.getTransform());
                    return mesh.difference(primitiveMesh).computeVolume();
                } catch (QuickHullException ex) {
                }
            }
            return Double.MAX_VALUE;
        }
    }

    public static class PrimitiveLimits {

        private final double[] lowerBounds;
        private final double[] upperBounds;
        private final double[] initialGuess;

        PrimitiveLimits(double[] lowerBounds, double[] upperBounds, double[] initialGuess) {
            this.lowerBounds = lowerBounds;
            this.upperBounds = upperBounds;
            this.initialGuess = initialGuess;
        }

        public double[] getLowerBounds() {
            return lowerBounds;
        }

        public double[] getUpperBounds() {
            return upperBounds;
        }

        public double[] getInitialGuess() {
            return initialGuess;
        }

    }
}
