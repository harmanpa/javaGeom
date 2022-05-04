/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;

/**
 *
 * @author peter
 * @param <T>
 */
public class Shape2DFitter<T extends Shape2D> extends AbstractFitter<T, Point2D> {

    public Shape2DFitter(int nParameters, Function<double[], T> constructor, Function<T, double[]> destructor, BiFunction<T, Point2D, Double> assessor, Function<List<Point2D>, T> guesser) {
        super(nParameters, constructor, destructor, assessor, guesser);
    }

}
