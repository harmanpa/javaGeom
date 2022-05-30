/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Stream;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.conic.Circle2D;

/**
 *
 * @author peter
 */
public class FittingUtils {

    public static <T> Stream<T[]> sequentials(Class<T> type, List<T> list, int each) {
        return sequentials(type, list, each, true);
    }

    public static <T> Stream<T[]> sequentials(Class<T> type, List<T> list, int each, final boolean looping) {
        return sequentials(type, list, each, looping, 0);
    }

    public static <T> Stream<T[]> sequentials(Class<T> type, List<T> list, int each, final boolean looping, final int start) {
        return indexStream(list.size(), each, looping, start).map(indices -> {
            T[] arr = (T[]) Array.newInstance(type, each);
            for (int i = 0; i < indices.length; i++) {
                arr[i] = list.get(indices[i]);
            }
            return arr;
        });
    }

    public static Stream<int[]> indexStream(final int n, final int each) {
        return indexStream(n, each, true);
    }

    public static Stream<int[]> indexStream(final int n, final int each, final boolean looping) {
        return indexStream(n, each, looping, 0);
    }

    public static Stream<int[]> indexStream(final int n, final int each, final boolean looping, final int start) {
        if (looping ? n == 0 : (n == 0 || n < each)) {
            return Stream.of();
        }
        int[] seed = new int[each];
        for (int i = 0; i < each; i++) {
            seed[i] = (n + i + start) % n;
        }
        return Stream.iterate(seed, indices -> {
            int[] out = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                out[i] = indices[i] == n - 1 ? 0 : indices[i] + 1;
            }
            return out;
        }).limit((long) (looping ? n : 1 + (n - each)));
    }

    /**
     * Calculate max error of arc replacing straight line
     *
     * @param circle
     * @param a
     * @param b
     * @return
     */
    public static double error(Circle2D circle, Point2D a, Point2D b) {
        Point2D mid = Point2D.midPoint(a, b);
        return circle.radius() - circle.center().distance(mid);
    }
}
