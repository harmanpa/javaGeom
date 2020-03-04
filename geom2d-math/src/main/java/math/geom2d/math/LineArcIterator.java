/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.conic.Ellipse2D;
import math.geom2d.conic.EllipseArc2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Polygon2D;

/**
 *
 * @author peter
 */
public abstract class LineArcIterator {

    private final Iterator<SmoothCurve2D> delegate;
    //private final double maxError;

    public LineArcIterator(Polygon2D polygon) {
        this(splitPolygon(polygon));
    }

    public LineArcIterator(Collection<SmoothCurve2D> elements) {
        this(elements.iterator());
    }

    public LineArcIterator(Iterator<SmoothCurve2D> delegate) {
        this.delegate = delegate;
        //this.maxError = Math.abs(maxError);
    }

    public LineArcIterator(Curve2D cc) {
        this(splitCurve(cc));
    }

    static Collection<SmoothCurve2D> splitCurve(Curve2D cc) {
        List<SmoothCurve2D> elements = new ArrayList<>();
        cc.continuousCurves().forEach(c -> elements.addAll(c.smoothPieces()));
        return elements;
    }

    static Collection<SmoothCurve2D> splitPolygon(Polygon2D p) {
        List<SmoothCurve2D> elements = new ArrayList<>();
        elements.addAll(p.edges());
        return elements;
    }

    public void iterate() {
        while (delegate.hasNext()) {
            SmoothCurve2D element = delegate.next();
            handle(element);
        }
    }

    protected void handle(SmoothCurve2D element) {
        if (element instanceof LineSegment2D) {
            LineSegment2D line = (LineSegment2D) element;
            handleLine(line);
        } else if (element instanceof CircleArc2D) {
            CircleArc2D arc = (CircleArc2D) element;
            handleArc(arc);
        } else if (element instanceof Circle2D) {
            Circle2D circle = (Circle2D) element;
            handleCircle(circle);
        } else if (element instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) element;
            if (ellipse.isCircle()) {
                handleCircle(new Circle2D(ellipse.center(), ellipse.semiMajorAxisLength()));
            } else {
                handleEllipse(ellipse);
            }
        } else if (element instanceof EllipseArc2D) {
            EllipseArc2D ellipseArc = (EllipseArc2D) element;
            if (ellipseArc.getSupportingEllipse().isCircle()) {
                handleArc(new CircleArc2D(ellipseArc.getSupportingEllipse().center(), ellipseArc.getSupportingEllipse().semiMajorAxisLength(), ellipseArc.getStartAngle(), ellipseArc.getAngleExtent()));
            } else {
                handleEllipticalArc(ellipseArc);
            }
        } else {
            handleDefault(element);
        }
    }

    public void handleDefault(SmoothCurve2D curve) {
        //double c = Math.abs(curve.curvature(curve.t0()+(curve.t1()-curve.t0())));
        // TODO: Need length!
        //int n = (int)Math.ceil(L*c/(2*Math.acos(1-c*maxError)));        
        curve.asPolyline(2).edges().forEach(e -> handleLine(e));
    }

    public abstract void handleLine(LineSegment2D line);

    public abstract void handleArc(CircleArc2D arc);

    public void handleEllipticalArc(EllipseArc2D ellipseArc) {
        handleDefault(ellipseArc);
    }

    public void handleEllipse(Ellipse2D ellipse) {
        // Default implementation creates an arc from theta to theta+2*pi
        handleEllipticalArc(new EllipseArc2D(ellipse, ellipse.angle(), Math.PI * 2));
    }

    public void handleCircle(Circle2D circle) {
        // Default implementation creates an arc from theta to theta+2*pi
        handleArc(new CircleArc2D(circle, circle.angle(), Math.PI * 2));
    }

}
