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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (this.plane == null || polygons.isEmpty()) {
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
        List<Polygon> localPolygons = new ArrayList<>(this.polygons);
        if (this.front != null) {
            localPolygons.addAll(this.front.allPolygons());
        }
        if (this.back != null) {
            localPolygons.addAll(this.back.allPolygons());
        }
        localPolygons.addAll(this.polygonQueue);
        return localPolygons;
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
        out.add(other.allPolygons());
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
            this.plane = polygonQueue.get(0).plane;
            // This forces a tree even for poor data
            this.polygons.add(polygonQueue.remove(0));
        }

        List<Polygon> frontP = new ArrayList<>(polygonQueue.size());
        List<Polygon> backP = new ArrayList<>(polygonQueue.size());

        // parellel version does not work here WHY NOT?
        polygonQueue.forEach((polygon) -> {
            this.plane.splitPolygon(
                    polygon, this.polygons, this.polygons, frontP, backP);
        });

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
        this.polygonQueue.clear();
    }
}
