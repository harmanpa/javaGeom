/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.csg.fitting;

import math.geom3d.csg.CSG;
import math.geom3d.csg.primitives.Primitive;
import math.geom3d.quickhull.QuickHullException;

/**
 *
 * @author peter
 */
public class PrimitiveFitter {

    public double error(CSG mesh, Primitive primitive) throws QuickHullException {
        CSG primitiveMesh = primitive.toCSG();
        return mesh.difference(primitiveMesh).computeVolume();
    }

}
