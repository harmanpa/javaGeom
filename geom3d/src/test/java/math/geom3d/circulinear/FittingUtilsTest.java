/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Arrays;
import math.geom3d.fitting.FittingUtils;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class FittingUtilsTest {

    @Test
    public void test() {
        FittingUtils.indexStream(100, 3, true, -1).forEach(ind -> System.out.println(Arrays.toString(ind)));
    }
}
