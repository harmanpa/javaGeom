/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom2d.circulinear;

import java.util.ArrayList;
import java.util.List;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.line.LineSegment2D;

/**
 *
 * @author peter
 */
public class RoundedRectangle2D extends GenericCirculinearRing2D {

    public RoundedRectangle2D(double width, double height, double radius) {
        super(makeElements(width, height, radius));
    }

    static List<CirculinearElement2D> makeElements(double width, double height, double radius) {
        // Ensure all scaled correctly
        double h = Math.max(height, 0);
        double w = Math.max(width, 0);
        double h2 = h / 2;
        double w2 = w / 2;
        double r = Math.min(Math.min(w2, h2), Math.max(radius, 0));
        double h2r = h2 - r;
        double w2r = w2 - r;
        double halfpi = Math.PI / 2;
        List<CirculinearElement2D> elements = new ArrayList<>(8);
        elements.add(new LineSegment2D(-w2r, -h2, w2r, -h2));
        elements.add(new CircleArc2D(w2r, -h2r, r, -halfpi, 0, true));
        elements.add(new LineSegment2D(w2, -h2r, w2, h2r));
        elements.add(new CircleArc2D(w2r, h2r, r, 0, halfpi, true));
        elements.add(new LineSegment2D(w2r, h2, -w2r, h2));
        elements.add(new CircleArc2D(-w2r, h2r, r, halfpi, Math.PI, true));
        elements.add(new LineSegment2D(-w2, h2r, -w2, -h2r));
        elements.add(new CircleArc2D(-w2r, -h2r, r, Math.PI, Math.PI + halfpi, true));
        return elements;
    }
}
