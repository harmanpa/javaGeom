/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.csg;

// TODO: Auto-generated Javadoc
import math.geom3d.Point3D;

/**
 * Weight function.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@FunctionalInterface
public interface WeightFunction {

    /**
     * Evaluates the function at the specified location.
     *
     * @param v location
     * @param csg csg
     * @return the weight of the specified position
     */
    public double eval(Point3D v, CSG csg);
}
