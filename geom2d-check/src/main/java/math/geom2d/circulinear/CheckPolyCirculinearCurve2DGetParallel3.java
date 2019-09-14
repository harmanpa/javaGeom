/**
 *
 */
package math.geom2d.circulinear;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import math.geom2d.Point2D;
import math.geom2d.circulinear.buffer.BufferCalculator;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.line.LineSegment2D;

/**
 * @author dlegland
 *
 */
public class CheckPolyCirculinearCurve2DGetParallel3 extends JPanel {

    private static final long serialVersionUID = 1L;

    PolyCirculinearCurve2D<?> curve;
    Curve2D parallel, parallel2;

    public CheckPolyCirculinearCurve2DGetParallel3() {
        double d90 = Math.PI / 2;
        double d180 = Math.PI;
        double d270 = 3 * Math.PI / 2;
        LineSegment2D line1 = new LineSegment2D(0, 10, 0, 60);
        LineSegment2D line2 = new LineSegment2D(0, 60, 50, 60);
        CircleArc2D arc3 = new CircleArc2D(50, 50, 10, d90, d270, false);
        LineSegment2D line4 = new LineSegment2D(50, 40, 50, 10);
        CircleArc2D arc5 = new CircleArc2D(40, 10, 10, 0, d180, false);
        LineSegment2D line6 = new LineSegment2D(30, 10, 0, 10);

        curve = new PolyCirculinearCurve2D<CirculinearElement2D>(
                new CirculinearElement2D[]{line1, line2, arc3, line4, arc5, line6}, true);

    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(5, 5);
        g2.translate(20, 20);
        g2.setColor(Color.BLACK);
        curve.draw(g2);
        g2.setColor(Color.BLUE);
        double r = 4;
        //curve.singularPoints().forEach(p -> new Circle2D(p, r).draw(g2));
        BufferCalculator bc = BufferCalculator.getDefaultInstance();
        parallel = bc.createContinuousParallel(curve, r);
        //parallel = curve.parallel(r);        
        //parallel.draw(g2);
        //parallel2 = CirculinearCurves2D.convert(parallel).parallel(r);
        //parallel2.draw(g2);
        Domain2D buffer = bc.computeBuffer(curve, r);
        CirculinearCurve2D z = CirculinearCurves2D.convert(buffer.contours().iterator().next());
        z.draw(g2);
        Domain2D buffer2 = bc.computeBuffer(z, r);
        buffer2.contours().iterator().next().draw(g2);
        g2.setColor(Color.CYAN);
       // buffer.fill(g2);
    }
    
    private CirculinearCurve2D inset(CirculinearCurve2D curve, double dist) {
        Domain2D buffer = BufferCalculator.getDefaultInstance().computeBuffer(curve, dist);
        return CirculinearCurves2D.convert(buffer.contours().iterator().next());
    }

    public final static void main(String[] args) {
        System.out.println("draw wedges");

        JPanel panel = new CheckPolyCirculinearCurve2DGetParallel3();
        panel.setPreferredSize(new Dimension(500, 400));
        JFrame frame = new JFrame("Draw parallel polyline");
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
