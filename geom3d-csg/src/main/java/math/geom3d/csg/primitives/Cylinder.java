/**
 * Cylinder.java
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
package math.geom3d.csg.primitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.Polygon;
import math.geom3d.csg.Vertex;

// TODO: Auto-generated Javadoc
/**
 * A solid cylinder.
 *
 * The tessellation can be controlled via the {@link #numSlices} parameter.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Cylinder extends Primitive {

    private static final double MINIMUM_RADIUS = 0.001;
    private Point3D start;

    /**
     * The end.
     */
    private Point3D end;

    /**
     * The start radius.
     */
    private double startRadius;

    /**
     * The end radius.
     */
    private double endRadius;
    private static int defaultNumSlices = 16;

    /**
     * The num slices.
     */
    private int numSlices = defaultNumSlices;

    /**
     * Constructor. Creates a new cylinder with center {@code [0,0,0]} and
     * ranging from {@code [0,-0.5,0]} to {@code [0,0.5,0]}, i.e.
     * {@code size = 1}.
     */
    public Cylinder() {
        this.start = new Point3D(0, -0.5, 0);
        this.end = new Point3D(0, 0.5, 0);
        this.startRadius = 1;
        this.endRadius = 1;
        this.numSlices = defaultNumSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code start} to {@code end}
     * with the specified {@code radius}. The resolution of the tessellation can
     * be controlled with {@code numSlices}.
     *
     * @param start cylinder start
     * @param end cylinder end
     * @param radius cylinder radius
     * @param numSlices number of slices (used for tessellation)
     */
    public Cylinder(Point3D start, Point3D end, double radius, int numSlices) {
        this.start = start;
        this.end = end;
        this.startRadius = radius < MINIMUM_RADIUS ? MINIMUM_RADIUS : radius;
        this.endRadius = radius < MINIMUM_RADIUS ? MINIMUM_RADIUS : radius;
        this.numSlices = numSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code start} to {@code end}
     * with the specified {@code radius}. The resolution of the tessellation can
     * be controlled with {@code numSlices}.
     *
     * @param start cylinder start
     * @param end cylinder end
     * @param startRadius cylinder start radius
     * @param endRadius cylinder end radius
     * @param numSlices number of slices (used for tessellation)
     */
    public Cylinder(Point3D start, Point3D end, double startRadius, double endRadius, int numSlices) {
        this.start = start;
        this.end = end;
        this.startRadius = startRadius < MINIMUM_RADIUS ? MINIMUM_RADIUS : startRadius;
        this.endRadius = endRadius < MINIMUM_RADIUS ? MINIMUM_RADIUS : endRadius;
        this.numSlices = numSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code [0,0,0]} to
     * {@code [0,0,height]} with the specified {@code radius} and
     * {@code height}. The resolution of the tessellation can be controlled with
     * {@code numSlices}.
     *
     * @param radius cylinder radius
     * @param height cylinder height
     * @param numSlices number of slices (used for tessellation)
     */
    public Cylinder(double radius, double height, int numSlices) {
        this.start = new Point3D();
        this.end = new Point3D(0, 0, height);
        this.startRadius = radius < MINIMUM_RADIUS ? MINIMUM_RADIUS : radius;
        this.endRadius = radius < MINIMUM_RADIUS ? MINIMUM_RADIUS : radius;
        this.numSlices = numSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code [0,0,0]} to
     * {@code [0,0,height]} with the specified {@code radius} and
     * {@code height}. The resolution of the tessellation can be controlled with
     * {@code numSlices}.
     *
     * @param startRadius cylinder start radius
     * @param endRadius cylinder end radius
     * @param height cylinder height
     * @param numSlices number of slices (used for tessellation)
     */
    public Cylinder(double startRadius, double endRadius, double height, int numSlices) {
        this.start = new Point3D();
        this.end = new Point3D(0, 0, height);
        this.startRadius = startRadius < MINIMUM_RADIUS ? MINIMUM_RADIUS : startRadius;
        this.endRadius = endRadius < MINIMUM_RADIUS ? MINIMUM_RADIUS : endRadius;
        this.numSlices = numSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code [0,0,0]} to
     * {@code [0,0,height]} with the specified {@code radius} and
     * {@code height}. The resolution of the tessellation can be controlled with
     * {@code numSlices}.
     *
     * @param radius cylinder radius
     * @param height cylinder height
     */
    public Cylinder(double radius, double height) {
        this.start = new Point3D();
        this.end = new Point3D(0, 0, height);
        this.startRadius = radius < MINIMUM_RADIUS ? MINIMUM_RADIUS : radius;
        this.endRadius = radius < MINIMUM_RADIUS ? MINIMUM_RADIUS : radius;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code [0,0,0]} to
     * {@code [0,0,height]} with the specified {@code radius} and
     * {@code height}. The resolution of the tessellation can be controlled with
     * {@code numSlices}.
     *
     * @param startRadius cylinder start radius
     * @param endRadius cylinder end radius
     * @param height cylinder height
     */
    public Cylinder(double startRadius, double endRadius, double height) {
        this.start = new Point3D();
        this.end = new Point3D(0, 0, height);
        this.startRadius = startRadius < MINIMUM_RADIUS ? MINIMUM_RADIUS : startRadius;
        this.endRadius = endRadius < MINIMUM_RADIUS ? MINIMUM_RADIUS : endRadius;
    }

    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() {
        final Vector3D ray = new Vector3D(start, end);
        final Vector3D axisZ = ray.normalize();
        boolean isY = (Math.abs(axisZ.getY()) > 0.5);
        final Vector3D axisX = new Vector3D(isY ? 1 : 0, !isY ? 1 : 0, 0).
                cross(axisZ).normalize();
        final Vector3D axisY = axisX.cross(axisZ).normalize();
        Vertex startV = new Vertex(start, axisZ.opposite());
        Vertex endV = new Vertex(end, axisZ.normalize());
        List<Polygon> polygons = new ArrayList<>();

        for (int i = 0; i < numSlices; i++) {
            double t0 = i / (double) numSlices, t1 = (i + 1) / (double) numSlices;
            polygons.add(new Polygon(Arrays.asList(
                    startV,
                    cylPoint(axisX, axisY, axisZ, ray, start, startRadius, 0, t0, -1),
                    cylPoint(axisX, axisY, axisZ, ray, start, startRadius, 0, t1, -1))
            ));
            polygons.add(new Polygon(Arrays.asList(
                    cylPoint(axisX, axisY, axisZ, ray, start, startRadius, 0, t1, 0),
                    cylPoint(axisX, axisY, axisZ, ray, start, startRadius, 0, t0, 0),
                    cylPoint(axisX, axisY, axisZ, ray, start, endRadius, 1, t0, 0),
                    cylPoint(axisX, axisY, axisZ, ray, start, endRadius, 1, t1, 0))
            ));
            polygons.add(new Polygon(
                    Arrays.asList(
                            endV,
                            cylPoint(axisX, axisY, axisZ, ray, start, endRadius, 1, t1, 1),
                            cylPoint(axisX, axisY, axisZ, ray, start, endRadius, 1, t0, 1))
            ));
        }

        return polygons;
    }

    /**
     * Cyl point.
     *
     * @param axisX the axis x
     * @param axisY the axis y
     * @param axisZ the axis z
     * @param ray the ray
     * @param s the s
     * @param r the r
     * @param stack the stack
     * @param slice the slice
     * @param normalBlend the normal blend
     * @return the vertex
     */
    private Vertex cylPoint(
            Vector3D axisX, Vector3D axisY, Vector3D axisZ, Vector3D ray, Point3D s,
            double r, double stack, double slice, double normalBlend) {
        double angle = slice * Math.PI * 2;
        Vector3D out = axisX.times(Math.cos(angle)).plus(axisY.times(Math.sin(angle)));
        Point3D pos = s.plus(ray.times(stack)).plus(out.times(r));
        Vector3D normal = out.times(1.0 - Math.abs(normalBlend)).plus(axisZ.times(normalBlend));
        return new Vertex(pos, normal);
    }

    /**
     * Gets the start.
     *
     * @return the start
     */
    public Point3D getStart() {
        return start;
    }

    /**
     * Sets the start.
     *
     * @param start the start to set
     */
    public void setStart(Point3D start) {
        this.start = start;
    }

    /**
     * Gets the end.
     *
     * @return the end
     */
    public Point3D getEnd() {
        return end;
    }

    /**
     * Sets the end.
     *
     * @param end the end to set
     */
    public void setEnd(Point3D end) {
        this.end = end;
    }

    /**
     * Gets the start radius.
     *
     * @return the radius
     */
    public double getStartRadius() {
        return startRadius;
    }

    /**
     * Sets the start radius.
     *
     * @param radius the radius to set
     */
    public void setStartRadius(double radius) {
        this.startRadius = radius;
    }

    /**
     * Gets the end radius.
     *
     * @return the radius
     */
    public double getEndRadius() {
        return endRadius;
    }

    /**
     * Sets the end radius.
     *
     * @param radius the radius to set
     */
    public void setEndRadius(double radius) {
        this.endRadius = radius;
    }

    /**
     * Gets the num slices.
     *
     * @return the number of slices
     */
    public int getNumSlices() {
        return numSlices;
    }

    /**
     * Sets the num slices.
     *
     * @param numSlices the number of slices to set
     */
    public void setNumSlices(int numSlices) {
        this.numSlices = numSlices;
    }

}
