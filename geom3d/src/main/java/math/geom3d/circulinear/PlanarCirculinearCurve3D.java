/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Collection;
import java.util.stream.Collectors;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom3d.plane.PlanarCurve3D;
import math.geom3d.plane.Plane3D;

/**
 *
 * @author peter
 */
public class PlanarCirculinearCurve3D<T extends CirculinearCurve2D> extends PlanarCurve3D<T> implements CirculinearCurve3D {

    public PlanarCirculinearCurve3D(Plane3D plane, T shape) {
        super(plane, shape);
    }

    @Override
    public double length() {
        return getShape().length();
    }

    @Override
    public double length(double pos) {
        return getShape().length(pos);
    }

    @Override
    public double position(double distance) {
        return getShape().position(distance);
    }

    @Override
    public Collection<? extends CirculinearContinuousCurve3D> continuousCurves() {
        return getShape().continuousCurves().stream()
                .map(cc -> new PlanarCirculinearContinuousCurve3D<>(getPlane(), cc))
                .collect(Collectors.toList());
    }

    @Override
    public CirculinearCurve3D subCurve(double t0, double t1) {
        return map(c -> c.subCurve(t0, t1), CirculinearCurve3D.class);
    }

    @Override
    public CirculinearCurve3D reverseCurve() {
        return map(c -> c.reverse(), CirculinearCurve3D.class);
    }

}
