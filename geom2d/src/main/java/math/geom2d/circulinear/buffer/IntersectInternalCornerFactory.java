/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.circulinear.buffer;

import java.util.Collection;
import java.util.Deque;
import math.geom2d.Point2D;
import math.geom2d.circulinear.CirculinearContinuousCurve2D;
import math.geom2d.circulinear.CirculinearCurves2D;

/**
 *
 * @author peter
 */
public class IntersectInternalCornerFactory implements InternalCornerFactory {

    @Override
    public boolean createInternalCorner(Deque<CirculinearContinuousCurve2D> parallelElementQueue, CirculinearContinuousCurve2D currentParallelElement) {
        // does this parallel intersect the previous?
        Collection<Point2D> intersections = CirculinearCurves2D.findIntersections(currentParallelElement, parallelElementQueue.peekLast());
        if (intersections.size() == 1) {
            Point2D point = intersections.iterator().next();
            // Remove and trim the previous edge, and trim the current one, add both
            CirculinearContinuousCurve2D previousParallel = parallelElementQueue.pollLast();
            parallelElementQueue.add(previousParallel.subCurve(previousParallel.t0(), previousParallel.position(point)));
            parallelElementQueue.add(currentParallelElement.subCurve(currentParallelElement.position(point), currentParallelElement.t1()));
            return true;
        }
        return false;
    }

}
