/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d;

/**
 *
 * @author peter
 */
public class Tolerance2D {

    private static Double ACCURACY = 1e-12;
    private static final ThreadLocal<Double> THREAD_ACCURACY = new ThreadLocal<Double>() {
        @Override
        protected Double initialValue() {
            return ACCURACY;
        }
    };

    public static void setGlobal(Double accuracy) {
        ACCURACY = accuracy;
        reset();
    }

    public static void set(Double accuracy) {
        THREAD_ACCURACY.set(accuracy);
    }

    public static Double get() {
        return THREAD_ACCURACY.get();
    }

    public static void reset() {
        THREAD_ACCURACY.remove();
    }
}
