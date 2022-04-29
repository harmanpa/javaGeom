/**
 *
 */
package math.geom3d;

import static java.lang.Math.acos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import math.geom2d.Tolerance2D;
import math.geom3d.transform.AffineTransform3D;

/**
 * Define a vector in 3 dimensions. Provides methods to compute cross product
 * and dot product, addition and subtraction of vectors.
 */
public class Vector3D {

    // ===================================================================
    // class variables
    protected double x = 1;
    protected double y = 0;
    protected double z = 0;

    // ===================================================================
    // static methods
    /**
     * Computes the dot product of the two vectors, defined by :
     * <p>
     * <code> x1*x2 + y1*y2 + z1*z2</code>
     * <p>
     * Dot product is zero if the vectors defined by the 2 vectors are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     *
     * @param v1
     * @param v2
     * @return
     */
    public final static double dotProduct(Vector3D v1, Vector3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
//    
//    public final static Vector3D randomOrthogonal(Vector3D v) {
//        
//        v1.x * v.x + v1.y * v.y + v1.z * v.z = 0;
//        
//    }

    /**
     * Computes the cross product of the two vectors.Cross product is zero for
     * colinear vectors.It is positive if angle between vector 1 and vector 2 is
     * comprised between 0 and PI, and negative otherwise.
     *
     * @param v1
     * @param v2
     * @return
     */
    public final static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
        return new Vector3D(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x);
    }

    /**
     * test if the two vectors are colinear
     *
     * @param v1
     * @param v2
     * @return true if the vectors are colinear
     */
    public final static boolean isColinear(Vector3D v1, Vector3D v2) {
        v1 = v1.normalize();
        v2 = v2.normalize();
        return Vector3D.crossProduct(v1, v2).norm() < Tolerance2D.get();
    }

    public final static boolean isOpposite(Vector3D v1, Vector3D v2) {
        return v1.normalize().plus(v2.normalize()).norm() < Tolerance2D.get();
    }

    /**
     * test if the two vectors are orthogonal
     *
     * @param v1
     * @param v2
     * @return true if the vectors are orthogonal
     */
    public final static boolean isOrthogonal(Vector3D v1, Vector3D v2) {
        v1 = v1.normalize();
        v2 = v2.normalize();
        double dot = Vector3D.dotProduct(v1, v2);
        return Math.abs(dot) < Tolerance2D.get();
    }

    // ===================================================================
    // constructors
    /**
     * Constructs a new Vector3D initialized with x=1, y=0 and z=0.
     */
    public Vector3D() {
        this(1, 0, 0);
    }

    /**
     * Base constructor, using coordinates in each direction.
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct a new vector between origin and a 3D point.
     *
     * @param point
     */
    public Vector3D(Point3D point) {
        this(point.getX(), point.getY(), point.getZ());
    }

    /**
     * construct a new vector between two points
     *
     * @param point1
     * @param point2
     */
    public Vector3D(Point3D point1, Point3D point2) {
        this(point2.getX() - point1.getX(), point2.getY() - point1.getY(), point2
                .getZ()
                - point1.getZ());
    }

    // ===================================================================
    // accessors
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    // ===================================================================
    // basic arithmetic on vectors
    /**
     * Return the sum of current vector with vector given as parameter.Inner
     * fields are not modified.
     *
     * @param v
     * @return
     */
    public Vector3D plus(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Return the subtraction of current vector with vector given as
     * parameter.Inner fields are not modified.
     *
     * @param v
     * @return
     */
    public Vector3D minus(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Multiplies this vector by a constant.
     *
     * @param k
     * @return
     */
    public Vector3D times(double k) {
        return new Vector3D(k * x, k * y, k * z);
    }

    // ===================================================================
    // general operations on vectors
    /**
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     *
     * @return the vector opposite to <code>this</code>.
     */
    public Vector3D opposite() {
        return new Vector3D(-x, -y, -z);
    }

    /**
     * Computes the norm of the vector
     *
     * @return the euclidean norm of the vector
     */
    public double norm() {
        return Math.hypot(Math.hypot(x, y), z);
    }

    /**
     * Computes the square of the norm of the vector. This avoids to compute the
     * square root.
     *
     * @return the euclidean norm of the vector
     */
    public double normSq() {
        return x * x + y * y + z * z;
    }

    /**
     * Returns the vector with same direction as this one, but with norm equal
     * to 1.
     *
     * @return
     */
    public Vector3D normalize() {
        double r = this.norm();
        return new Vector3D(this.x / r, this.y / r, this.z / r);
    }

    public Vector3D cross(Vector3D v2) {
        return crossProduct(this, v2);
    }

    public double dot(Vector3D v2) {
        return dotProduct(this, v2);
    }

    public double angle(Vector3D v) {
        double val = this.dot(v) / (this.norm() * v.norm());
        return acos(max(min(val, 1), -1)); // compensate rounding errors
    }

    public Vector3D lerp(Vector3D a, double t) {
        return this.plus(a.minus(this).times(t));
    }

    public Vector3D swapNonZero() {
        if(Math.abs(x-y)>Tolerance2D.get()) {
            return new Vector3D(y, x, z);
        } else if(Math.abs(x-z)>Tolerance2D.get()) {
            return new Vector3D(z, y, x);
        } else {
            return new Vector3D(x, z, y);
        }
    }

    /**
     * Transform the vector, by using only the first 4 parameters of the
     * transform. Translation of a vector returns the same vector.
     *
     * @param trans an affine transform
     * @return the transformed vector.
     */
    public Vector3D transform(AffineTransform3D trans) {
        double[] tab = trans.coefficients();
        return new Vector3D(
                x * tab[0] + y * tab[1] + z * tab[2],
                x * tab[4] + y * tab[5] + z * tab[6],
                x * tab[8] + y * tab[9] + z * tab[10]);
    }

    // ===================================================================
    // methods implementing Object interface
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3D)) {
            return false;
        }

        Vector3D v = (Vector3D) obj;
        if (Math.abs(x - v.x) > Tolerance2D.get()) {
            return false;
        }
        if (Math.abs(y - v.y) > Tolerance2D.get()) {
            return false;
        }
        return Math.abs(z - v.z) <= Tolerance2D.get();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Vector3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

}
