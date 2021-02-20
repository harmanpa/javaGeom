/**
 * Cube.java
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
import java.util.List;
import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.Polygon;
import math.geom3d.csg.Transform;
import math.geom3d.csg.Vertex;

// TODO: Auto-generated Javadoc
/**
 * An axis-aligned solid cuboid defined by {@code center} and
 * {@code dimensions}.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Cube extends Primitive {

    /**
     * Center of this cube.
     */
    private Point3D center;
    /**
     * Cube dimensions.
     */
    private Vector3D dimensions;

    /**
     * The centered.
     */
    private boolean centered = true;

    /**
     * Constructor. Creates a new cube with center {@code [0,0,0]} and
     * dimensions {@code [1,1,1]}.
     */
    public Cube() {
        this(1.0);
    }

    /**
     * Constructor. Creates a new cube with center {@code [0,0,0]} and
     * dimensions {@code [size,size,size]}.
     *
     * @param size size
     */
    public Cube(double size) {
        this(size, size, size);
    }

    /**
     * Constructor. Creates a new cuboid with the specified center and
     * dimensions.
     *
     * @param center center of the cuboid
     * @param dimensions cube dimensions
     */
    public Cube(Point3D center, Vector3D dimensions) {
        this.center = center;
        this.dimensions = dimensions;
    }

    /**
     * Constructor. Creates a new cuboid with center {@code [0,0,0]} and with
     * the specified dimensions.
     *
     * @param w width
     * @param h height
     * @param d depth
     */
    public Cube(double w, double h, double d) {
        this(new Point3D(), new Vector3D(w, h, d));
    }

    public Cube(Box3D box) {
        this(box.getCenter(), box.getDimensions());
    }

    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() {
        int[][][] a = {
            // position     // normal
            {{0, 4, 6, 2}, {-1, 0, 0}},
            {{1, 3, 7, 5}, {+1, 0, 0}},
            {{0, 1, 5, 4}, {0, -1, 0}},
            {{2, 6, 7, 3}, {0, +1, 0}},
            {{0, 2, 3, 1}, {0, 0, -1}},
            {{4, 5, 7, 6}, {0, 0, +1}}
        };
        List<Polygon> polygons = new ArrayList<>();
        for (int[][] info : a) {
            List<Vertex> vertices = new ArrayList<>();
            for (int i : info[0]) {
                Point3D pos = new Point3D(
                        center.getX() + dimensions.getX() * (1 * Math.min(1, i & 1) - 0.5),
                        center.getY() + dimensions.getY() * (1 * Math.min(1, i & 2) - 0.5),
                        center.getZ() + dimensions.getZ() * (1 * Math.min(1, i & 4) - 0.5)
                );
                vertices.add(new Vertex(pos, new Vector3D(
                        (double) info[1][0],
                        (double) info[1][1],
                        (double) info[1][2]
                )));
            }
            polygons.add(new Polygon(vertices));
        }
        if (!centered) {
            Transform centerTransform = Transform.unity().translate(dimensions.getX() / 2.0, dimensions.getY() / 2.0, dimensions.getZ() / 2.0);
            polygons.forEach((p) -> {
                p.transform(centerTransform);
            });
        }
        return polygons;
    }

    /**
     * Gets the center.
     *
     * @return the center
     */
    public Point3D getCenter() {
        return center;
    }

    /**
     * Sets the center.
     *
     * @param center the center to set
     * @return
     */
    public Cube setCenter(Point3D center) {
        this.center = center;
        return this;
    }

    /**
     * Gets the dimensions.
     *
     * @return the dimensions
     */
    public Vector3D getDimensions() {
        return dimensions;
    }

    /**
     * Sets the dimensions.
     *
     * @param dimensions the dimensions to set
     * @return
     */
    public Cube setDimensions(Vector3D dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    /**
     * Defines that this cube will not be centered.
     *
     * @return this cube
     */
    public Cube noCenter() {
        centered = false;
        return this;
    }

}
