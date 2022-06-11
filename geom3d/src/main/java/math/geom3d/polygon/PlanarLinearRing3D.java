/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.polygon;

import java.util.List;
import java.util.stream.Collectors;
import math.geom2d.exceptions.Geom2DException;
import math.geom2d.polygon.LinearRing2D;
import math.geom3d.Point3D;
import math.geom3d.circulinear.CirculinearRing3D;
import math.geom3d.circulinear.PlanarCirculinearRing3D;
import math.geom3d.plane.Plane3D;

/**
 *
 * @author peter
 */
public class PlanarLinearRing3D extends PlanarCirculinearRing3D<LinearRing2D> implements CirculinearRing3D {

    public static PlanarLinearRing3D withDirection(List<Point3D> points, boolean cw) throws Geom2DException {
        PlanarLinearRing3D ring = new PlanarLinearRing3D(points);
        boolean ringCW = ring.getShape().area() < 0;
        if (cw != ringCW) {
            return new PlanarLinearRing3D(ring.getPlane().flip(), points);
        }
        return ring;
    }

    public PlanarLinearRing3D(List<Point3D> points) throws Geom2DException {
        this(Plane3D.fromPoints(points), points);
    }

    public PlanarLinearRing3D(Plane3D plane, List<Point3D> points) {
        this(plane, new LinearRing2D(points.stream().map(p -> plane.pointPosition(p)).collect(Collectors.toList())));
    }

    public PlanarLinearRing3D(Plane3D plane, LinearRing2D shape) {
        super(plane, shape);
    }

    public List<Point3D> vertices() {
        return getShape().vertices().stream().map(v -> getPlane().point(v)).collect(Collectors.toList());
    }

}
