package math.geom3d.io;

import math.geom3d.Point3D;
import math.geom3d.csg.CSG;
import math.geom3d.csg.Polygon;
import de.javagl.obj.ReadableObj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.ObjReader;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class OBJParser {

    public static CSG parse(Path filepath) throws IOException {
        return toCSG(ObjReader.read(Files.newBufferedReader(filepath, StandardCharsets.US_ASCII)));
    }

    static CSG toCSG(ReadableObj obj) {
        List<Polygon> polygons = new ArrayList<>(obj.getNumFaces());
        for (int i = 0; i < obj.getNumFaces(); i++) {
            polygons.add(toPolygon(obj, obj.getFace(i)));
        }
        return CSG.fromPolygons(polygons);
    }

    static Polygon toPolygon(ReadableObj obj, ObjFace face) {
        Point3D[] vertices = new Point3D[face.getNumVertices()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = getVertex(obj, face.getVertexIndex(i));
        }
        return Polygon.fromPoints(vertices);
    }

    static Point3D getVertex(ReadableObj obj, int index) {
        FloatTuple xyz = obj.getVertex(index);
        return new Point3D((double) xyz.getX(), (double) xyz.getY(), (double) xyz.getZ());
    }
}