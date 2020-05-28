/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.cae.binpacking;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.convhull.JarvisMarch2D;

/**
 *
 * @author peter
 */
public class PackingUtils {

    public static Stream<AffineTransform2D> candidateTranslations(Set<Point2D> fixedPoints, Polygon2D candidate) {
        return Sets.cartesianProduct(fixedPoints, Sets.newLinkedHashSet(candidate.vertices())).stream()
                .map(pointPair -> AffineTransform2D.createTranslation(new Vector2D(pointPair.get(1), pointPair.get(0))));
    }

    public static Stream<Polygon2D> transformed(Polygon2D source, Stream<AffineTransform2D> transform) {
        return transform.map(trans -> source.transform(trans));
    }

    public static Polygon2D augmentHull(Polygon2D hull, Polygon2D additional) {
        // Just combine the points and do it again
        return new JarvisMarch2D().convexHull(ImmutableList.copyOf(Iterables.concat(hull.vertices(), additional.vertices())));
    }
}
