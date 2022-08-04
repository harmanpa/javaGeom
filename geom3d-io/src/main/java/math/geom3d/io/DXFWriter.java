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

/**
 *
 * @author peter
 */
public class DXFWriter {

    public static void write(String name, List<Triangle3D> triangles, Writer writer, double scale) throws IOException {
        writer.append("0\nSECTION\n2\nENTITIES\n");
        for (Triangle3D triangle : triangles) {
            if (!triangle.isEmpty()) {
                write(triangle.scale(scale), writer);
            }
        }
        writer.append("0\nENDSEC\n0\nEOF\n");
        writer.flush();
    }

    static void write(Triangle3D triangle, Writer writer) throws IOException {
        writer.append("0\n3DFACE\n8\ndefault\n62\n30\n");
        for (int i = 0; i < 3; i++) {
            appendVec(writer, triangle.getVertices()[i], i);
        }
        appendVec(writer, triangle.getVertices()[0], 3);
    }

    static void appendVec(Appendable app, Point3D vec, int n) throws IOException {
        app.append("1").append(Integer.toString(n)).append("\n").append(Double.toString(vec.getX())).append("\n");
        app.append("2").append(Integer.toString(n)).append("\n").append(Double.toString(vec.getY())).append("\n");
        app.append("3").append(Integer.toString(n)).append("\n").append(Double.toString(vec.getZ())).append("\n");
    }
}
