/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.io;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.CSG;

/**
 *
 * @author peter
 */
public class STLWriter {
    
    public static void write(String name, CSG csg, Writer writer, double scale) throws IOException {
        write(name, Triangle3D.fromCSG(csg), writer, scale);
    }

    public static void write(String name, List<Triangle3D> triangles, Writer writer, double scale) throws IOException {
        writer.append("solid ").append(name).append("\n");
        for (Triangle3D triangle : triangles) {
            if (!triangle.isEmpty()) {
                write(triangle.scale(scale), writer);
            }
        }
        writer.append("endsolid ").append(name).append("\n");
        writer.flush();
    }

    static void write(Triangle3D triangle, Writer writer) throws IOException {
        writer.append("  facet normal ");
        appendVec(writer, triangle.getNormal());
        writer.append("\n    outer loop\n");
        for (Point3D vec : triangle.getVertices()) {
            writer.append("      vertex ");
            appendVec(writer, vec);
            writer.append("\n");
        }
        writer.append("    endloop\n  endfacet\n");
    }

    static void appendVec(Appendable app, Point3D vec) throws IOException {
        app.append(Double.toString(vec.getX())).append(' ').append(Double.toString(vec.getY())).append(' ').append(Double.toString(vec.getZ()));
    }

    static void appendVec(Appendable app, Vector3D vec) throws IOException {
        app.append(Double.toString(vec.getX())).append(' ').append(Double.toString(vec.getY())).append(' ').append(Double.toString(vec.getZ()));
    }
}
