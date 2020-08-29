/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgeom.geom2d.decomposition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

/**
 *
 * @author peter
 */
public class PolygonDecomposition {

    private final Polygon2D polygon;
    private final Set<Polygon2D> holes;

    public PolygonDecomposition(Polygon2D polygon) {
        this(polygon, new HashSet<>());
    }

    public PolygonDecomposition(Polygon2D polygon, Set<Polygon2D> holes) {
        this.polygon = polygon;
        this.holes = holes;
    }

    public Set<Polygon2D> decompose() {
        // If original polygon is already convex return it
        if (holes.isEmpty()) {
            if (Math.abs(Polygons2D.convexHull(polygon.boundary().get(0).vertices()).area())
                    - Math.abs(polygon.boundary().get(0).area())
                    <= Tolerance2D.get()) {
                return new HashSet<>(Arrays.asList(polygon));
            }
        }

        // Triangulate
        Polygon triangulatee = new Polygon(polygon.vertices().stream()
                .map(v -> new PolygonPoint(v.getX(), v.getY()))
                .collect(Collectors.toList()));
        holes.forEach(hole -> triangulatee.addHole(new Polygon(hole.vertices().stream()
                .map(v -> new PolygonPoint(v.getX(), v.getY()))
                .collect(Collectors.toList()))));

        Poly2Tri.triangulate(triangulatee);

        // Build graph of adjacent triangles
        Map<DelaunayTriangle, Polygon2D> polygonMap = new HashMap<>(triangulatee.getTriangles().size());
        triangulatee.getTriangles().forEach(tri
                -> polygonMap.put(tri, new SimplePolygon2D(Stream.of(tri.points)
                        .map(p -> new Point2D(p.getX(), p.getY()))
                        .collect(Collectors.toList()))));
        GraphBuilder<Polygon2D, DefaultEdge, Graph<Polygon2D, DefaultEdge>> builder
                = GraphTypeBuilder.undirected().vertexClass(Polygon2D.class)
                        .edgeClass(DefaultEdge.class).buildGraphBuilder();
        polygonMap.values().forEach(poly -> builder.addVertex(poly));
        triangulatee.getTriangles().forEach(tri1 -> Stream.of(tri1.neighbors)
                .filter(tri2 -> tri2 != null && polygonMap.containsKey(tri2)).forEach(tri2 -> {
            Polygon2D poly1 = polygonMap.get(tri1);
            Polygon2D poly2 = polygonMap.get(tri2);
            builder.removeEdge(poly1, poly2);
            builder.addEdge(poly1, poly2);
        }));
        Graph<Polygon2D, DefaultEdge> graph = builder.build();
        Set<Polygon2D> irreducible = new HashSet<>();
        // For each, attempt to add it to each of it's neighbours, starting with smallest
        while (graph.vertexSet().size() > irreducible.size()) {
            Polygon2D subject = next(graph.vertexSet(), irreducible);
            // If resulting polygon is convex, combine, else don't
            Optional<Polygon2D[]> combined = filtered(Graphs.neighborSetOf(graph, subject), irreducible)
                    .map(neighbour -> new Polygon2D[]{subject, neighbour, combineIfConvex(subject, neighbour)})
                    .filter(polys -> polys[2] != null)
                    .findFirst();
            if (combined.isPresent()) {
                Set<Polygon2D> sharedNeighbours = new HashSet<>();
                sharedNeighbours.addAll(Graphs.neighborSetOf(graph, combined.get()[0]));
                sharedNeighbours.addAll(Graphs.neighborSetOf(graph, combined.get()[1]));
                sharedNeighbours.remove(combined.get()[0]);
                sharedNeighbours.remove(combined.get()[1]);
                graph.removeVertex(combined.get()[0]);
                graph.removeVertex(combined.get()[1]);
                graph.addVertex(combined.get()[2]);
                sharedNeighbours.forEach(n -> graph.addEdge(combined.get()[2], n));
            } else {
                // If shape cannot combine with any neighbours mark it as irreducible
                irreducible.add(subject);
            }
        }
        // Once only irreducible shapes remain it has converged
        return irreducible;
    }

    private Stream<Polygon2D> filtered(Set<Polygon2D> from, Set<Polygon2D> exclude) {
        return from.stream().filter(p -> !exclude.contains(p))
                .sorted((p1, p2) -> Double.compare(Math.abs(p1.area()), Math.abs(p2.area())));
    }

    private Polygon2D next(Set<Polygon2D> from, Set<Polygon2D> exclude) {
        return filtered(from, exclude).findFirst().get();
    }

    private Polygon2D combineIfConvex(Polygon2D a, Polygon2D b) {
        Polygon2D union = Polygons2D.union(a, b);
        if (Math.abs(Polygons2D.convexHull(union.vertices()).area()) <= Math.abs(a.area()) + Math.abs(b.area()) + Tolerance2D.get()) {
            return union;
        }
        return null;
    }
}
