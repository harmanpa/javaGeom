/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.svg;

import java.util.ArrayList;
import java.util.List;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.conic.CircleArc2D;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author peter
 */
@RunWith(Parameterized.class)
public class SVGArcTest {

    private final CircleArc2D arc;
    private final String svg;

    public SVGArcTest(CircleArc2D arc, String svg) {
        this.arc = arc;
        this.svg = svg;
    }

    @Parameters(name = "{1}")
    public static Object[] parameters() {
        double PI = Math.PI;
        double HalfPI = Math.PI / 2;
        double TwoPI = Math.PI * 2;
        double OneAndHalfPI = Math.PI * 1.5;
        List<CircleArc2D> arcs = new ArrayList<>();
        arcs.add(new CircleArc2D(0, 0, 1, 0, HalfPI, true));//0 0
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, PI, true));//0 0
        arcs.add(new CircleArc2D(0, 0, 1, PI, OneAndHalfPI, true));//0 0
        arcs.add(new CircleArc2D(0, 0, 1, OneAndHalfPI, TwoPI, true));//0 0
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, 0, true));//1 0
        arcs.add(new CircleArc2D(0, 0, 1, PI, HalfPI, true));//1 0
        arcs.add(new CircleArc2D(0, 0, 1, OneAndHalfPI, PI, true));//1 0
        arcs.add(new CircleArc2D(0, 0, 1, TwoPI, OneAndHalfPI, true));//1 0
        arcs.add(new CircleArc2D(0, 0, 1, 0, HalfPI, false));//1 1
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, PI, false));//1 1
        arcs.add(new CircleArc2D(0, 0, 1, PI, OneAndHalfPI, false));//1 1
        arcs.add(new CircleArc2D(0, 0, 1, OneAndHalfPI, TwoPI, false));//1 1
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, 0, false));//0 1
        arcs.add(new CircleArc2D(0, 0, 1, PI, HalfPI, false));//0 1
        arcs.add(new CircleArc2D(0, 0, 1, OneAndHalfPI, PI, false));//0 1
        arcs.add(new CircleArc2D(0, 0, 1, TwoPI, OneAndHalfPI, false));//0 1
        arcs.add(new CircleArc2D(0, 0, 1, 0, PI, true));
        arcs.add(new CircleArc2D(0, 0, 1, PI, TwoPI, true));
        arcs.add(new CircleArc2D(0, 0, 1, PI, 0, true));
        arcs.add(new CircleArc2D(0, 0, 1, TwoPI, PI, true));
        arcs.add(new CircleArc2D(0, 0, 1, 0, PI, false));
        arcs.add(new CircleArc2D(0, 0, 1, PI, TwoPI, false));
        arcs.add(new CircleArc2D(0, 0, 1, PI, 0, false));
        arcs.add(new CircleArc2D(0, 0, 1, TwoPI, PI, false));
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, OneAndHalfPI, true));
        arcs.add(new CircleArc2D(0, 0, 1, OneAndHalfPI, HalfPI, true));
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, OneAndHalfPI, false));
        arcs.add(new CircleArc2D(0, 0, 1, OneAndHalfPI, HalfPI, false));
        arcs.add(new CircleArc2D(0, 0, 1, 0, OneAndHalfPI, true));
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, TwoPI, true));
        arcs.add(new CircleArc2D(0, 0, 1, PI, HalfPI, true));
        arcs.add(new CircleArc2D(0, 0, 1, 0, OneAndHalfPI, false));
        arcs.add(new CircleArc2D(0, 0, 1, HalfPI, TwoPI, false));
        arcs.add(new CircleArc2D(0, 0, 1, PI, HalfPI, false));
        return arcs.stream().map(a -> parameter(a)).toArray();
    }

    static Object[] parameter(CircleArc2D arc) {
        return new Object[]{arc, SVGPaths.toString(arc)};
    }

    @Test
    public void test() {
        CirculinearCurve2D parsed = SVGPaths.parse(svg);
        CircleArc2D parsedArc = (CircleArc2D)parsed.continuousCurves().iterator().next().smoothPieces().iterator().next();
        Assert.assertTrue("First point", parsed.firstPoint().almostEquals(arc.firstPoint(), 1e-3));
        Assert.assertTrue("Last point", parsed.lastPoint().almostEquals(arc.lastPoint(), 1e-3));
        Assert.assertTrue("Centre point", parsedArc.supportingCircle().center().almostEquals(arc.supportingCircle().center(), 1e-3));
        Assert.assertTrue("Mid point", parsed.point(parsed.t0() + (parsed.t1() - parsed.t0()) / 2).almostEquals(arc.point(arc.t0() + (arc.t1() - arc.t0()) / 2), 1e-3));
    }
}
