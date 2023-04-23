/**
 * Node.java
 *
 * Copyright 2014-2014 Michael Hoffer info@michaelhoffer.de. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer info@michaelhoffer.de "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer info@michaelhoffer.de OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Michael Hoffer
 * info@michaelhoffer.de.
 */
package math.geom3d.csg;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import math.geom3d.csg.util.PolygonUtil;

// TODO: Auto-generated Javadoc
/**
 * Holds a node in a BSP tree. A BSP tree is built from a collection of polygons
 * by picking a polygon to split along. That polygon (and all other coplanar
 * polygons) are added directly to that node and the other polygons are added to
 * the front and/or back subtrees. This is not a leafy BSP tree since there is
 * no distinction between internal and leaf nodes.
 */
final class Node {

    /**
     * Polygons.
     */
    private final List<Polygon> polygonQueue;
    /**
     * Polygons.
     */
    private List<Polygon> polygons;
    /**
     * Plane used for BSP.
     */
    private Plane plane;
    /**
     * Polygons in front of the plane.
     */
    private Node front;
    /**
     * Polygons in back of the plane.
     */
    private Node back;

    /**
     * Constructor.
     *
     * Creates a BSP node consisting of the specified polygons.
     *
     * @param polygons polygons
     */
    public Node(List<Polygon> polygons) {
        this.polygonQueue = new ArrayList<>(polygons == null ? 0 : polygons.size());
        this.polygons = new ArrayList<>(polygons == null ? 0 : polygons.size());
        if (polygons != null) {
            this.add(polygons);
        }
    }

    /**
     * Constructor. Creates a node without polygons.
     */
    public Node() {
        this(null);
    }

    private Node(List<Polygon> mine, Plane plane, Node front, Node back) {
        this.polygonQueue = new ArrayList<>();
        this.polygons = mine;
        this.plane = plane;
        this.front = front;
        this.back = back;
    }

    /**
     * Converts solid space to empty space and vice versa.
     */
    public Node invert() {
        build();
        return new Node(
                this.polygons.stream().map(p -> p.flip()).collect(Collectors.toList()),
                this.plane == null ? null : this.plane.flip(),
                this.back == null ? null : this.back.invert(),
                this.front == null ? null : this.front.invert()
        );
    }

    /**
     * Recursively removes all polygons in the {@link polygons} list that are
     * contained within this BSP tree.
     *
     * Note: polygons are split if necessary.
     *
     * @param polygons the polygons to clip
     *
     * @return the clipped list of polygons
     */
    private List<Polygon> clipPolygons(List<Polygon> polygons) {
        build();
        if (this.plane == null) {
            return new ArrayList<>(polygons);
        }
        List<Polygon> frontP = new ArrayList<>(polygons.size());
        List<Polygon> backP = new ArrayList<>(polygons.size());
        this.plane.splitPolygons(polygons, frontP, backP, frontP, backP);
        if (this.front != null) {
            frontP = this.front.clipPolygons(frontP);
        }
        if (this.back != null) {
            backP = this.back.clipPolygons(backP);
            frontP.addAll(backP);
        }
        return frontP;
    }

    public void getPlanarEdges(List<Edge> edges) {

    }

    public void getCoplanarEdges(List<Edge> edges) {

    }

    public void getIntersectingEdges(List<Edge> edges) {
        build();
        if (this.plane == null) {
            return;
        }
        List<Polygon> intersecting = PolygonUtil.getList(1000);
        allPolygons(intersecting);
        List<Integer> types = new ArrayList<>();
        for (Polygon p : intersecting) {
        }
        PolygonUtil.releaseList(intersecting);
    }

    private void clipPolygons(List<Polygon> polygons, List<Polygon> result) {
        build();
        if (this.plane == null) {
            result.addAll(polygons);
            return;
        }
        List<Polygon> frontP = PolygonUtil.getList(polygons.size());
        List<Polygon> backP = PolygonUtil.getList(polygons.size());
        this.plane.splitPolygons(polygons, frontP, backP, frontP, backP);
        if (this.front != null) {
            this.front.clipPolygons(frontP, result);
        }
        if (this.back != null) {
            this.back.clipPolygons(backP, result);
        }
        PolygonUtil.releaseList(frontP);
        PolygonUtil.releaseList(backP);
    }

