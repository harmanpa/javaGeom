/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package math.geom2d.bezier;

import java.util.ArrayList;
import java.util.List;

/**
 * A handler class that generates an array of shorts and an array doubles from
 * parsing path data.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id: PathArrayProducer.java 475685 2006-11-16 11:16:05Z cam $
 */
public class BezierListProducer implements PathHandler {

    final List<Bezier> bezierSegs = new ArrayList<>();
    double[] coords = new double[6];
    double curveLength = 0f;
    BezierHistory hist = new BezierHistory();

    @Override
    public void startPath() {
        curveLength = 0f;
        bezierSegs.clear();
    }

    @Override
    public void movetoRel(double x, double y) {
        double offx = hist.lastPoint.getX();
        double offy = hist.lastPoint.getY();
        movetoAbs(offx + x, offy + y);
    }

    @Override
    public void movetoAbs(double x, double y) {
        hist.setLastPoint(x, y);
    }

    @Override
    public void closePath() {
        //command(SVGPathSeg.PATHSEG_CLOSEPATH);
    }

    @Override
    public void linetoRel(double x, double y) {
        double offx = hist.lastPoint.getX();
        double offy = hist.lastPoint.getY();

        linetoAbs(offx + x, offy + y);
    }

    @Override
    public void linetoAbs(double x, double y) {

        coords[0] = x;
        coords[1] = y;

        Bezier b = new Bezier(hist.lastPoint.getX(), hist.lastPoint.getY(), coords, 1);
        bezierSegs.add(b);
        curveLength += b.getLength();

        hist.setLastPoint(x, y);
        hist.setLastKnot(x, y);

    }

    @Override
    public void linetoHorizontalRel(double x) {
        linetoAbs(x + hist.lastPoint.getX(), hist.lastPoint.getY());
    }

    @Override
    public void linetoHorizontalAbs(double x) {
        linetoAbs(x, hist.lastPoint.getY());
    }

    @Override
    public void linetoVerticalRel(double y) {
        linetoAbs(hist.lastPoint.getX(), y + hist.lastPoint.getY());
    }

    @Override
    public void linetoVerticalAbs(double y) {
        linetoAbs(hist.lastPoint.getX(), y);
    }

    @Override
    public void curvetoCubicRel(double x1, double y1,
            double x2, double y2,
            double x, double y) {
        double offx = hist.lastPoint.getX();
        double offy = hist.lastPoint.getY();

        curvetoCubicAbs(x1 + offx, y1 + offy,
                x2 + offx, y2 + offy,
                x + offx, y + offy);
    }

    @Override
    public void curvetoCubicAbs(double x1, double y1,
            double x2, double y2,
            double x, double y) {

        coords[0] = x1;
        coords[1] = y1;
        coords[2] = x2;
        coords[3] = y2;
        coords[4] = x;
        coords[5] = y;

        Bezier b = new Bezier(hist.lastPoint.getX(), hist.lastPoint.getY(), coords, 3);
        bezierSegs.add(b);
        curveLength += b.getLength();
        hist.setLastPoint(x, y);
        hist.setLastKnot(x2, y2);
    }

    @Override
    public void curvetoCubicSmoothRel(double x2, double y2,
            double x, double y) {
        double offx = hist.lastPoint.getX();
        double offy = hist.lastPoint.getY();

        curvetoCubicSmoothAbs(x2 + offx, y2 + offy, x + offx, y + offy);
    }

    @Override
    public void curvetoCubicSmoothAbs(double x2, double y2,
            double x, double y) {

        double oldKx = hist.lastKnot.getX();
        double oldKy = hist.lastKnot.getY();
        double oldX = hist.lastPoint.getX();
        double oldY = hist.lastPoint.getY();
        //Calc knot as reflection of old knot
        double k1x = oldX * 2f - oldKx;
        double k1y = oldY * 2f - oldKy;

        coords[0] = k1x;
        coords[1] = k1y;
        coords[2] = x2;
        coords[3] = y2;
        coords[4] = x;
        coords[5] = y;

        Bezier b = new Bezier(hist.lastPoint.getX(), hist.lastPoint.getY(), coords, 3);
        bezierSegs.add(b);
        curveLength += b.getLength();
        hist.setLastPoint(x, y);
        hist.setLastKnot(x2, y2);
    }

    @Override
    public void curvetoQuadraticRel(double x1, double y1,
            double x, double y) {
        double offx = hist.lastPoint.getX();
        double offy = hist.lastPoint.getY();

        curvetoQuadraticAbs(x1 + offx, y1 + offy, x + offx, y + offy);
    }

    @Override
    public void curvetoQuadraticAbs(double x1, double y1,
            double x, double y) {

        coords[0] = x1;
        coords[1] = y1;
        coords[2] = x;
        coords[3] = y;

        Bezier b = new Bezier(hist.lastPoint.getX(), hist.lastPoint.getY(), coords, 2);
        bezierSegs.add(b);
        curveLength += b.getLength();

        hist.setLastPoint(x, y);
        hist.setLastKnot(x1, y1);
    }

    @Override
    public void curvetoQuadraticSmoothRel(double x, double y) {
        double offx = hist.lastPoint.getX();
        double offy = hist.lastPoint.getY();

        curvetoQuadraticSmoothAbs(x + offx, y + offy);
    }

    @Override
    public void curvetoQuadraticSmoothAbs(double x, double y) {

        curvetoQuadraticAbs(hist.lastKnot.getX(), hist.lastKnot.getY(), x, y);
    }

    @Override
    public void arcRel(double rx, double ry,
            double xAxisRotation,
            boolean largeArcFlag, boolean sweepFlag,
            double x, double y) {

    }

    @Override
    public void arcAbs(double rx, double ry,
            double xAxisRotation,
            boolean largeArcFlag, boolean sweepFlag,
            double x, double y) {

    }

    @Override
    public void endPath() {
        hist.setLastPoint(hist.startPoint.getX(), hist.startPoint.getY());
        hist.setLastKnot(hist.startPoint.getX(), hist.startPoint.getY());
    }
}
