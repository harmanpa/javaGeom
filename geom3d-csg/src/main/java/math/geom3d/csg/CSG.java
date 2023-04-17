/**
 * CSG.java
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import math.geom3d.Box3D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.primitives.Cube;
import math.geom3d.csg.primitives.Sphere;
import math.geom3d.csg.util.HullUtil;
import math.geom3d.quickhull.QuickHullException;
import math.geom3d.transform.AffineTransform3D;

//import javafx.scene.paint.Color;
//import javafx.scene.paint.PhongMaterial;
//import javafx.scene.shape.CullFace;
//import javafx.scene.shape.MeshView;
//import javafx.scene.shape.TriangleMesh;
//import javafx.scene.transform.Affine;
// TODO: Auto-generated Javadoc
/**
 * Constructive Solid Geometry (CSG).
 *
 * This implementation is a Java port of
 *
 * href="https://github.com/evanw/csg.js/" https://github.com/evanw/csg.js/ with
 * some additional features like polygon extrude, transformations etc. Thanks to
 * the author for creating the CSG.js library.<br>
 * <br>
 *
 * Implementation Details
 *
 * All CSG operations are implemented in terms of two functions,
 * {@link Node#clipTo(eu.mihosoft.vrl.v3d.Node)} and {@link Node#invert()},
 * which remove parts of a BSP tree inside another BSP tree and swap solid and
 * empty space, respectively. To find the union of {@code a} and {@code b}, we
 * want to remove everything in {@code a} inside {@code b} and everything in
 * {@code b} inside {@code a}, then combine polygons from {@code a} and
 * {@code b} into one solid:
 *
 *
 * a.clipTo(b); b.clipTo(a); a.build(b.allPolygons());
 *
 *
 * The only tricky part is handling overlapping coplanar polygons in both trees.
 * The code above keeps both copies, but we need to keep them in one tree and
 * remove them in the other tree. To remove them from {@code b} we can clip the
 * inverse of {@code b} against {@code a}. The code for union now looks like
 * this:
 *
 *
 * a.clipTo(b); b.clipTo(a); b.invert(); b.clipTo(a); b.invert();
 * a.build(b.allPolygons());
 *
 *
 * Subtraction and intersection naturally follow from set operations. If union
 * is {@code A | B}, differenceion is {@code A - B = ~(~A | B)} and intersection
 * is {@code A & B =
 * ~(~A | ~B)} where {@code ~} is the complement operator.
 */
public class CSG implements Shape3D {

    /**
     * The polygons.
     */
    private final List<Polygon> polygons;

    /**
     * The default opt type.
     */
    private static OptType defaultOptType = OptType.CSG_BOUND;

    /**
     * The opt type.
     */
    private final OptType optType;

    private Node node;
    private Box3D bounds;

    /**
     * Instantiates a new csg.
     */
    public CSG() {
        this(new ArrayList<>());
    }
    
    public CSG(List<Polygon> polygons) {
        this(polygons, defaultOptType);
    }
    
    public CSG(List<Polygon> polygons, OptType optType) {
        this.polygons = polygons;
        this.optType = optType;
    }
    
    private CSG(Node node, OptType optType) {
        this(node.allPolygons(), optType);
        this.node = node;
    }
    
    public synchronized Node getNode() {
        if(this.node==null) {
            this.node = new Node(this.getPolygons());
        }
        return this.node;
    }

    @Override
    public boolean isEmpty() {
        return polygons.isEmpty();
    }

    @Override
    public boolean isBounded() {
        return true;
    }

    @Override
    public Box3D boundingBox() {
        return getBounds();
    }

    @Override
    public CSG transform(AffineTransform3D trans) {
        return transformed(Transform.from(trans));
    }

