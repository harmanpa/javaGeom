package math.geom2d.bezier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import math.geom2d.Point2D;

public final class BezierPath {

    static final Matcher MATCH_POINT = Pattern.compile("\\s*(\\d+)[^\\d]+(\\d+)\\s*").matcher("");

    BezierListProducer path;

    private final ArrayList<Point2D> pointList = new ArrayList<>();
    double resolution = 0.075;

    /**
     * Creates a new instance of Animate
     */
    public BezierPath() {
    }

    /**
     * Creates a new instance of Animate
     *
     * @param path
     */
    public BezierPath(String path) {
        parsePathString(path);
    }

    public void parsePathString(String d) {
        this.path = new BezierListProducer();
        parsePathList(d);
    }

    protected void parsePathList(String list) {
        // Oh come on... well i have no idea what this regx is going to parse for, good
        // luck
        final Matcher matchPathCmd = Pattern
                .compile("([MmLlHhVvAaQqTtCcSsZz])|([-+]?((\\d*\\.\\d+)|(\\d+))([eE][-+]?\\d+)?)").matcher(list);

        // Tokenize
        LinkedList<String> tokens = new LinkedList<>();
        while (matchPathCmd.find()) {
            tokens.addLast(matchPathCmd.group());
        }

        char curCmd = 'Z';
        while (!tokens.isEmpty()) {
            String curToken = tokens.removeFirst();
            char initChar = curToken.charAt(0);
            if ((initChar >= 'A' && initChar <= 'Z') || (initChar >= 'a' && initChar <= 'z')) {
                curCmd = initChar;
            } else {
                tokens.addFirst(curToken);
            }
            float x, y;
            switch (curCmd) {
                case 'M':
                    x = nextFloat(tokens);
                    y = nextFloat(tokens);
                    path.movetoAbs(x, y);
                    pointList.add(new Point2D(x, y));
                    curCmd = 'L';
                    break;
                case 'm':
                    x = nextFloat(tokens);
                    y = nextFloat(tokens);
                    path.movetoRel(x, y);
                    pointList.add(new Point2D(x, y));
                    curCmd = 'l';
                    break;
                case 'L':
                    path.linetoAbs(nextFloat(tokens), nextFloat(tokens));
                    pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));
                    break;
                case 'l':
                    path.linetoRel(nextFloat(tokens), nextFloat(tokens));
                    pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));
                    break;
                case 'H':
                    path.linetoHorizontalAbs(nextFloat(tokens));

                    pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));

                    break;
                case 'h':
                    path.linetoHorizontalRel(nextFloat(tokens));

                    pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));

                    break;
                case 'V':
                    path.linetoVerticalAbs(nextFloat(tokens));

                    pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));

                    break;
                case 'v':
                    path.linetoVerticalAbs(nextFloat(tokens));
                    pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));
                    break;
                case 'A':
                case 'a':
                    break;
                case 'Q':
                    path.curvetoQuadraticAbs(nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 'q':
                    path.curvetoQuadraticAbs(nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 'T':
                    path.curvetoQuadraticSmoothAbs(nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 't':
                    path.curvetoQuadraticSmoothRel(nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 'C':
                    path.curvetoCubicAbs(nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 'c':
                    path.curvetoCubicRel(nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 'S':
                    path.curvetoCubicSmoothAbs(nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 's':
                    path.curvetoCubicSmoothRel(nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
                    for (double i = resolution; i < 1; i += resolution) {
                        pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(i));
                    }
                    break;
                case 'Z':
                case 'z':
                    path.closePath();
                    // pointList.add(path.bezierSegs.get(path.bezierSegs.size() - 1).eval(1));
                    break;
                case '/':
                    // comment line
                    break;
                default:
                // Invalid path element, ignore
            }
        }
    }

    static protected float nextFloat(LinkedList<String> l) {
        String s = l.removeFirst();
        return Float.parseFloat(s);
    }

    /**
     * Evaluates this animation element for the passed interpolation time.
     * Interp must be on [0..1].
     *
     * @param interp
     * @return
     */
    public Point2D eval(double interp) {
        Point2D point = new Point2D(0, 0);
        if (interp < 0.001) {
            interp = 0.001;
        }
        if (interp > 0.9999) {
            interp = 0.9999;
        }

        double curLength = path.curveLength * interp;
        for (Bezier bez : path.bezierSegs) {
            double bezLength = bez.getLength();
            if (curLength < bezLength) {
                double param = curLength / bezLength;
                point = bez.eval(param);
                break;
            }

            curLength -= bezLength;
        }

        return point;
    }

    /**
     * Evaluates this animation element for the passed interpolation time.
     * Interp must be on [0..1].
     *
     * @return
     */
    public List<Point2D> evaluate() {
        return pointList;
    }

}
