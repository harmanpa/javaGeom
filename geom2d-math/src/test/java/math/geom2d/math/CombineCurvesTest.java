/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import math.geom2d.AffineTransform2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.CirculinearCurves2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.polygon.Rectangle2D;
import math.geom2d.polygon.SimplePolygon2D;

/**
 *
 * @author peter
 */
public class CombineCurvesTest extends JPanel {

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(100, 100);
        CirculinearCurve2D triangle1 = CirculinearCurves2D.convert(new SimplePolygon2D(new double[]{0,500,250}, new double[]{100,100,400}));
        CirculinearCurve2D triangle2 = CirculinearCurves2D.convert(new SimplePolygon2D(new double[]{0,500,250}, new double[]{300,300,0}));
        CirculinearCurve2D curve = Rings2D.union(triangle1, triangle2, 1e-6).get(0);
//        CirculinearCurve2D curve = CirculinearCurves2D.convert(new Rectangle2D(0, 0, 500, 400));
        g2.setColor(Color.BLACK);
        curve.draw(g2);
        CirculinearCurve2D another = new Circle2D(300 + 100 * Math.sin(System.currentTimeMillis() / 1000), 200, 220 + 50 * Math.cos(System.currentTimeMillis() / 1000));
        another.draw(g2);
        g2.setColor(Color.red);
        Rings2D.union(curve, another, 1e-6).forEach(c -> {
            c.transform(AffineTransform2D.createTranslation(600, 0)).draw(g2);
        });
        g2.setColor(Color.blue);
        Rings2D.difference(curve, another, 1e-6).forEach(c -> {
            c.transform(AffineTransform2D.createTranslation(0, 600)).draw(g2);
        });
        g2.setColor(Color.green);
        Rings2D.difference(another, curve, 1e-6).forEach(c -> {
            c.transform(AffineTransform2D.createTranslation(600, 600)).draw(g2);
        });

    }

    public final static void main(String[] args) throws InterruptedException {

        JPanel panel = new CombineCurvesTest();
        panel.setPreferredSize(new Dimension(1200, 800));
        JFrame frame = new JFrame("CSG");
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        while (true) {
            frame.repaint();
        }
    }
}
