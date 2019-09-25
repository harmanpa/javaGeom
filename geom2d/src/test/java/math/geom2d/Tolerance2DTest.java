/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class Tolerance2DTest {

    @Test
    public void test() {
        Tolerance2D.set(1e-3);
        Assert.assertEquals(Tolerance2D.round(2.0001), Tolerance2D.round(2.0002));
        Assert.assertNotSame(Tolerance2D.round(2.001), Tolerance2D.round(2.002));
        Tolerance2D.reset();
    }
}