    @Override
    public double distance(Point3D p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean contains(Point3D point) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * To z min.
     *
     * @param target the target
     * @return the csg
     */
    public CSG toZMin(CSG target) {
        return this.transformed(new Transform().translateZ(-target.getBounds().getMinZ()));
    }

    /**
     * To z max.
     *
     * @param target the target
     * @return the csg
     */
    public CSG toZMax(CSG target) {
        return this.transformed(new Transform().translateZ(-target.getBounds().getMaxZ()));
    }

    /**
     * To x min.
     *
     * @param target the target
     * @return the csg
     */
    public CSG toXMin(CSG target) {
        return this.transformed(new Transform().translateX(-target.getBounds().getMinX()));
    }

    /**
     * To x max.
     *
     * @param target the target
     * @return the csg
     */
    public CSG toXMax(CSG target) {
        return this.transformed(new Transform().translateX(-target.getBounds().getMaxX()));
    }

    /**
     * To y min.
     *
     * @param target the target
     * @return the csg
     */
    public CSG toYMin(CSG target) {
        return this.transformed(new Transform().translateY(-target.getBounds().getMinY()));
    }

    /**
     * To y max.
     *
     * @param target the target
     * @return the csg
     */
    public CSG toYMax(CSG target) {
        return this.transformed(new Transform().translateY(-target.getBounds().getMaxY()));
    }

    /**
     * To z min.
     *
     * @return the csg
     */
    public CSG toZMin() {
        return toZMin(this);
    }

    /**
     * To z max.
     *
     * @return the csg
     */
    public CSG toZMax() {
        return toZMax(this);
    }

    /**
     * To x min.
     *
     * @return the csg
     */
    public CSG toXMin() {
        return toXMin(this);
    }

    /**
     * To x max.
     *
     * @return the csg
     */
    public CSG toXMax() {
        return toXMax(this);
    }

    /**
     * To y min.
     *
     * @return the csg
     */
    public CSG toYMin() {
        return toYMin(this);
    }

    /**
     * To y max.
     *
     * @return the csg
     */
    public CSG toYMax() {
        return toYMax(this);
    }

    public CSG move(Number x, Number y, Number z) {
        return transformed(new Transform().translate(x.doubleValue(), y.doubleValue(), z.doubleValue()));
    }

    public CSG move(Vertex v) {
        return transformed(new Transform().translate(v.getX(), v.getY(), v.getZ()));
    }

    public CSG move(Vector3D v) {
        return transformed(new Transform().translate(v.getX(), v.getY(), v.getZ()));
    }

    public CSG move(Number[] posVector) {
        return move(posVector[0], posVector[1], posVector[2]);
    }

    /**
     * Movey.
     *
     * @param howFarToMove the how far to move
     * @return the csg
     */
    // Helper/wrapper functions for movement
    public CSG movey(Number howFarToMove) {
        return this.transformed(Transform.unity().translateY(howFarToMove.doubleValue()));
    }

    /**
     * Movez.
     *
     * @param howFarToMove the how far to move
     * @return the csg
     */
    public CSG movez(Number howFarToMove) {
        return this.transformed(Transform.unity().translateZ(howFarToMove.doubleValue()));
    }

    /**
     * Movex.
     *
     * @param howFarToMove the how far to move
     * @return the csg
     */
    public CSG movex(Number howFarToMove) {
        return this.transformed(Transform.unity().translateX(howFarToMove.doubleValue()));
    }

    public List<CSG> move(List<Transform> p) {
        List<CSG> bits = new ArrayList<>(p.size());
        p.forEach((_item) -> {
            bits.add(this);
        });
        return move(bits, p);
    }

    public static List<CSG> move(List<CSG> slice, List<Transform> p) {
        List<CSG> s = new ArrayList<>(Math.max(slice.size(), p.size()));
        // s.add(slice.get(0));
        for (int i = 0; i < slice.size() && i < p.size(); i++) {
            s.add(slice.get(i).transformed(p.get(i)));
        }
        return s;
    }

    /**
     * mirror about y axis.
     *
     *
     * @return the csg
     */
    // Helper/wrapper functions for movement
    public CSG mirrory() {
        return this.scaley(-1);
    }

    /**
     * mirror about z axis.
     *
     * @return the csg
     */
    public CSG mirrorz() {
        return this.scalez(-1);
    }

    /**
     * mirror about x axis.
     *
     * @return the csg
     */
    public CSG mirrorx() {
        return this.scalex(-1);
    }

    public CSG rot(Number x, Number y, Number z) {
        return rotx(x.doubleValue()).roty(y.doubleValue()).rotz(z.doubleValue());
    }

    public CSG rot(Number[] posVector) {
        return rot(posVector[0], posVector[1], posVector[2]);
    }

    /**
     * Rotz.
     *
     * @param degreesToRotate the degrees to rotate
     * @return the csg
     */
    // Rotation function, rotates the object
    public CSG rotz(Number degreesToRotate) {
        return this.transformed(new Transform().rotZ(degreesToRotate.doubleValue()));
    }

    /**
     * Roty.
     *
     * @param degreesToRotate the degrees to rotate
     * @return the csg
     */
    public CSG roty(Number degreesToRotate) {
        return this.transformed(new Transform().rotY(degreesToRotate.doubleValue()));
    }

    /**
     * Rotx.
     *
     * @param degreesToRotate the degrees to rotate
     * @return the csg
     */
    public CSG rotx(Number degreesToRotate) {
        return this.transformed(new Transform().rotX(degreesToRotate.doubleValue()));
    }

    /**
     * Scalez.
     *
     * @param scaleValue the scale value
     * @return the csg
     */
    // Scale function, scales the object
    public CSG scalez(Number scaleValue) {
        return this.transformed(new Transform().scaleZ(scaleValue.doubleValue()));
    }

    /**
     * Scaley.
     *
     * @param scaleValue the scale value
     * @return the csg
     */
    public CSG scaley(Number scaleValue) {
        return this.transformed(new Transform().scaleY(scaleValue.doubleValue()));
    }

    /**
     * Scalex.
     *
     * @param scaleValue the scale value
     * @return the csg
     */
    public CSG scalex(Number scaleValue) {
        return this.transformed(new Transform().scaleX(scaleValue.doubleValue()));
    }

    /**
     * Scale.
     *
     * @param scaleValue the scale value
     * @return the csg
     */
    public CSG scale(Number scaleValue) {
        return this.transformed(new Transform().scale(scaleValue.doubleValue()));
    }

    /**
     * Constructs a CSG from a list of {@link Polygon} instances.
     *
     * @param polygons polygons
     * @return a CSG instance
     */
    public static CSG fromPolygons(List<Polygon> polygons) {
        return new CSG(polygons);
    }

    /**
     * Constructs a CSG from the specified {@link Polygon} instances.
     *
     * @param polygons polygons
     * @return a CSG instance
     */
    public static CSG fromPolygons(Polygon... polygons) {
        return fromPolygons(Arrays.asList(polygons));
    }

    /**
     * Gets the polygons.
     *
     * @return the polygons of this CSG
     */
    public List<Polygon> getPolygons() {
        return polygons;
    }
    
    public Stream<Polygon> streamPolygons() {
        return polygons.size() > 200 ? polygons.parallelStream() : polygons.stream();
    }

    /**
     * Return a new CSG solid representing the union of this csg and the
     * specified csg.
     *
     * Note: Neither this csg nor the specified csg are weighted.
     *
     *
     * A.union(B)
     *
     * +-------+ +-------+ | | | | | A | | | | +--+----+ = | +----+ +----+--+ |
     * +----+ | | B | | | | | | | +-------+ +-------+
     *
     *
     *
     * @param csg other csg
     *
     * @return union of this csg and the specified csg
     */
    public CSG union(CSG csg) {

        switch (getOptType()) {
            case CSG_BOUND:
                return _unionCSGBoundsOpt(csg);
            case POLYGON_BOUND:
                return _unionPolygonBoundsOpt(csg);
            default:
                // return _unionIntersectOpt(csg);
                return _unionNoOpt(csg);
        }
    }

    /**
     * Returns a csg consisting of the polygons of this csg and the specified
     * CSGs.
     *
     * The purpose of this method is to allow fast union operations for objects
     * that do not intersect.
     *
     * WARNING: this method does not apply the csg algorithms. Therefore, please
     * ensure that this csg and the specified csg do not intersect.
     *
     * @param csgs One or more CSGs
     *
     * @return a csg consisting of the polygons of this csg and the specified
     * CSGs
     */
    public CSG dumbUnion(CSG... csgs) {
        return dumbUnion(Arrays.asList(csgs));
    }
    
    public CSG dumbUnion(List<CSG> csgs) {
        int n = getPolygons().size() + csgs.stream().mapToInt(csg -> csg.getPolygons().size()).sum();
        List<Polygon> polygons = new ArrayList<>(n);
        polygons.addAll(getPolygons());
        for(CSG csg : csgs) {
            polygons.addAll(csg.getPolygons());
        }
        return fromPolygons(polygons);
    }
    
    /**
     * Return a new CSG solid representing the union of this csg and the
     * specified csgs.
     *
     * Note: Neither this csg nor the specified csg are weighted.
     *
     *
     * A.union(B)
     *
     * +-------+ +-------+ | | | | | A | | | | +--+----+ = | +----+ +----+--+ |
     * +----+ | | B | | | | | | | +-------+ +-------+
     *
     *
     *
     * @param csgs other csgs
     *
     * @return union of this csg and the specified csgs
     */
    public CSG union(List<CSG> csgs) {

        CSG result = this;

        for (int i = 0; i < csgs.size(); i++) {
            CSG csg = csgs.get(i);
            result = result.union(csg);
        }

        return result;
    }

    /**
     * Return a new CSG solid representing the union of this csg and the
     * specified csgs.
     *
     * Note: Neither this csg nor the specified csg are weighted.
     *
     *
     * A.union(B)
     *
     * +-------+ +-------+ | | | | | A | | | | +--+----+ = | +----+ +----+--+ |
     * +----+ | | B | | | | | | | +-------+ +-------+
     *
     *
     *
     * @param csgs other csgs
     *
     * @return union of this csg and the specified csgs
     */
    public CSG union(CSG... csgs) {
        return union(Arrays.asList(csgs));
    }

    /**
     * Returns the convex hull of this csg.
     *
     * @return the convex hull of this csg
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public CSG hull() throws QuickHullException {
        return HullUtil.hull(this);
    }

    public static CSG unionAll(CSG... csgs) {
        return unionAll(Arrays.asList(csgs));
    }

    public static CSG unionAll(List<CSG> csgs) {
        CSG first = csgs.remove(0);
        return first.union(csgs);
    }

    public static CSG hullAll(CSG... csgs) throws QuickHullException {
        return hullAll(Arrays.asList(csgs));
    }

    public static CSG hullAll(List<CSG> csgs) throws QuickHullException {
        CSG first = csgs.remove(0);
        return first.hull(csgs);
    }

    /**
     * Returns the convex hull of this csg and the union of the specified csgs.
     *
     * @param csgs csgs
     * @return the convex hull of this csg and the specified csgs
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public CSG hull(List<CSG> csgs) throws QuickHullException {
        return dumbUnion(csgs).hull();
    }

    /**
     * Returns the convex hull of this csg and the union of the specified csgs.
     *
     * @param csgs csgs
     * @return the convex hull of this csg and the specified csgs
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public CSG hull(CSG... csgs) throws QuickHullException {
        return hull(Arrays.asList(csgs));
    }

    /**
     * _union csg bounds opt.
     *
     * @param csg the csg
     * @return the csg
     */
    private CSG _unionCSGBoundsOpt(CSG csg) {
        // System.err.println("WARNING: using " + CSG.OptType.NONE
        // + " since other optimization types missing for union operation.");
        return _unionIntersectOpt(csg);
    }

    /**
     * _union polygon bounds opt.
     *
     * @param csg the csg
     * @return the csg
     */
    private CSG _unionPolygonBoundsOpt(CSG csg) {
        List<Polygon> inner = new ArrayList<>(getPolygons().size());
        List<Polygon> outer = new ArrayList<>(getPolygons().size());

        Box3D b = csg.getBounds();

        this.streamPolygons().forEach((p) -> {
            if (b.intersects(p.getBounds())) {
                inner.add(p);
            } else {
                outer.add(p);
            }
        });

        List<Polygon> allPolygons = new ArrayList<>(getPolygons().size()*2);

        if (!inner.isEmpty()) {
            CSG innerCSG = CSG.fromPolygons(inner);

            allPolygons.addAll(outer);
            allPolygons.addAll(innerCSG._unionNoOpt(csg).getPolygons());
        } else {
            allPolygons.addAll(this.getPolygons());
            allPolygons.addAll(csg.getPolygons());
        }
        return new CSG(allPolygons, getOptType());
    }

    /**
     * Optimizes for intersection. If csgs do not intersect create a new csg
     * that consists of the polygon lists of this csg and the specified csg. In
     * this case no further space partitioning is performed.
     *
     * @param csg csg
     * @return the union of this csg and the specified csg
     */
    private CSG _unionIntersectOpt(CSG csg) {
        Box3D b = csg.getBounds();
        
        boolean intersects = streamPolygons().anyMatch(p -> b.intersects(p.getBounds()));

        List<Polygon> allPolygons = new ArrayList<>(getPolygons().size()+csg.getPolygons().size());

        if (intersects) {
            return _unionNoOpt(csg);
        } else {
            allPolygons.addAll(this.getPolygons());
            allPolygons.addAll(csg.getPolygons());
        }

        return new CSG(allPolygons, getOptType());
    }

    /**
     * _union no opt.
     *
     * @param csg the csg
     * @return the csg
     */
    private CSG _unionNoOpt(CSG csg) {
        Node a = getNode();
        Node b = csg.getNode();
        Node c = a.clipTo(b);
        return new CSG(c.union(b.clipTo(c).invert().clipTo(c).invert()), getOptType());
//        a.clipTo(b);
//        b.clipTo(a);
//        b.invert();
//        b.clipTo(a);
//        b.invert();
//        a.build(b.allPolygons());
//        return new CSG(a.allPolygons(), getOptType());
    }

    /**
     * Return a new CSG solid representing the difference of this csg and the
     * specified csgs.
     *
     * Note: Neither this csg nor the specified csgs are weighted.
     *
     *
     * A.difference(B)
     *
     * +-------+ +-------+ | | | | | A | | | | +--+----+ = | +--+ +----+--+ |
     * +----+ | B | | | +-------+
     *
     *
     * @param csgs other csgs
     * @return difference of this csg and the specified csgs
     */
    public CSG difference(List<CSG> csgs) {

        if (csgs.isEmpty()) {
            return this;
        }

        CSG csgsUnion = csgs.get(0);

        for (int i = 1; i < csgs.size(); i++) {
            csgsUnion = csgsUnion.union(csgs.get(i));
        }

        return difference(csgsUnion);
    }

    /**
     * Return a new CSG solid representing the difference of this csg and the
     * specified csgs.
     *
     * Note: Neither this csg nor the specified csgs are weighted.
     *
     *
     * A.difference(B)
     *
     * +-------+ +-------+ | | | | | A | | | | +--+----+ = | +--+ +----+--+ |
     * +----+ | B | | | +-------+
     *
     *
     * @param csgs other csgs
     * @return difference of this csg and the specified csgs
     */
    public CSG difference(CSG... csgs) {

        return difference(Arrays.asList(csgs));
    }

    /**
     * Return a new CSG solid representing the difference of this csg and the
     * specified csg.
     *
     * Note: Neither this csg nor the specified csg are weighted.
     *
     *
     * A.difference(B)
     *
     * +-------+ +-------+ | | | | | A | | | | +--+----+ = | +--+ +----+--+ |
     * +----+ | B | | | +-------+
     *
     *
     * @param csg other csg
     * @return difference of this csg and the specified csg
     */
    public CSG difference(CSG csg) {
        try {
            // Check to see if a CSG operation is attempting to difference with
            // no
            // polygons
            if (!this.getPolygons().isEmpty() && !csg.getPolygons().isEmpty()) {
                switch (getOptType()) {
                    case CSG_BOUND:
                        return _differenceCSGBoundsOpt(csg);
                    case POLYGON_BOUND:
                        return _differencePolygonBoundsOpt(csg);
                    default:
                        return _differenceNoOpt(csg);
                }
            } else {
                return this;
            }
        } catch (QuickHullException ex) {
            try {
                System.err.println("CSG difference failed, performing workaround");
                //ex.printStackTrace();
                CSG intersectingParts = csg
                        .intersect(this);

                if (!intersectingParts.getPolygons().isEmpty()) {
                    switch (getOptType()) {
                        case CSG_BOUND:
                            return _differenceCSGBoundsOpt(intersectingParts);
                        case POLYGON_BOUND:
                            return _differencePolygonBoundsOpt(intersectingParts);
                        default:
                            return _differenceNoOpt(intersectingParts);
                    }
                } else {
                    return this;
                }
            } catch (QuickHullException e) {
                return this;
            }
        }

    }

    /**
     * _difference csg bounds opt.
     *
     * @param csg the csg
     * @return the csg
     */
    private CSG _differenceCSGBoundsOpt(CSG csg) throws QuickHullException {
        CSG b = csg;
        CSG bb = new Cube(csg.getBounds()).toCSG();
        CSG a1 = this._differenceNoOpt(bb);
        CSG a2 = this.intersect(bb);
        return a2._differenceNoOpt(b)._unionIntersectOpt(a1);
    }

    /**
     * _difference polygon bounds opt.
     *
     * @param csg the csg
     * @return the csg
     */
    private CSG _differencePolygonBoundsOpt(CSG csg) {
        List<Polygon> inner = new ArrayList<>(getPolygons().size());
        List<Polygon> outer = new ArrayList<>(getPolygons().size());

        Box3D b = csg.getBounds();

        this.streamPolygons().forEach((p) -> {
            if (b.intersects(p.getBounds())) {
                inner.add(p);
            } else {
                outer.add(p);
            }
        });

        CSG innerCSG = CSG.fromPolygons(inner);

        List<Polygon> allPolygons = new ArrayList<>(getPolygons().size()*2);
        allPolygons.addAll(outer);
        allPolygons.addAll(innerCSG._differenceNoOpt(csg).getPolygons());

        return new CSG(allPolygons, getOptType());
    }

    /**
     * _difference no opt.
     *
     * @param csg the csg
     * @return the csg
     */
    private CSG _differenceNoOpt(CSG csg) {
        Node a = getNode();
        Node b = csg.getNode();
        Node c = a.invert().clipTo(b);        
        Node d = b.clipTo(c).invert().clipTo(c).invert();
        return new CSG(c.union(d).invert(), getOptType());
//        a.invert();
//        a.clipTo(b);
//        b.clipTo(a);
//        b.invert();
//        b.clipTo(a);
//        b.invert();
//        a.build(b.allPolygons());
//        a.invert();
//
//        CSG csgA = CSG.fromPolygons(a.allPolygons()).optimization(getOptType());
//        return csgA;
    }

    /**
     * Return a new CSG solid representing the intersection of this csg and the
     * specified csg.
     *
     * Note: Neither this csg nor the specified csg are weighted.
     *
     *
     * A.intersect(B)
     *
     * +-------+ | | | A | | +--+----+ = +--+ +----+--+ | +--+ | B | | |
     * +-------+ }
     *
     *
     * @param csg other csg
     * @return intersection of this csg and the specified csg
     */
    public CSG intersect(CSG csg) {
        Node a = getNode();
        Node b = csg.getNode();
//        Node aInv = a.invert();
//        Node c = b.clipTo(aInv).invert();
//        Node d = aInv.clipTo(c);
//        return new CSG(d.union(c.clipTo(d)).invert(), getOptType());

        Node a2 = a.invert();
        Node b2 = b.clipTo(a2);
        Node b3 = b2.invert();
        Node a3 = a2.clipTo(b3);
        Node b4 = b3.clipTo(a3);
        Node a4 = a3.union(b4);
        Node a5 = a4.invert();
        return new CSG(a5, getOptType());




//        a.invert();
//        b.clipTo(a);
//        b.invert();
//        a.clipTo(b);
//        b.clipTo(a);
//        a.build(b.allPolygons());
//        a.invert();
//        return CSG.fromPolygons(a.allPolygons()).optimization(getOptType());
    }

    /**
     * Return a new CSG solid representing the intersection of this csg and the
     * specified csgs.
     *
     * Note: Neither this csg nor the specified csgs are weighted.
     *
     *
     * A.intersect(B)
     *
     * +-------+ | | | A | | +--+----+ = +--+ +----+--+ | +--+ | B | | |
     * +-------+ }
     *
     *
     * @param csgs other csgs
     * @return intersection of this csg and the specified csgs
     */
    public CSG intersect(List<CSG> csgs) {

        if (csgs.isEmpty()) {
            return this;
        }

        CSG csgsUnion = csgs.get(0);

        for (int i = 1; i < csgs.size(); i++) {
            csgsUnion = csgsUnion.union(csgs.get(i));
        }

        return intersect(csgsUnion);
    }

    /**
     * Return a new CSG solid representing the intersection of this csg and the
     * specified csgs.
     *
     * Note: Neither this csg nor the specified csgs are weighted.
     *
     *
     * A.intersect(B)
     *
     * +-------+ | | | A | | +--+----+ = +--+ +----+--+ | +--+ | B | | |
     * +-------+ }
     *
     *
     * @param csgs other csgs
     * @return intersection of this csg and the specified csgs
     */
    public CSG intersect(CSG... csgs) {
        return intersect(Arrays.asList(csgs));
    }

    /**
     * Returns a transformed copy of this CSG.
     *
     * @param transform the transform to apply
     *
     * @return a transformed copy of this CSG
     */
    public CSG transformed(Transform transform) {
        if (getPolygons().isEmpty()) {
            return this;
        }
        List<Polygon> newpolygons = this.streamPolygons()
                .map(p -> p.transform(transform))
                .collect(Collectors.toList());
        return new CSG(newpolygons, getOptType());
    }

    /**
     * Returns the bounds of this csg. SIDE EFFECT bounds is created and simply
     * returned if existing
     *
     * @return bounds of this csg
     */
    public Box3D getBounds() {
        if (bounds != null) {
            return bounds;
        }
        if (getPolygons().isEmpty()) {
            bounds = new Box3D();
            return bounds;
        }
        bounds = Box3D.fromPoints(streamPolygons()
                .flatMap(p -> p.vertices.stream()
                .map(v -> v.pos))
                .collect(Collectors.toList()));
        return bounds;
    }

    public Point3D getCenter() {
        return new Point3D(
                getCenterX(),
                getCenterY(),
                getCenterZ());
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return CenterX
     */
    public double getCenterX() {
        return ((getMinX() / 2) + (getMaxX() / 2));
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return CenterY
     */
    public double getCenterY() {
        return ((getMinY() / 2) + (getMaxY() / 2));
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return CenterZ
     */
    public double getCenterZ() {
        return ((getMinZ() / 2) + (getMaxZ() / 2));
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MaxX
     */
    public double getMaxX() {
        return getBounds().getMaxX();
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MaxY
     */
    public double getMaxY() {
        return getBounds().getMaxY();
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MaxZ
     */
    public double getMaxZ() {
        return getBounds().getMaxZ();
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MinX
     */
    public double getMinX() {
        return getBounds().getMinX();
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MinY
     */
    public double getMinY() {
        return getBounds().getMinY();
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return tMinZ
     */
    public double getMinZ() {
        return getBounds().getMinZ();
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MinX
     */
    public double getTotalX() {
        return (-this.getMinX() + this.getMaxX());
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return MinY
     */
    public double getTotalY() {
        return (-this.getMinY() + this.getMaxY());
    }

    /**
     * Helper function wrapping bounding box values
     *
     * @return tMinZ
     */
    public double getTotalZ() {
        return (-this.getMinZ() + this.getMaxZ());
    }

    /**
     * Gets the opt type.
     *
     * @return the optType
     */
    private OptType getOptType() {
        return optType != null ? optType : defaultOptType;
    }

    /**
     * Sets the default opt type.
     *
     * @param optType the optType to set
     */
    public static void setDefaultOptType(OptType optType) {
        defaultOptType = optType;
    }

    /**
     * The Enum OptType.
     */
    public static enum OptType {

        /**
         * The csg bound.
         */
        CSG_BOUND,
        /**
         * The polygon bound.
         */
        POLYGON_BOUND,
        /**
         * The none.
         */
        NONE
    }

    /**
     * This is a simplified version of a minkowski transform using convex hull
     * and the internal list of convex polygons The shape is placed at the
     * vertex of each point on a polygon, and the result is convex hulled
     * together. This collection is returned. To make a normal insets,
     * difference this collection To make an outset by the normals, union this
     * collection with this object.
     *
     * @param travelingShape a shape to sweep around
     * @return
     */
    public ArrayList<CSG> minkowski(CSG travelingShape) {
        HashMap<Vertex, CSG> map = new HashMap<>();
        travelingShape.getPolygons().forEach((Polygon p) -> {
            p.vertices.stream().filter((v) -> (map.get(v) == null)// use hashmap to avoid duplicate locations
            ).forEachOrdered((v) -> {
                map.put(v, CSG.this.move(v));
            });
        });
        return new ArrayList<>(map.values());
    }

    /**
     * minkowskiDifference performs an efficient difference of the minkowski
     * transform of the intersection of an object. if you have 2 objects and
     * need them to fit with a specific tolerance as described as the distance
     * from he normal of the surface, then this function will effectinatly
     * compute that value.
     *
     * @param itemToDifference the object that needs to fit
     * @param minkowskiObject the object to represent the offset
     * @return
     */
    public CSG minkowskiDifference(CSG itemToDifference, CSG minkowskiObject) {
        CSG intersection = this.intersect(itemToDifference);

        ArrayList<CSG> csgDiff = intersection.minkowski(minkowskiObject);
        CSG result = this;
        for (int i = 0; i < csgDiff.size(); i++) {
            result = result.difference(csgDiff.get(i));
        }
        return result;
    }

    /**
     * minkowskiDifference performs an efficient difference of the Minkowski
     * transform of the intersection of an object.if you have 2 objects and need
     * them to fit with a specific tolerance as described as the distance from
     * the normal of the surface, then this function will compute that value.
     *
     * @param itemToDifference the object that needs to fit
     * @param tolerance the tolerance distance
     * @return
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public CSG minkowskiDifference(CSG itemToDifference, double tolerance) throws QuickHullException {
        double shellThickness = Math.abs(tolerance);
        if (shellThickness < 0.001) {
            return this;
        }
        return minkowskiDifference(itemToDifference, new Cube(shellThickness).toCSG());
    }

    public CSG toolOffset(Number sn, int nFaces) throws QuickHullException {
        double shellThickness = sn.doubleValue();
        boolean cut = shellThickness < 0;
        shellThickness = Math.abs(shellThickness);
        if (shellThickness < 0.001) {
            return this;
        }
        double z = shellThickness;
        if (z > this.getTotalZ() / 2) {
            z = this.getTotalZ() / 2;
        }
        CSG printNozzel = new Sphere(z / 2.0, nFaces / 2, 4).toCSG();

        if (cut) {
            ArrayList<CSG> mikObjs = minkowski(printNozzel);
            CSG remaining = this;
            for (CSG bit : mikObjs) {
                remaining = remaining.intersect(bit);
            }
            return remaining;
        }
        return union(minkowski(printNozzel));
    }

//    private int getNumFacesForOffsets() {
//        return getNumFacesInOffset();
//    }

    public CSG makeKeepaway(Number sn) {
        double shellThickness = sn.doubleValue();

        double x = Math.abs(this.getBounds().getMaxX()) + Math.abs(this.getBounds().getMinX());
        double y = Math.abs(this.getBounds().getMaxY()) + Math.abs(this.getBounds().getMinY());

        double z = Math.abs(this.getBounds().getMaxZ()) + Math.abs(this.getBounds().getMinZ());

        double xtol = (x + shellThickness) / x;
        double ytol = (y + shellThickness) / y;
        double ztol = (z + shellThickness) / z;

        double xPer = -(Math.abs(this.getBounds().getMaxX()) - Math.abs(this.getBounds().getMinX())) / x;
        double yPer = -(Math.abs(this.getBounds().getMaxY()) - Math.abs(this.getBounds().getMinY())) / y;
        double zPer = -(Math.abs(this.getBounds().getMaxZ()) - Math.abs(this.getBounds().getMinZ())) / z;

        // println " Keep away x = "+y+" new = "+ytol
        return this.transformed(new Transform().scale(xtol, ytol, ztol))
                .transformed(new Transform().translateX(shellThickness * xPer))
                .transformed(new Transform().translateY(shellThickness * yPer))
                .transformed(new Transform().translateZ(shellThickness * zPer));

    }

    /**
     * A test to see if 2 CSG's are touching. The fast-return is a bounding box
     * check If bounding boxes overlap, then an intersection is performed and
     * the existance of an interscting object is returned
     *
     * @param incoming
     * @return
     */
    public boolean touching(CSG incoming) {
        // Fast bounding box overlap check, quick fail if not intersecting
        // bounding boxes
        if (this.getMaxX() > incoming.getMinX() && this.getMinX() < incoming.getMaxX()
                && this.getMaxY() > incoming.getMinY() && this.getMinY() < incoming.getMaxY()
                && this.getMaxZ() > incoming.getMinZ() && this.getMinZ() < incoming.getMaxZ()) {
            // Run a full intersection
            CSG inter = this.intersect(incoming);
            if (!inter.getPolygons().isEmpty()) {
                // intersection success
                return true;
            }
        }
        return false;
    }

    /**
     * Get Bounding box
     *
     * @return A CSG that completely encapsulates the base CSG, centered around
     * it
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public CSG getBoundingBox() throws QuickHullException {
        return new Cube((-this.getMinX() + this.getMaxX()),
                (-this.getMinY() + this.getMaxY()),
                (-this.getMinZ() + this.getMaxZ()))
                .toCSG()
                .toXMax()
                .movex(this.getMaxX())
                .toYMax()
                .movey(this.getMaxY())
                .toZMax()
                .movez(this.getMaxZ());
    }

//    public String getName() {
//        return name;
//    }
//
//    public CSG setName(String name) {
//        this.name = name;
//        return this;
//    }
//
//    public static int getNumFacesInOffset() {
//        return numFacesInOffset;
//    }
//
//    public static void setNumFacesInOffset(int numFacesInOffset) {
//        CSG.numFacesInOffset = numFacesInOffset;
//    }

    /**
     * Computes and returns the volume of this CSG based on a triangulated
     * version of the internal mesh.
     *
     * @return volume of this csg
     */
    public double computeVolume() {
        if (getPolygons().isEmpty()) {
            return 0;
        }
        // triangulate polygons (parallel for larger meshes)
        Stream<Polygon> triangleStream = streamPolygons()
                .flatMap(poly -> poly.toTriangles().stream());
        // compute sum over signed volumes of triangles
        // we use parallel streams for larger meshes
        // see http://chenlab.ece.cornell.edu/Publication/Cha/icip01_Cha.pdf
        double volume = triangleStream.mapToDouble(tri -> {
            Point3D p1 = tri.vertices.get(0).pos;
            Point3D p2 = tri.vertices.get(1).pos;
            Point3D p3 = tri.vertices.get(2).pos;
            return new Vector3D(p1).dot(new Vector3D(p2).cross(new Vector3D(p3))) / 6.0;
        }).sum();
        volume = Math.abs(volume);
        return volume;
    }
}
