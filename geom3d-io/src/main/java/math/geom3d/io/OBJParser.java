package math.geom3d.io;

import math.geom3d.Point3D;
import math.geom3d.csg.CSG;
import de.javagl.obj.ReadableObj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.ObjReader;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class OBJParser {

    public static CSG parse(Path filepath) throws IOException {
        return Triangle3D.toCSG(parseOBJFile(filepath));
    }

    public static List<Triangle3D> parseOBJFile(InputStream is) throws IOException {
        try {
            return toTriangles(ObjReader.read(new BufferedReader(new InputStreamReader(is))));
        } finally {
            is.close();
        }
    }

    public static List<Triangle3D> parseOBJFile(File f) throws IOException {
        return parseOBJFile(f.toPath());
    }

    public static List<Triangle3D> parseOBJFile(Path filepath) throws IOException {
        return toTriangles(ObjReader.read(Files.newBufferedReader(filepath, StandardCharsets.US_ASCII)));
    }

    static List<Triangle3D> toTriangles(ReadableObj obj) {
        List<Triangle3D> triangles = new ArrayList<>(obj.getNumFaces());
        for (int i = 0; i < obj.getNumFaces(); i++) {
            toTriangles(obj, obj.getFace(i), triangles);
        }
        return triangles;
    }

    static void toTriangles(ReadableObj obj, ObjFace face, List<Triangle3D> triangles) {
        Point3D[] vertices = new Point3D[face.getNumVertices()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = getVertex(obj, face.getVertexIndex(i));
        }
        int nTriangles = vertices.length - 2;
        for (int i = 0; i < nTriangles; i++) {
            triangles.add(new Triangle3D(vertices[0], vertices[1 + i], vertices[2 + i]));
        }
    }

    static Point3D getVertex(ReadableObj obj, int index) {
        FloatTuple xyz = obj.getVertex(index);
        return new Point3D((double) xyz.getX(), (double) xyz.getY(), (double) xyz.getZ());
    }
}