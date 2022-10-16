/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import math.geom3d.csg.CSG;
import math.geom3d.csg.fitting.PrimitiveFitter;
import math.geom3d.csg.primitives.Cube;
import math.geom3d.quickhull.QuickHullException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author peter
 */
@RunWith(Parameterized.class)
public class TestFit {

    private final File source;

    public TestFit(File source) {
        this.source = source;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        File testDir = new File(new File(System.getProperty("user.dir")), "src/test/resources");
        List<Object[]> out = new ArrayList<>();
        for (File f : testDir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".stl")) {
                out.add(new Object[]{f});
            }
        }
        return out;
    }

    @Test
    public void test() throws IOException, QuickHullException {
        CSG mesh = STLParser.parse(source.toPath());
        System.out.println("Fitting sphere to " + source.getName());
        PrimitiveFitter.TransformedPrimitive sphere = PrimitiveFitter.fitSphere(mesh);
        if (sphere != null) {
            write("sphere", sphere.getPrimitive().toCSG().transform(sphere.getTransform()));
        }
        System.out.println("Fitting cube to " + source.getName());
        PrimitiveFitter.TransformedPrimitive cube = PrimitiveFitter.fitCube(mesh);
        if (mesh != null) {
            write("cube", cube.getPrimitive().toCSG().transform(sphere.getTransform()));
        }
        System.out.println("Fitting cylinder to " + source.getName());
        PrimitiveFitter.TransformedPrimitive cylinder = PrimitiveFitter.fitCylinder(mesh);
        if (cylinder != null) {
            write("cylinder", cylinder.getPrimitive().toCSG().transform(sphere.getTransform()));
        }
    }

    void write(String shape, CSG mesh) throws IOException {
        File out = new File(new File(System.getProperty("user.dir")), "target/" + source.getName().replace(".stl", "_" + shape + ".stl"));
        try ( FileWriter w = new FileWriter(out)) {
            STLWriter.write(shape, mesh, w, 1.0);
        }
    }
}
