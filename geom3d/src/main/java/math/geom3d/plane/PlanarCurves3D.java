/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.circulinear.CirculinearRing2D;
import math.geom2d.circulinear.GenericCirculinearRing2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.exceptions.Geom2DException;
import math.geom3d.circulinear.PlanarCirculinearRing3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.line.LineSegment3D;

/**
 *
 * @author peter
 */
public class PlanarCurves3D {

    public static PlanarCirculinearRing3D joinPlanar(Collection<Curve3D> curves) throws Geom2DException {
        Plane3D plane = getPlane(curves);
        CirculinearRing2D ring2D = join2D(inPlane(curves, plane));
        return new PlanarCirculinearRing3D(plane, ring2D);
    }

    public static CirculinearRing2D join2D(Collection<Curve2D> curves) throws Geom2DException {
        return new GenericCirculinearRing2D(curves.stream()
                .flatMap(c -> c.continuousCurves().stream())
                .flatMap(cc -> cc.smoothPieces().stream())
                .map(sp -> (CirculinearElement2D) sp)
                .collect(Collectors.toList()));
    }

    public static Collection<Curve2D> inPlane(Collection<Curve3D> curves, Plane3D plane) throws Geom2DException {
        List<Curve2D> out = new ArrayList<>(curves.size());
        for (Curve3D curve : curves) {
            if (curve instanceof PlanarCurve3D) {
                PlanarCurve3D planarShape = (PlanarCurve3D) curve;
                if (!planarShape.getPlane().equals(plane)) {
                    throw new Geom2DException("Curve not in plane " + curve);
                }
                out.add((Curve2D) planarShape.getShape());
            } else if (curve instanceof LineSegment3D) {
                LineSegment3D line = (LineSegment3D) curve;
                if (!plane.isLineInPlane(line)) {
//                    throw new Geom2DException("Curve not in plane " + curve);
                }
                out.add(plane.lineInPlane(line));
            } else {
                throw new Geom2DException("Curve type not supported for inPlane: " + curve.getClass());
            }
        }
        return out;
    }

    public static Plane3D getPlane(Collection<Curve3D> curves) throws Geom2DException {
        // Get a plane from one of the PlanarShapes
        Optional<Plane3D> planeFromPlanarShapes = curves.stream()
                .filter(curve -> curve != null && curve instanceof PlanarShape3D)
                .map(curve -> ((PlanarShape3D) curve).getPlane())
                .findAny();
        if (planeFromPlanarShapes.isPresent()) {
            return planeFromPlanarShapes.get();
        }
        // Get a plane from the start points
        return Plane3D.fromPoints(curves.stream()
                .filter(curve -> curve != null)
                .map(curve -> curve.firstPoint())
                .collect(Collectors.toList()));
    }
}
