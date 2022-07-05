package math.geom3d;

import junit.framework.TestCase;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealVector;

public class Vector3DTest extends TestCase {

    public void testDotProduct() {
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(1, 0, 0);
        Vector3D v3 = new Vector3D(0, 1, 0);

        assertEquals(Vector3D.dotProduct(v1, v2), 1, 1e-14);
        assertEquals(Vector3D.dotProduct(v1, v3), 0, 1e-14);
    }

    public void testCrossProduct() {
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);
        Vector3D v3 = new Vector3D(0, 0, 1);

        assertTrue(v3.equals(Vector3D.crossProduct(v1, v2)));
        assertTrue(v1.equals(Vector3D.crossProduct(v2, v3)));
        assertTrue(v2.equals(Vector3D.crossProduct(v3, v1)));
    }

    public void testIsColinear() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(1.5, 3, 4.5);
        assertTrue(Vector3D.isColinear(v1, v2));
    }

    public void testIsOrthogonal() {
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);
        Vector3D v3 = new Vector3D(0, 0, 1);

        assertTrue(Vector3D.isOrthogonal(v1, v2));
        assertTrue(Vector3D.isOrthogonal(v1, v3));
        assertTrue(Vector3D.isOrthogonal(v2, v3));
    }

    public void testMakeOrthogonal() {
        Vector3D v1 = new Vector3D(1, 0, 0);

    }

    public void testPlus() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        Vector3D v3 = new Vector3D(5, 7, 9);
        assertTrue(v1.plus(v2).equals(v3));
    }

    public void testMinus() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);
        Vector3D v3 = new Vector3D(-3, -3, -3);
        assertTrue(v1.minus(v2).equals(v3));
    }

    public void testTimes() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(1.5, 3, 4.5);
        assertTrue(v1.times(1.5).equals(v2));
    }

    public void testGetNorm() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        assertEquals(v1.norm(), Math.sqrt(14), 1e-14);
    }

    public void testDirection() {
        Direction3D a = new Direction3D(new Vector3D(0, 0, 1));
        Direction3D b = new Direction3D(new Vector3D(0, 0, 1));
        Direction3D c = new Direction3D(new Vector3D(0, 0, -1));
        Direction3D d = new Direction3D(new Vector3D(0, 1, 0));
        Direction3D e = new Direction3D(null);
        Direction3D f = new Direction3D(null);
        assertEquals(a, b);
        assertEquals(a, c);
        assertEquals(b, c);
        assertEquals(e, f);
        assertNotSame(a, d);
        assertNotSame(b, d);
        assertNotSame(c, d);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        assertEquals(b.hashCode(), c.hashCode());
        assertEquals(e.hashCode(), f.hashCode());
        assertNotSame(a.hashCode(), d.hashCode());
        assertNotSame(b.hashCode(), d.hashCode());
        assertNotSame(c.hashCode(), d.hashCode());
    }

}
