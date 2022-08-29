/**
 *
 */
package math.geom3s;

import static java.lang.Math.acos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import math.geom2d.Tolerance2D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;

/**
 * Define a vector in 3 dimensions. Provides methods to compute cross product
 * and dot product, addition and subtraction of vectors.
 */
public class Vector3S implements GeometricObject3D {

    // ===================================================================
    // class variables
    protected double r = 1;
    protected double theta = 0;
    protected double phi = 0;

    // ===================================================================
    // constructors
    /**
     * Constructs a new Vector3D initialized with x=1, y=0 and z=0.
     */
    public Vector3S() {
        this(0, 0);
    }

    public Vector3S(double theta, double phi) {
        this(1, theta, phi);
    }

    public Vector3S(double r, double theta, double phi) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }

    public double getR() {
        return r;
    }

    public double getTheta() {
        return theta;
    }

    public double getPhi() {
        return phi;
    }

    public static Vector3S fromCartesian(Vector3D v) {
        return new Vector3S(v.norm(), Math.acos(v.getZ() / v.norm()), Math.atan2(v.getY(), v.getX()));
    }

    public Vector3D toCartesian() {
        return new Vector3D(r * Math.cos(phi) * Math.sin(theta), r * Math.sin(phi) * Math.sin(theta), r * Math.cos(theta));
    }

    public Vector3S addToTheta(double a) {
        return new Vector3S(r, theta + a, phi).canonicalize();
    }

    public Vector3S addToPhi(double a) {
        return new Vector3S(r, theta, phi + a).canonicalize();
    }

    Vector3S canonicalize() {
        return fromCartesian(toCartesian());
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
    public Vector3S plus(Vector3S v) {
        return fromCartesian(toCartesian().plus(v.toCartesian()));
    }

    /**
     * Return the subtraction of current vector with vector given as
     * parameter.Inner fields are not modified.
     *
     * @param v
     * @return
     */
    public Vector3S minus(Vector3S v) {
        return fromCartesian(toCartesian().minus(v.toCartesian()));
    }

    /**
     * Multiplies this vector by a constant.
     *
     * @param k
     * @return
     */
    public Vector3S times(double k) {
        return new Vector3S(r * k, theta, phi);
    }

    // ===================================================================
    // general operations on vectors
    /**
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     *
     * @return the vector opposite to <code>this</code>.
     */
    public Vector3S opposite() {
        return new Vector3S(-r, theta, phi);
    }

    /**
     * Computes the norm of the vector
     *
     * @return the euclidean norm of the vector
     */
    public double norm() {
        return r;
    }

    /**
     * Computes the square of the norm of the vector. This avoids to compute the
     * square root.
     *
     * @return the euclidean norm of the vector
     */
    public double normSq() {
        return r * r;
    }

    /**
     * Returns the vector with same direction as this one, but with norm equal
     * to 1.
     *
     * @return
     */
    public Vector3S normalize() {
        return new Vector3S(1.0, this.theta, this.phi);
    }

    public Vector3S cross(Vector3S v2) {
        return fromCartesian(toCartesian().cross(v2.toCartesian()));
    }

    public double dot(Vector3S v2) {
        return toCartesian().dot(v2.toCartesian());
    }

    public double angle(Vector3S v) {
        double val = this.dot(v) / (this.norm() * v.norm());
        return acos(max(min(val, 1), -1)); // compensate rounding errors
    }

    public Vector3S lerp(Vector3S a, double t) {
        return this.plus(a.minus(this).times(t));
    }

    /**
     * Transform the vector, by using only the first 4 parameters of the
     * transform. Translation of a vector returns the same vector.
     *
     * @param trans an affine transform
     * @return the transformed vector.
     */
    public Vector3S transform(AffineTransform3D trans) {
        return fromCartesian(toCartesian().transform(trans));
    }

    AffineTransform3D transformToZero() {
        AffineTransform3D a = AffineTransform3D.createRotationOz(-phi);
        Vector3S tmp = transform(a);
        AffineTransform3D b = AffineTransform3D.createRotationOy(-tmp.theta);
        return a.preConcatenate(b);
    }

    AffineTransform3D transformFromZero() {
        return transformToZero().inverse();
    }

    public AffineTransform3D transformTo(Vector3S other) {
        return transformToZero().preConcatenate(other.transformFromZero());
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        return obj instanceof Vector3S
                && Math.abs(((Vector3S) obj).getR() - getR()) <= eps
                && Math.abs(((Vector3S) obj).getTheta() - getTheta()) <= eps
                && Math.abs(((Vector3S) obj).getPhi() - getPhi()) <= eps;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Tolerance2D.hash(r);
        hash = 31 * hash + Tolerance2D.hash(phi);
        hash = 31 * hash + Tolerance2D.hash(theta);
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
        final Vector3S other = (Vector3S) obj;
        return almostEquals(other, Tolerance2D.get());
    }

    @Override
    public String toString() {
        return "Vector3S{" + "r=" + r + ", theta=" + theta + ", phi=" + phi + '}';
    }

}
