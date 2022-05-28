/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math;

import math.geom2d.Tolerance2D;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class ToleranceTests {

    @Test
    public void test1() {
        Tolerance2D.set(1e-6);
        System.out.println(Tolerance2D.round(0.0002343894732947329164371864732));
        System.out.println(Tolerance2D.round(0.0002343843554354234643263456544));
        System.out.println(Tolerance2D.hash(0.0002343894732947329164371864732));
        System.out.println(Tolerance2D.hash(0.0002343843554354234643263456544));
        Tolerance2D.set(1e-8);
        System.out.println(Tolerance2D.round(0.0002343894732947329164371864732));
        System.out.println(Tolerance2D.round(0.0002343843554354234643263456544));
        System.out.println(Tolerance2D.hash(0.0002343894732947329164371864732));
        System.out.println(Tolerance2D.hash(0.0002343843554354234643263456544));
    }
}
