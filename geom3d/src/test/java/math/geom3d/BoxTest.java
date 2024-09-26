/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class BoxTest {

    @Test
    public void test() {
        Box3D box0 = new Box3D(0, 1, 0, 1, 0, 1);
        Box3D box1 = new Box3D(2, 3, 0, 1, 0, 1);
        Box3D box2 = new Box3D(0, 1, 2, 3, 0, 1);
        Box3D box3 = new Box3D(2, 3, 2, 3, 0, 1);
        Box3D box4 = new Box3D(2, 3, 2, 3, 2, 3);
        Assert.assertEquals("Wrong external distance box0 to box1", 1, box0.distance(box1), 1e-6);
        Assert.assertEquals("Wrong external distance box1 to box0", 1, box1.distance(box0), 1e-6);
        Assert.assertEquals("Wrong external distance box0 to box2", 1, box0.distance(box2), 1e-6);
        Assert.assertEquals("Wrong external distance box2 to box0", 1, box2.distance(box0), 1e-6);
        Assert.assertEquals("Wrong external distance box0 to box3", Math.sqrt(2), box0.distance(box3), 1e-6);
        Assert.assertEquals("Wrong external distance box3 to box0", Math.sqrt(2), box3.distance(box0), 1e-6);
        Assert.assertEquals("Wrong external distance box0 to box4", Math.sqrt(3), box0.distance(box4), 1e-6);
        Assert.assertEquals("Wrong external distance box4 to box0", Math.sqrt(3), box4.distance(box0), 1e-6);
    }

    @Test
    public void test2() {
        Box3D box0 = new Box3D(0, 1, 0, 1, 0, 1);
        Box3D box1 = new Box3D(0.8, 1.8, 0, 1, 0, 1);
        Box3D box2 = new Box3D(0, 1, 0.8, 1.8, 0, 1);
        Box3D box3 = new Box3D(0.8, 1.8, 0.8, 1.8, 0, 1);
        Box3D box4 = new Box3D(0.8, 1.8, 0.8, 1.8, 0.8, 1.8);
        Assert.assertEquals("Wrong internal distance box0 to box1", -0.2, box0.distance(box1), 1e-6);
        Assert.assertEquals("Wrong internal distance box1 to box0", -0.2, box1.distance(box0), 1e-6);
        Assert.assertEquals("Wrong internal distance box0 to box2", -0.2, box0.distance(box2), 1e-6);
        Assert.assertEquals("Wrong internal distance box2 to box0", -0.2, box2.distance(box0), 1e-6);
        Assert.assertEquals("Wrong internal distance box0 to box3", -0.2, box0.distance(box3), 1e-6);
        Assert.assertEquals("Wrong internal distance box3 to box0", -0.2, box3.distance(box0), 1e-6);
        Assert.assertEquals("Wrong internal distance box0 to box4", -0.2, box0.distance(box4), 1e-6);
        Assert.assertEquals("Wrong internal distance box4 to box0", -0.2, box4.distance(box0), 1e-6);
    }
}
