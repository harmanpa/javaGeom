/* file : AffineTransform3D.java
 * 
 * Project : geometry
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 27 nov. 2005
 *
 */
package math.geom3d.transform;

import java.util.List;
import math.geom2d.Tolerance2D;
import math.geom2d.exceptions.Geom2DException;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * An affine transform in 3 dimensions. Contains also static methods for
 * creating common transforms like translations or rotations.
 *
 * @author dlegland
 */
public final class AffineTransform3D implements Bijection3D {

    /**
     * coefficients for x coordinate.
     */
    private final double m00, m01, m02, m03;

    /**
     * coefficients for y coordinate.
     */
    private final double m10, m11, m12, m13;

    /**
     * coefficients for z coordinate.
     */
    private final double m20, m21, m22, m23;

    // ===================================================================
    // public static methods
    public final static AffineTransform3D createTranslation(Vector3D vec) {
        return createTranslation(vec.getX(), vec.getY(), vec.getZ());
    }

    public final static AffineTransform3D createTranslation(double x, double y,
            double z) {
        return new AffineTransform3D(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z);
    }

    public final static AffineTransform3D createRotationOx(double theta) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new AffineTransform3D(
                1, 0, 0, 0,
                0, cot, -sit, 0,
                0, sit, cot, 0);
    }

    public final static AffineTransform3D createRotationOy(double theta) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new AffineTransform3D(
                cot, 0, sit, 0,
                0, 1, 0, 0,
                -sit, 0, cot, 0);
    }

    public final static AffineTransform3D createRotationOz(double theta) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new AffineTransform3D(
                cot, -sit, 0, 0,
                sit, cot, 0, 0,
                0, 0, 1, 0);
    }

    public final static AffineTransform3D createScaling(double s) {
        return createScaling(s, s, s);
    }

    public final static AffineTransform3D createScaling(double sx, double sy, double sz) {
        return new AffineTransform3D(sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0);
    }
    
    public final static AffineTransform3D fromMatrix(RealMatrix m) {
        return new AffineTransform3D(
                m.getEntry(0, 0),
                m.getEntry(0, 1),
                m.getEntry(0, 2),
                m.getColumnDimension()>3 ? m.getEntry(0, 3) : 0.0,
                m.getEntry(1, 0),
                m.getEntry(1, 1),
                m.getEntry(1, 2),
                m.getColumnDimension()>3 ? m.getEntry(1, 3) : 0.0,
                m.getEntry(2, 0),
                m.getEntry(2, 1),
                m.getEntry(2, 2),
                m.getColumnDimension()>3 ? m.getEntry(2, 3) : 0.0);
    }

    // ===================================================================
    // constructors
    /**
     * Creates a new affine transform3D set to identity
     */
    public AffineTransform3D() {
        // init to identity matrix
        m00 = m11 = m22 = 1;
        m01 = m02 = m03 = 0;
        m10 = m12 = m13 = 0;
        m20 = m21 = m23 = 0;
    }

    public AffineTransform3D(double[] coefs) {
        switch (coefs.length) {
            case 9:
                m00 = coefs[0];
                m01 = coefs[1];
                m02 = coefs[2];
                m03 = 0.0;
                m10 = coefs[3];
                m11 = coefs[4];
                m12 = coefs[5];
                m13 = 0.0;
                m20 = coefs[6];
                m21 = coefs[7];
                m22 = coefs[8];
                m23 = 0.0;
                break;
            case 12:
                m00 = coefs[0];
                m01 = coefs[1];
                m02 = coefs[2];
                m03 = coefs[3];
                m10 = coefs[4];
                m11 = coefs[5];
                m12 = coefs[6];
                m13 = coefs[7];
                m20 = coefs[8];
                m21 = coefs[9];
                m22 = coefs[10];
                m23 = coefs[11];
                break;
            default:
                throw new IllegalArgumentException(
                        "Input array must have 9 or 12 elements");
        }
    }

    public AffineTransform3D(double xx, double yx, double zx, double tx,
            double xy, double yy, double zy, double ty, double xz, double yz,
            double zz, double tz) {
        m00 = xx;
        m01 = yx;
        m02 = zx;
        m03 = tx;
        m10 = xy;
        m11 = yy;
        m12 = zy;
        m13 = ty;
        m20 = xz;
        m21 = yz;
        m22 = zz;
        m23 = tz;
    }

    // ===================================================================
    // accessors
    public boolean isIdentity() {
        if (m00 != 1) {
            return false;
        }
        if (m11 != 1) {
            return false;
        }
        if (m22 != 0) {
            return false;
        }
        if (m01 != 0) {
            return false;
        }
        if (m02 != 0) {
            return false;
        }
        if (m03 != 0) {
            return false;
        }
        if (m10 != 0) {
            return false;
        }
        if (m12 != 0) {
            return false;
        }
        if (m13 != 0) {
            return false;
        }
        if (m20 != 0) {
            return false;
        }
        if (m21 != 0) {
            return false;
        }
        return m23 == 0;
    }

    /**
     * Returns the affine coefficients of the transform.Result is an array of 12
     * double.
     *
     * @return
     */
    public double[] coefficients() {
        double[] tab = {m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23};
        return tab;
    }

    /**
     * Computes the determinant of this transform. Can be zero.
     *
     * @return the determinant of the transform.
     */
    private double determinant() {
        return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m20 * m12)
                + m02 * (m10 * m21 - m20 * m11);
    }
    
    /**
     * Computes the inverse affine transform.
     *
     * @return
     */
    @Override
    public AffineTransform3D inverse() {
        double det = this.determinant();
        return new AffineTransform3D(
                (m11 * m22 - m21 * m12) / det,
                (m21 * m02 - m01 * m22) / det,
                (m01 * m12 - m11 * m02) / det,
                (m01 * (m22 * m13 - m12 * m23) + m02 * (m11 * m23 - m21 * m13)
                - m03 * (m11 * m22 - m21 * m12)) / det,
                (m20 * m12 - m10 * m22) / det,
                (m00 * m22 - m20 * m02) / det,
                (m10 * m02 - m00 * m12) / det,
                (m00 * (m12 * m23 - m22 * m13) - m02 * (m10 * m23 - m20 * m13)
                + m03 * (m10 * m22 - m20 * m12)) / det,
                (m10 * m21 - m20 * m11) / det,
                (m20 * m01 - m00 * m21) / det,
                (m00 * m11 - m10 * m01) / det,
                (m00 * (m21 * m13 - m11 * m23) + m01 * (m10 * m23 - m20 * m13)
                - m03 * (m10 * m21 - m20 * m11)) / det);
    }

    // ===================================================================
    // general methods
    /**
     * Returns the affine transform created by applying first the affine
     * transform given by <code>that</code>, then this affine transform.
     *
     * @param that the transform to apply first
     * @return the composition this * that
     */
    public AffineTransform3D concatenate(AffineTransform3D that) {
        double n00 = m00 * that.m00 + m01 * that.m10 + m02 * that.m20;
        double n01 = m00 * that.m01 + m01 * that.m11 + m02 * that.m21;
        double n02 = m00 * that.m02 + m01 * that.m12 + m02 * that.m22;
        double n03 = m00 * that.m03 + m01 * that.m13 + m02 * that.m23 + m03;
        double n10 = m10 * that.m00 + m11 * that.m10 + m12 * that.m20;
        double n11 = m10 * that.m01 + m11 * that.m11 + m12 * that.m21;
        double n12 = m10 * that.m02 + m11 * that.m12 + m12 * that.m22;
        double n13 = m10 * that.m03 + m11 * that.m13 + m12 * that.m23 + m13;
        double n20 = m20 * that.m00 + m21 * that.m10 + m22 * that.m20;
        double n21 = m20 * that.m01 + m21 * that.m11 + m22 * that.m21;
        double n22 = m20 * that.m02 + m21 * that.m12 + m22 * that.m22;
        double n23 = m20 * that.m03 + m21 * that.m13 + m22 * that.m23 + m23;
        return new AffineTransform3D(
                n00, n01, n02, n03,
                n10, n11, n12, n13,
                n20, n21, n22, n23);
    }

    /**
     * Return the affine transform created by applying first this affine
     * transform, then the affine transform given by <code>that</code>.
     *
     * @param that the transform to apply in a second step
     * @return the composition that * this
     */
    public AffineTransform3D preConcatenate(AffineTransform3D that) {
        double n00 = that.m00 * m00 + that.m01 * m10 + that.m02 * m20;
        double n01 = that.m00 * m01 + that.m01 * m11 + that.m02 * m21;
        double n02 = that.m00 * m02 + that.m01 * m12 + that.m02 * m22;
        double n03 = that.m00 * m03 + that.m01 * m13 + that.m02 * m23 + that.m03;
        double n10 = that.m10 * m00 + that.m11 * m10 + that.m12 * m20;
        double n11 = that.m10 * m01 + that.m11 * m11 + that.m12 * m21;
        double n12 = that.m10 * m02 + that.m11 * m12 + that.m12 * m22;
        double n13 = that.m10 * m03 + that.m11 * m13 + that.m12 * m23 + that.m13;
        double n20 = that.m20 * m00 + that.m21 * m10 + that.m22 * m20;
        double n21 = that.m20 * m01 + that.m21 * m11 + that.m22 * m21;
        double n22 = that.m20 * m02 + that.m21 * m12 + that.m22 * m22;
        double n23 = that.m20 * m03 + that.m21 * m13 + that.m22 * m23 + that.m23;
        return new AffineTransform3D(
                n00, n01, n02, n03,
                n10, n11, n12, n13,
                n20, n21, n22, n23);
    }

    /**
     * Transforms the input point array, stores the result in the pre-allocated
     * array, and returns a pointer to the result array.A new array is created
     * if <code>res</code> is null or has length smaller than of src.
     *
     * @param src
     * @param dst
     * @return
     */
    @Override
    public Point3D[] transformPoints(Point3D[] src, Point3D[] dst) {
        // Check validity of result array
        if (dst == null || dst.length < src.length) {
            dst = new Point3D[src.length];
        }

        // transform each input point
        for (int i = 0; i < src.length; i++) {
            dst[i] = new Point3D(
                    src[i].getX() * m00 + src[i].getY() * m01 + src[i].getZ() * m02 + m03,
                    src[i].getX() * m10 + src[i].getY() * m11 + src[i].getZ() * m12 + m13,
                    src[i].getX() * m20 + src[i].getY() * m21 + src[i].getZ() * m22 + m23);
        }
        return dst;
    }

    /**
     * Transforms the input point.
     *
     * @param src
     * @return
     */
    @Override
    public Point3D transformPoint(Point3D src) {
        return new Point3D(
                src.getX() * m00 + src.getY() * m01 + src.getZ() * m02 + m03,
                src.getX() * m10 + src.getY() * m11 + src.getZ() * m12 + m13,
                src.getX() * m20 + src.getY() * m21 + src.getZ() * m22 + m23);
    }

    /**
     * Compares two transforms. Returns true if all inner fields are equal up to
     * the precision given by Tolerance2D.get().
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AffineTransform3D)) {
            return false;
        }
        double tab[] = ((AffineTransform3D) obj).coefficients();
        if (Tolerance2D.compare(tab[0], m00)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[1], m01)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[2], m02)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[3], m03)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[4], m10)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[5], m11)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[6], m12)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[7], m13)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[8], m20)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[9], m21)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[10], m22)!=0) {
            return false;
        }
        if (Tolerance2D.compare(tab[11], m23)!=0) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Tolerance2D.hash(this.m00);
        hash = 67 * hash + Tolerance2D.hash(this.m01);
        hash = 67 * hash + Tolerance2D.hash(this.m02);
        hash = 67 * hash + Tolerance2D.hash(this.m03);
        hash = 67 * hash + Tolerance2D.hash(this.m10);
        hash = 67 * hash + Tolerance2D.hash(this.m11);
        hash = 67 * hash + Tolerance2D.hash(this.m12);
        hash = 67 * hash + Tolerance2D.hash(this.m13);
        hash = 67 * hash + Tolerance2D.hash(this.m20);
        hash = 67 * hash + Tolerance2D.hash(this.m21);
        hash = 67 * hash + Tolerance2D.hash(this.m22);
        hash = 67 * hash + Tolerance2D.hash(this.m23);
        return hash;
    }
    
    public static Point3D centroid(List<Point3D> points) {
        return new Point3D(
            points.stream().mapToDouble(p -> p.getX()).summaryStatistics().getAverage(),
            points.stream().mapToDouble(p -> p.getY()).summaryStatistics().getAverage(),
            points.stream().mapToDouble(p -> p.getZ()).summaryStatistics().getAverage());
    }

    public static AffineTransform3D calculate(List<Point3D> pointsA, List<Point3D> pointsB) throws Geom2DException {
        if(pointsA.size()<3 || pointsA.size() != pointsB.size()) {
            throw new Geom2DException("Need more points");
        }
        int n = pointsA.size();        
        Point3D centroidA = centroid(pointsA);
        Point3D centroidB = centroid(pointsB);
        // Kabsch Algorithm
        RealMatrix P = new Array2DRowRealMatrix(3, n);
        RealMatrix Q = new Array2DRowRealMatrix(3, n);
        for(int i=0; i<n; i++) {
            P.setEntry(i, 0, pointsA.get(i).minus(centroidA).getX());
            P.setEntry(i, 1, pointsA.get(i).minus(centroidA).getY());
            P.setEntry(i, 2, pointsA.get(i).minus(centroidA).getZ());
            Q.setEntry(i, 0, pointsB.get(i).minus(centroidB).getX());
            Q.setEntry(i, 1, pointsB.get(i).minus(centroidB).getY());
            Q.setEntry(i, 2, pointsB.get(i).minus(centroidB).getZ());
        }
        RealMatrix H = P.transpose().multiply(Q);
        SingularValueDecomposition svd = new SingularValueDecomposition(H);
        RealMatrix M = new Array2DRowRealMatrix(3, 3);
        M.setEntry(0, 0, 1);
        M.setEntry(1, 1, 1);
        M.setEntry(2, 2, Math.signum(new LUDecomposition(svd.getV().multiply(svd.getUT())).getDeterminant()));
        RealMatrix R = svd.getV().multiply(M).multiply(svd.getUT());
        AffineTransform3D transform = AffineTransform3D.createTranslation(new Vector3D(centroidA, new Point3D()))
                .preConcatenate(new AffineTransform3D(new double[]{
                    R.getEntry(0, 0),
                    R.getEntry(0, 1),
                    R.getEntry(0, 2),
                    R.getEntry(1, 0),
                    R.getEntry(1, 1),
                    R.getEntry(1, 2),
                    R.getEntry(2, 0),
                    R.getEntry(2, 1),
                    R.getEntry(2, 2)})).preConcatenate(AffineTransform3D.createTranslation(centroidB.asVector()));
        for(int i=0;i<pointsA.size();i++) {
            System.out.println(pointsA.get(i).transform(transform) + " vs " + pointsB.get(i));
        }
        return transform;
    }
}
