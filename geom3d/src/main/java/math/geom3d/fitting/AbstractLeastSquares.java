/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.stream.DoubleStream;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/**
 *
 * @author peter
 */
public abstract class AbstractLeastSquares {

    protected MultivariateFunction fS(MultivariateVectorFunction fv) {
        return (double[] x) -> DoubleStream.of(fv.value(x)).map(v -> Math.pow(v, 2.0)).sum();
    }

    protected MultivariateJacobianFunction fj(MultivariateVectorFunction f, double increment, int d, int p) {
        return (RealVector x) -> {
            RealVector value = new ArrayRealVector(d);
            RealMatrix jacobian = new Array2DRowRealMatrix(d, p);
            double[] a = f.value(x.toArray());
            for (int j = 0; j < d; j++) {
                value.setEntry(j, a[j]);
            }
            jacobian(f, jacobian, x, increment, d, p);
            return new Pair<>(value, jacobian);
        };
    }

    protected void jacobian(MultivariateVectorFunction f, RealMatrix jacobian, RealVector x, double increment, int d, int p) {
        for (int i = 0; i < p; i++) {
            double v = x.getEntry(i);
            x.setEntry(i, v + increment / 2);
            double[] a = f.value(x.toArray());
            x.setEntry(i, v - increment / 2);
            double[] b = f.value(x.toArray());
            for (int j = 0; j < d; j++) {
                jacobian.setEntry(j, i, (a[j] - b[j]) / increment);
            }
            x.setEntry(i, v);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
