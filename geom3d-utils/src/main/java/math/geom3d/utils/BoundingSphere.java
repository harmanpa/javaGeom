/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.utils;

import com.dreizak.miniball.highdim.Miniball;
import com.dreizak.miniball.model.PointSet;
import math.geom3d.Point3D;
import math.geom3d.PointSet3D;
import math.geom3d.Sphere3D;

/**
 *
 * @author peter
 */
public class BoundingSphere {

    public static Sphere3D boundingSphere(PointSet3D points) {
        Miniball ball = new Miniball(new MiniballPointSet3D(points));
        return new Sphere3D(new Point3D(ball.center()[0], ball.center()[1], ball.center()[2]), ball.radius());
    }

    static class MiniballPointSet3D implements PointSet {

        private final PointSet3D points;

        public MiniballPointSet3D(PointSet3D points) {
            this.points = points;
        }

        @Override
        public int size() {
            return points.pointNumber();
        }

        @Override
        public int dimension() {
            return 3;
        }

        @Override
        public double coord(int i, int j) {
            switch (j) {
                case 0:
                    return points.getPoint(i).getX();
                case 1:
                    return points.getPoint(i).getY();
                case 2:
                    return points.getPoint(i).getZ();
                default:
                    return 0.0;
            }
        }
    }
}
