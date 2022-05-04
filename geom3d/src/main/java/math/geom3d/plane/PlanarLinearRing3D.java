/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import java.util.List;
import java.util.stream.Collectors;
import math.geom2d.polygon.LinearRing2D;
import math.geom3d.Point3D;

/**
 *
 * @author peter
 */
public class PlanarLinearRing3D extends PlanarShape3D<LinearRing2D> {

    public PlanarLinearRing3D(List<Point3D> points) {
        this(Plane3D.fromPoints(points), points);
    }

    public PlanarLinearRing3D(Plane3D plane, List<Point3D> points) {
        this(plane, new LinearRing2D(points.stream().map(p -> plane.pointPosition(p)).collect(Collectors.toList())));
    }

    public PlanarLinearRing3D(Plane3D plane, LinearRing2D shape) {
        super(plane, shape);
    }

}
