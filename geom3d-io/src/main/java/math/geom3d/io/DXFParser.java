/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import math.geom3d.Point3D;

/**
 *
 * @author peter
 */
public class DXFParser {

    public static List<Triangle3D> parseDXFFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Triangle3D> out = new ArrayList<>();
        boolean inFace = false;
        String id = "";
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            if ("3DFACE".equals(lines.get(i).trim())) {
                if (inFace) {
                    out.add(makeTriangle3D(data));
                }
                inFace = true;
            } else {
                if ((i % 2) == 0) {
                    id = lines.get(i);
                } else {
                    data.put(id, lines.get(i));
                }
            }
        }
        if (inFace) {
            out.add(makeTriangle3D(data));
        }
        return out;
    }

    static Triangle3D makeTriangle3D(Map<String, String> data) {
        return new Triangle3D(makeVertex(data, "10", "20", "30"),
                makeVertex(data, "11", "21", "31"),
                makeVertex(data, "12", "22", "32"));
    }

    static Point3D makeVertex(Map<String, String> data, String xRef, String yRef, String zRef) {
        return makeVertex(data.get(xRef), data.get(yRef), data.get(zRef));
    }

    static Point3D makeVertex(String x, String y, String z) {
        return new Point3D(Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
    }

}
