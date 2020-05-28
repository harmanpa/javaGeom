/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.cae.binpacking;

import java.util.ArrayList;
import java.util.List;
import math.geom2d.Box2D;
import math.geom2d.polygon.Polygon2D;
import org.joml.AABBf;
import pl.pateman.dynamicaabbtree.AABBTree;
import pl.pateman.dynamicaabbtree.Boundable;
import pl.pateman.dynamicaabbtree.Identifiable;

/**
 *
 * @author peter
 */
public class PolygonFastIntersector {

    private final AABBTree<PolygonBoundable> tree;

    public PolygonFastIntersector() {
        this.tree = new AABBTree<>();
    }

    public void add(Polygon2D polygon) {
        this.tree.add(new PolygonBoundable(polygon));
    }

    public boolean overlaps(Polygon2D candidate) {
        List<PolygonBoundable> nearby = new ArrayList<>();
        this.tree.detectOverlaps(getAABB(candidate.boundingBox(), null), nearby);
        return nearby.stream().anyMatch(poly -> candidate.vertices().stream().anyMatch(v -> poly.polygon.contains(v)));
    }

    static AABBf getAABB(Box2D box, AABBf dest) {
        if (dest == null) {
            dest = new AABBf();
        }
        dest.setMin((float) box.getMinX(), (float) box.getMinY(), 0.0f);
        dest.setMax((float) box.getMaxX(), (float) box.getMaxY(), 0.0f);
        return dest;
    }

    static class PolygonBoundable implements Boundable, Identifiable {

        private static long ID = 0L;
        private final Polygon2D polygon;
        private final Box2D bounds;
        private final long id;

        public PolygonBoundable(Polygon2D polygon) {
            this.polygon = polygon;
            this.bounds = polygon.boundingBox();
            this.id = ID++;
        }

        @Override
        public AABBf getAABB(AABBf aabbf) {
            return PolygonFastIntersector.getAABB(bounds, aabbf);
        }

        @Override
        public long getID() {
            return id;
        }

    }
}
