/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom2d.math;

import java.util.List;
import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.exceptions.Geom2DException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 *
 * @author peter
 */
public class Transforms2D {

    public static Point2D centroid(List<Point2D> points) {
        return new Point2D(
                points.stream().mapToDouble(p -> p.getX()).summaryStatistics().getAverage(),
                points.stream().mapToDouble(p -> p.getY()).summaryStatistics().getAverage());
    }

    public static AffineTransform2D calculate(List<Point2D> pointsA, List<Point2D> pointsB) throws Geom2DException {
        if (pointsA.size() < 3 || pointsA.size() != pointsB.size()) {
            throw new Geom2DException("Need more points");
        }
        int n = pointsA.size();
        Point2D centroidA = centroid(pointsA);
        Point2D centroidB = centroid(pointsB);
        // Kabsch Algorithm
        RealMatrix P = new Array2DRowRealMatrix(3, n);
        RealMatrix Q = new Array2DRowRealMatrix(3, n);
        for (int i = 0; i < n; i++) {
            P.setEntry(i, 0, pointsA.get(i).minus(centroidA).getX());
            P.setEntry(i, 1, pointsA.get(i).minus(centroidA).getY());
            Q.setEntry(i, 0, pointsB.get(i).minus(centroidB).getX());
            Q.setEntry(i, 1, pointsB.get(i).minus(centroidB).getY());
        }
        RealMatrix H = P.transpose().multiply(Q);
        SingularValueDecomposition svd = new SingularValueDecomposition(H);
        RealMatrix M = new Array2DRowRealMatrix(2, 2);
        M.setEntry(0, 0, 1);
        M.setEntry(1, 1, 1);
        M.setEntry(2, 2, Math.signum(new LUDecomposition(svd.getV().multiply(svd.getUT())).getDeterminant()));
        RealMatrix R = svd.getV().multiply(M).multiply(svd.getUT());
        AffineTransform2D transform = AffineTransform2D.createTranslation(new Vector2D(centroidA, new Point2D()))
                .preConcatenate(new AffineTransform2D(new double[]{
            R.getEntry(0, 0),
            R.getEntry(0, 1),
            R.getEntry(1, 0),
            R.getEntry(1, 1)})).preConcatenate(AffineTransform2D.createTranslation(centroidB.getX(), centroidB.getY()));
        return transform;
    }
}