    // Remove all polygons in this BSP tree that are inside the other BSP tree
    // `bsp`.
    /**
     * Removes all polygons in this BSP tree that are inside the specified BSP
     * tree ({@code bsp}).
     *
     * Note: polygons are splitted if necessary.
     *
     * @param bsp bsp that shall be used for clipping
     */
    public Node clipTo(Node bsp) {
        build();
        return new Node(bsp.clipPolygons(this.polygons),
                this.plane,
                this.front == null ? null : this.front.clipTo(bsp),
                this.back == null ? null : this.back.clipTo(bsp));
    }

    /**
     * Returns a list of all polygons in this BSP tree.
     *
     * @return a list of all polygons in this BSP tree
     */
    public List<Polygon> allPolygons() {
        List<Polygon> localPolygons = new ArrayList<>(1000);
        allPolygons(localPolygons);
        return localPolygons;
    }

    public void allPolygons(List<Polygon> result) {
        HashMultimap<Plane, Polygon> mm = HashMultimap.create(1000, 5);
        allPolygons(mm);
        mm.asMap().entrySet().forEach(entry -> result.addAll(Polygon.merge(entry.getValue())));
    }

    public void allPolygons(Multimap<Plane, Polygon> result) {
        this.polygons.forEach(p -> result.put(p.plane, p));
        if (this.front != null) {
            this.front.allPolygons(result);
        }
        if (this.back != null) {
            this.back.allPolygons(result);
        }
        this.polygonQueue.forEach(p -> result.put(p.plane, p));
    }

    private Node duplicate() {
        // OK it's like clone, shut up
        Node node = new Node(
                new ArrayList<>(this.polygons),
                this.plane,
                this.front == null ? null : this.front.duplicate(),
                this.back == null ? null : this.back.duplicate());
        node.add(this.polygonQueue);
        return node;
    }

    public Node union(Node other) {
        Node out = duplicate();
        other.allPolygons(out.polygonQueue);
        return out;
    }

    public void add(List<Polygon> polygons) {
        this.polygonQueue.addAll(polygons);
    }

    /**
     * Build a BSP tree out of {@code polygons}. When called on an existing
     * tree, the new polygons are filtered down to the bottom of the tree and
     * become new nodes there. Each set of polygons is partitioned using the
     * first polygon (no heuristic is used to pick a good split).
     *
     * @param polygons polygons used to build the BSP
     */
    public final void build() {
        if (polygonQueue.isEmpty()) {
            return;
        }
        if (this.plane == null) {
            // Remove the first polygon, we will use this as our plane
            Polygon polygon = polygonQueue.remove(0);
            if (polygon.vertices.size() > 3) {
                // Sometimes non-triangular polygons don't have a valid plane
                int planeCat = polygon.plane.categorise(polygon, new ArrayList<>());
                if (Plane.COPLANAR != planeCat) {
                    List<Polygon> triangles = polygon.toTriangles();
                    this.plane = triangles.get(0).getPlane();
                    this.polygons.add(triangles.get(0));
                    this.polygonQueue.addAll(0, triangles.subList(1, triangles.size()));
                } else {
                    this.plane = polygon.plane;
                    this.polygons.add(polygon);
                }
            } else {
                this.plane = polygon.plane;
                this.polygons.add(polygon);
            }
        }

        List<Polygon> frontP = PolygonUtil.getList(polygonQueue.size());
        List<Polygon> backP = PolygonUtil.getList(polygonQueue.size());
//        List<Polygon> frontP = new ArrayList<>(polygonQueue.size());
//        List<Polygon> backP = new ArrayList<>(polygonQueue.size());

        // parellel version does not work here WHY NOT?
        polygonQueue.forEach((polygon) -> {
            this.plane.splitPolygon(
                    polygon, this.polygons, this.polygons, frontP, backP);
        });
//        this.plane.splitPolygons(polygonQueue, frontP, backP, frontP, backP);

        if (!frontP.isEmpty()) {
            if (this.front == null) {
                this.front = new Node();
            }
            this.front.add(frontP);
        }
        if (!backP.isEmpty()) {
            if (this.back == null) {
                this.back = new Node();
            }
            this.back.add(backP);
        }
        PolygonUtil.releaseList(frontP);
        PolygonUtil.releaseList(backP);
        this.polygonQueue.clear();
    }
}
