/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.List;
import java.util.function.Function;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;

/**
 *
 * @author peter
 * @param <T>
 */
public class Shape3DFitter<T extends Shape3D> extends AbstractFitter<T, Point3D> {

    protected Shape3DFitter(int nParameters, Function<double[], T> constructor, Function<T, double[]> destructor, Function<List<Point3D>, T> guesser) {
        super(nParameters, constructor, destructor, (shape, point) -> shape.distance(point), guesser);
    }

}
