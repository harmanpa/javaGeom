/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import math.geom2d.exceptions.Geom2DException;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

/**
 *
 * @author peter
 * @param <T>
 * @param <X>
 */
public class AbstractFitter<T, X> extends AbstractLeastSquares {

    private final int nParameters;
    private final Function<double[], T> constructor;
    private final Function<T, double[]> destructor;
    private final BiFunction<T, X, Double> assessor;
    private final Function<List<X>, T> guesser;
    private final PrePost<T, X> prePost;

    protected AbstractFitter(int nParameters,
            Function<double[], T> constructor,
            Function<T, double[]> destructor,
            BiFunction<T, X, Double> assessor,
            Function<List<X>, T> guesser) {
        this(nParameters, constructor, destructor, assessor, guesser, null);
    }

    protected AbstractFitter(int nParameters,
            Function<double[], T> constructor,
            Function<T, double[]> destructor,
            BiFunction<T, X, Double> assessor,
            Function<List<X>, T> guesser,
            PrePost<T, X> prePost) {
        this.nParameters = nParameters;
        this.constructor = constructor;
        this.destructor = destructor;
        this.assessor = assessor;
        this.guesser = guesser;
        this.prePost = prePost;
    }

    public T fit(List<X> target, T initial) throws Geom2DException {
        if (prePost == null) {
            return fitInternal(target, initial);
        } else {
            return prePost.post(fitInternal(prePost.pre(target), initial));
        }
    }

    public T fit(List<X> target) throws Geom2DException {
        if (prePost == null) {
            return fitInternal(target, guesser.apply(target));
        } else {
            List<X> processed = prePost.pre(target);
            return prePost.post(fitInternal(processed, guesser.apply(processed)));
        }
    }

    private T fitInternal(List<X> target, T initial) throws Geom2DException {
        if (target.isEmpty()) {
            return initial;
        }

        // the target is to have all points at the specified radius from the center
        double[] prescribedErrors = new double[target.size()];
        Arrays.fill(prescribedErrors, 0.0);

        // least squares problem to solve
        LeastSquaresProblem problem = new LeastSquaresBuilder().
                start(destructor.apply(initial)).
                model(fj(
                        f(constructor, assessor, target),
                        1e-2,
                        target.size(),
                        nParameters)
                ).
                target(prescribedErrors).
                lazyEvaluation(false).
                checkerPair(new SimpleVectorValueChecker(1e-6, 1e-5, 1000)).
                maxIterations(1001).
                maxEvaluations(10000).
                build();
        try {
            LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
            System.out.println("RMS: " + optimum.getRMS());
            System.out.println("evaluations: " + optimum.getEvaluations());
            System.out.println("iterations: " + optimum.getIterations());
            return constructor.apply(optimum.getPoint().toArray());
        } catch (TooManyEvaluationsException | TooManyIterationsException ex) {
            return optifit(target, initial);
        }
    }

    private T optifit(List<X> target, T initial) throws Geom2DException {
        try {
            PointValuePair pvp = new BOBYQAOptimizer(nParameters + 2).optimize(
                    SimpleBounds.unbounded(nParameters),
                    new MaxEval(1000),
                    new MaxIter(1000),
                    GoalType.MINIMIZE,
                    new InitialGuess(destructor.apply(initial)),
                    new ObjectiveFunction(fS(f(constructor, assessor, target))));
            return constructor.apply(pvp.getPoint());
        } catch (TooManyEvaluationsException | TooManyIterationsException ex) {
            throw new Geom2DException("Failed to solve least-squares and optimisation approaches", ex);
        }
    }

    protected MultivariateVectorFunction f(Function<double[], T> constructor, BiFunction<T, X, Double> assessor, List<X> target) {
        return (double[] x) -> {
            double[] out = new double[target.size()];
            T obj = constructor.apply(x);
            for (int j = 0; j < target.size(); j++) {
                out[j] = assessor.apply(obj, target.get(j));
            }
            return out;
        };
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.nParameters;
        hash = 67 * hash + Objects.hashCode(this.constructor);
        hash = 67 * hash + Objects.hashCode(this.destructor);
        hash = 67 * hash + Objects.hashCode(this.assessor);
        hash = 67 * hash + Objects.hashCode(this.guesser);
        hash = 67 * hash + Objects.hashCode(this.prePost);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractFitter<?, ?> other = (AbstractFitter<?, ?>) obj;
        if (this.nParameters != other.nParameters) {
            return false;
        }
        if (!Objects.equals(this.constructor, other.constructor)) {
            return false;
        }
        if (!Objects.equals(this.destructor, other.destructor)) {
            return false;
        }
        if (!Objects.equals(this.assessor, other.assessor)) {
            return false;
        }
        if (!Objects.equals(this.guesser, other.guesser)) {
            return false;
        }
        return Objects.equals(this.prePost, other.prePost);
    }

    protected static interface PrePost<T, X> {

        public List<X> pre(List<X> target);

        public T post(T result);
    }
}
