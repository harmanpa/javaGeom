/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import math.geom2d.math.LineArcIterator;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.conic.Ellipse2D;
import math.geom2d.conic.EllipseArc2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Rectangle2D;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class CurveOpsTest {

    @Test
    public void testCircle() {
        // primitives
        new LineArcIterator(new Circle2D(0, 0, 1)) {
            @Override
            public void handleLine(LineSegment2D line) {
            }

            @Override
            public void handleArc(CircleArc2D arc) {
            }
        }.iterate();
    }
    
    @Test
    public void testEllipse() {
        // primitives
        new LineArcIterator(new Ellipse2D(0, 0, 1, 2, 0)) {
            @Override
            public void handleLine(LineSegment2D line) {
            }

            @Override
            public void handleArc(CircleArc2D arc) {
            }
        }.iterate();
    }
    
    @Test
    public void testEllipseArc() {
        // primitives
        new LineArcIterator(new EllipseArc2D(0, 0, 1, 2, 0, 0, 1)) {
            @Override
            public void handleLine(LineSegment2D line) {
            }

            @Override
            public void handleArc(CircleArc2D arc) {
            }
        }.iterate();
    }
    
    @Test
    public void testRectangle() {
        // primitives
        new LineArcIterator(new Rectangle2D(0, 0, 1, 1)) {
            @Override
            public void handleLine(LineSegment2D line) {
            }

            @Override
            public void handleArc(CircleArc2D arc) {
            }
        }.iterate();
    }
}
