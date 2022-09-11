/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d;

import java.util.Comparator;
import java.util.function.Function;
import math.geom2d.Tolerance2D;

/**
 *
 * @author peter
 */
public class Direction3D implements GeometricObject3D {

    private final Vector3D v;

    public Direction3D(Vector3D v) {
        this.v = v != null && PREFERPOSITIVEDOMAIN.compare(v, v.opposite()) > 0 ? v.opposite() : v;
    }

    public Vector3D getV() {
        return v;
    }

    @Override
    public String toString() {
        return "Direction3D{" + "v=" + v + '}';
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        return obj instanceof Direction3D
                && (((Direction3D) obj).getV() == null ? getV() == null
                : (((Direction3D) obj).getV().almostEquals(getV(), eps)
                || ((Direction3D) obj).getV().opposite().almostEquals(getV(), eps)));
    }

    @Override
    public int hashCode() {
        return 23 + (v == null ? 0 : v.hashCode() + v.opposite().hashCode());
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
        final Direction3D other = (Direction3D) obj;
        return almostEquals(other, Tolerance2D.get());
    }

    private static final Function<Vector3D, Integer> POSITIVEELEMENTS = (Vector3D v1) -> (v1.getX() > Tolerance2D.get() ? 1 : 0) + (v1.getY() > Tolerance2D.get() ? 1 : 0) + (v1.getZ() > Tolerance2D.get() ? 1 : 0);
    private static final Function<Vector3D, Integer> NEGATIVEELEMENTS = (Vector3D v1) -> (v1.getX() < -Tolerance2D.get() ? -1 : 0) + (v1.getY() < -Tolerance2D.get() ? -1 : 0) + (v1.getZ() < -Tolerance2D.get() ? -1 : 0);
    private static final Comparator<Vector3D> PREFERPOSITIVEDOMAIN = (Vector3D a, Vector3D b) -> {
        int res = -Integer.compare(POSITIVEELEMENTS.apply(a), POSITIVEELEMENTS.apply(b));
        if (res == 0) {
            res = -Integer.compare(NEGATIVEELEMENTS.apply(a), NEGATIVEELEMENTS.apply(b));
        }
        return res;
    };
}
