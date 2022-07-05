/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.curve;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class CurveArray3DTest {

    @Test
    public void test() {
        CurveArray3D a = new CurveArray3D(1);
        CurveArray3D b = new CurveArray3D(1);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        EqualsVerifier.forClass(CurveArray3D.class).usingGetClass().verify();
    }
    
    @Test
    public void test2() {
        PolyCurve3D a = new PolyCurve3D(1);
        PolyCurve3D b = new PolyCurve3D(1);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        EqualsVerifier.forClass(PolyCurve3D.class).usingGetClass().verify();
    }
}
