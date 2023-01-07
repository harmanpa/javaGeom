/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class RoundingTest {

    public static final int ITERS = 1000 * 1000;

    @Test
    public void test() {
        for (int i = 0; i < 3; i++) {
            perfRoundTo3();
            perfCastRoundTo3();
            perfBdRoundTo3();
        }
    }

    private static double perfRoundTo3() {
        double sum = 0.0;
        long start = 0;
        for (int i = -20000; i < ITERS; i++) {
            if (i == 0) {
                start = System.nanoTime();
            }
            sum += roundTo3(i * 1e-4);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Took %,d ns per round%n", time / ITERS);
        return sum;
    }

    private static double perfCastRoundTo3() {
        double sum = 0.0;
        long start = 0;
        for (int i = -20000; i < ITERS; i++) {
            if (i == 0) {
                start = System.nanoTime();
            }
            sum += castRoundTo3(i * 1e-4);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Took %,d ns per cast round%n", time / ITERS);
        return sum;
    }

    private static double perfBdRoundTo3() {
        double sum = 0.0;
        long start = 0;
        for (int i = -20000; i < ITERS; i++) {
            if (i == 0) {
                start = System.nanoTime();
            }
            sum += bdroundTo3(i * 1e-4);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Took %,d ns per BigDecimal round%n", time / ITERS);
        return sum;
    }

    public static double roundTo3(double d) {
        return Math.round(d * 1000 + 0.5) / 1000.0;
    }

    public static double bdroundTo3(double d) {
        return round(d).doubleValue();
    }

    public static BigDecimal round(BigDecimal bd) {
        return bd.setScale(3, RoundingMode.HALF_UP);
    }

    public static BigDecimal round(Double d) {
        return round(new BigDecimal(d, MathContext.UNLIMITED));
    }

    public static double castRoundTo3(double d) {
        return (long) (d * 1000.0 + 0.5) / 1000.0;
    }
}
