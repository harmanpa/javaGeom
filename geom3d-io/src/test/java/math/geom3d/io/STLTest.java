/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import math.geom3d.csg.primitives.Cube;
import math.geom3d.quickhull.QuickHullException;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class STLTest {

    @Test
    public void test() throws IOException, QuickHullException {
        File out = new File(new File(System.getProperty("user.dir")), "target/cube.stl");
        try ( FileWriter w = new FileWriter(out)) {
            STLWriter.write("cube", new Cube(1).toCSG(), w, 1.0);
        }
    }
}
