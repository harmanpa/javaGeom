/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import math.geom2d.circulinear.CirculinearCurve2D;

/**
 *
 * @author peter
 */
public class Tolerance2D {

    private static Double ACCURACY = 1e-12;
    private static final ThreadLocal<Integer> SCALE = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new BigDecimal(Tolerance2D.get().toString()).scale();
        }
    };

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
        SCALE.remove();
    }

    public static Double get() {
        double result = THREAD_ACCURACY.get();
        return result;
    }

    public static Double getRelative(CirculinearCurve2D curve) {
        return getRelative(curve.length());
    }

    public static Double getRelative(double length) {
        return Math.max(1e-12, get() / length);
    }

    public static void reset() {
        THREAD_ACCURACY.remove();
        SCALE.remove();
    }

    public static BigDecimal round(BigDecimal bd) {
        return bd.setScale(scale(), RoundingMode.HALF_UP);
    }

    public static BigDecimal round(Double d) {
        return round(new BigDecimal(d, MathContext.UNLIMITED));
    }

    static Integer scale() {
        return SCALE.get();
    }
}
