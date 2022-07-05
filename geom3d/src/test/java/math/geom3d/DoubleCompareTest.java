/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d;

import math.geom2d.Tolerance2D;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class DoubleCompareTest {

    @Test
    public void test() {
        System.out.println(Double.compare(Double.NaN, -1));
        System.out.println(Tolerance2D.compare(Double.NaN, 1));
    }
}
