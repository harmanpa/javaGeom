package tech.cae.binpacking;

//Hao Hua, Southeast University, whitegreen@163.com
import java.util.ArrayList;
import java.util.Collections;
import tech.cae.binpacking.exceptions.ConvexHullException;
import tech.cae.binpacking.exceptions.PackingException;

public class Pack2D implements Cloneable {

    public ArrayList<Strip> fixs = new ArrayList<>();
    public ArrayList<Strip> movs = new ArrayList<>();
    public Convex cntConvex;
    private final double[][] trigos;
    private final int rotSteps;
    private static final double PI = Math.PI;
    private final double WID, HEI;
    private static final double areasc = 1E-6; //
    private final double preferX; // 0.501 or 1

    @Override
    public Pack2D clone() throws CloneNotSupportedException {
        super.clone();
        Pack2D np = new Pack2D(trigos, rotSteps, WID, HEI, preferX);
        np.movs = movs;
        np.cntConvex = cntConvex;
        return np;
    }

    public Pack2D(double[][] trigos, int rotSteps, double WID, double HEI, double preferX) {
        this.trigos = trigos;
        this.rotSteps = rotSteps;
        this.WID = WID;
        this.HEI = HEI;
        this.preferX = preferX;
    }

    public boolean isEmpty() {
        return movs.isEmpty();
    }

    public Pack2D(double[][][] polys, double offset, Double segment_max_length, int rotSteps, double WID, double HEI, double preferX) throws PackingException {
        for (int i = 0; i < polys.length; i++) {
            double[][] poly = polys[i];
            Strip strip = new Strip(i, poly, offset, segment_max_length);
            movs.add(strip);
        }
        Collections.sort(movs); // ascend, remove form the end (largest)
        this.preferX = Math.max(Math.min(preferX, 1.0), 0.0);

        this.rotSteps = rotSteps;
        trigos = new double[rotSteps][];
        for (int i = 0; i < rotSteps; i++) {
            double theta = i * 2 * PI / rotSteps;
            trigos[i] = new double[]{Math.cos(theta), Math.sin(theta)};
        }
        this.WID = WID;
        this.HEI = HEI;
        System.out.println(WID + "*" + HEI);
    }

    private void place1stStrip() throws PackingException {
        int rotid = -1;
        double minArea = Double.MAX_VALUE;
        int sid = movs.size() - 1; // ***************************************** last one
        Strip first = movs.get(sid);
        
        for (int i = 0; i < rotSteps; i++) {
            double[][] tp = M.rotate(trigos[i], first.outps);
            double[] bd = M.boundBox(tp); // minx, maxx, miny, maxy
            if (bd[1] - bd[0] > WID || bd[3] - bd[2] > HEI) {
                continue;
            }
            double area = areasc * (bd[1] - bd[0]) * (bd[3] - bd[2]);
            double[] center = M.mean(tp);
            double len = preferX * (center[0] - bd[0]) + (1 - preferX) * (center[1] - bd[2]);
            area *= len;
            if (minArea > area) {
                minArea = area;
                rotid = i;
            }
        }
        if (rotid < 0) {
            throw new PackingException("First part too big for sheet");
        }
        double[][] tp = M.rotate(trigos[rotid], first.outps);
        double[] bd = M.boundBox(tp);
        first.fix_rotate_move(trigos[rotid], new double[]{-bd[0], -bd[2]});
        movs.remove(sid);
        fixs.add(first);
        try {
            cntConvex = new Convex(first.inps);
        } catch (ConvexHullException ex) {
            throw new PackingException("Failed to place strip", ex);
        }
    }

    public void packOneSheet(boolean Abey) throws PackingException {
        place1stStrip();
        int size = movs.size();
        for (int i = 0; i < size; i++) {
            if (Abey) {
                placeAnotherStrip_Abey(size - 1 - i);
            } else {
                placeAnotherStrip_Dalalah(size - 1 - i);
            }
        }
        ArrayList<Strip> list = new ArrayList<>();
        movs.stream().filter((stp) -> !(null == stp)).forEachOrdered((stp) -> {
            list.add(stp);
        });
        movs = list;
        System.out.println(fixs.size() + "->" + movs.size());
    }

    public double leftOverArea() {
        double sum = 0;
        sum = movs.stream().map((strip) -> strip.inarea).reduce(sum, (accumulator, _item) -> accumulator + _item);
        return sum;
    }

    private boolean placeAnotherStrip_Abey(int sid) throws PackingException {
        Strip stp = movs.get(sid);
        double min = 1000000000;
        double[] min_cossin = null;
        double[] min_trans = null;
        Convex min_con = null;
        double[][] opl = stp.outps;
        for (int i = 0; i < opl.length; i++) { // each vertex of new strip
            double[] p = opl[i];
            double[] d0 = M.sub(opl[(i - 1 + opl.length) % opl.length], p);
            double[] d2 = M.sub(opl[(i + 1) % opl.length], p);
            double mag0 = M.mag(d0);
            double mag2 = M.mag(d2);
            double cb0 = d0[0] / mag0;
            double sb0 = d0[1] / mag0;
            double cb2 = d2[0] / mag2;
            double sb2 = d2[1] / mag2;

            for (Strip fixed : fixs) {
                double[][] fopl = fixed.outps;
                for (int j = 0; j < fopl.length; j++) { // each vertex of each fixed strip
                    double[] v = fopl[j];
                    double[] t0 = M.sub(fopl[(j - 1 + fopl.length) % fopl.length], v);
                    double[] t2 = M.sub(fopl[(j + 1) % fopl.length], v);
                    double m0 = M.mag(t0);
                    double m2 = M.mag(t2);
                    double ca0 = t0[0] / m0;
                    double sa0 = t0[1] / m0;
                    double ca2 = t2[0] / m2;
                    double sa2 = t2[1] / m2;

                    for (int h = 0; h < 2; h++) { // two angles
                        double[] cossin;
                        if (0 == h) {
                            cossin = new double[]{ca0 * cb2 + sa0 * sb2, sa0 * cb2 - ca0 * sb2}; // a0 - b2
                        } else {
                            cossin = new double[]{ca2 * cb0 + sa2 * sb0, sa2 * cb0 - ca2 * sb0};// a2 - b0
                        }
                        double[][] rot_opl = M.rotate(cossin, opl);
                        double[] trans = M.sub(v, rot_opl[i]); // *****************
                        double[][] trans_rot_outpoly = M.move(trans, rot_opl);
                        if (feasible(trans_rot_outpoly)) {
                            double[][] trans_rot_inpoly = M.move(trans, M.rotate(cossin, stp.inps));
                            Convex tmpcon;
                            try {
                                tmpcon = cntConvex.clone();
                                for (double[] trp : trans_rot_inpoly) {
                                    tmpcon.increment_hull(trp);
                                }
                            } catch (CloneNotSupportedException ex) {
                                throw new PackingException("Internal error", ex);
                            } catch (ConvexHullException ex) {
                                throw new PackingException("Failed due to convex hull error", ex);
                            }

                            double conarea = areasc * M.areaAbs(tmpcon.convex);
                            double[] center = M.mean(trans_rot_inpoly);
                            double area;
                            area = conarea * (preferX * Math.abs(center[0]) + (1 - preferX) * Math.abs(center[1]));
                            if (min > area) {
                                min = area;
                                min_cossin = cossin;
                                min_trans = trans;
                                min_con = tmpcon;
                            }
                        }
                    }// for h
                }
            }
        } // for each vertex of new strip
        if (null == min_cossin) // no solution, stop
        {
            return false;
        }
        stp.fix_rotate_move(min_cossin, min_trans);
        movs.set(sid, null);
        fixs.add(stp);
        cntConvex = min_con;
        return true;
    }

    private boolean placeAnotherStrip_Dalalah(int sid) throws PackingException {
        Strip stp = movs.get(sid);
        double min = 1000000000;
        int min_rotid = -1;
        double[] min_trans = null;
        Convex min_con = null;
        for (int i = 0; i < rotSteps; i++) {// each angle of new strip
            double[][] rotated_outpoly = M.rotate(trigos[i], stp.outps);
            double[][] rotated_inpoly = M.rotate(trigos[i], stp.inps);

            for (double[] p : rotated_outpoly) { // each vertex of new strip
                for (Strip fixed : fixs) {
                    for (double[] v : fixed.outps) { // each vertex of each fixed strip
                        double[] trans = M.sub(v, p);
                        double[][] trans_rot_outpoly = M.move(trans, rotated_outpoly);
                        if (feasible(trans_rot_outpoly)) {
                            double[][] trans_rot_inpoly = M.move(trans, rotated_inpoly);
                            Convex tmpcon;
                            try {
                                tmpcon = cntConvex.clone();
                                for (double[] trp : trans_rot_inpoly) {
                                    tmpcon.increment_hull(trp);
                                }
                            } catch (CloneNotSupportedException ex) {
                                throw new PackingException("Internal error", ex);
                            } catch (ConvexHullException ex) {
                                throw new PackingException("Failed due to convex hull error", ex);
                            }

                            double conarea = areasc * M.areaAbs(tmpcon.convex);
                            double[] center = M.mean(trans_rot_outpoly);// trans_rot_outpoly
                            double area = conarea * (preferX * Math.abs(center[0]) + Math.abs(center[1]));
                            if (min > area) {
                                min = area;
                                min_rotid = i;
                                min_trans = trans;
                                min_con = tmpcon;
                            }
                        }
                    }
                }
            } // for each vertex of new strip
        } // for each angle of new strip
        if (0 > min_rotid) // no solution, stop
        {
            return false;
        }
        stp.fix_rotate_move(trigos[min_rotid], min_trans);
        // movs.remove(sid);
        movs.set(sid, null);
        fixs.add(stp);
        cntConvex = min_con;
        return true;
    }

    private boolean feasible(double[][] poly) {
        // TODO: Changing these tests could allow parts closer to edge
        for (double[] p : poly) {
            if (p[0] < 0 || p[0] > WID) {
                return false;
            }
            if (p[1] < 0 || p[1] > HEI) {
                return false;
            }
        }
        return fixs.stream().noneMatch((fixed) -> (overlap(poly, fixed.inps)));
    }

    public int[] getStripIds() {
        int size = fixs.size();
        int[] ids = new int[size];
        for (int i = 0; i < size; i++) {
            ids[i] = fixs.get(i).id;
        }
        return ids;
    }

    public double[][] getStripRotations() {
        int size = fixs.size();
        double[][] rots = new double[size][];
        for (int i = 0; i < size; i++) {
            rots[i] = fixs.get(i).trigo;
        }
        return rots;
    }

    public double[][] getStripPositions() {
        int size = fixs.size();
        double[][] ps = new double[size][];
        for (int i = 0; i < size; i++) {
            ps[i] = fixs.get(i).position;
        }
        return ps;
    }

    private static boolean overlap(double[][] poly1, double[][] poly2) {
        if (!M.intersect_boundBox(poly1, poly2)) {
            return false;
        }
        double[][] sml, big;
        if (M.areaAbs(poly1) < M.areaAbs(poly2)) {
            sml = poly1;
            big = poly2;
        } else {
            sml = poly2;
            big = poly1;
        }
        for (double[] p : sml) {
            if (M.inside(p, big)) {
                return true;
            }
        }
        for (int i = 0; i < sml.length; i++) {
            for (int j = 0; j < big.length; j++) {
                if (M.intersection(sml[i], sml[(i + 1) % sml.length], big[j], big[(j + 1) % big.length])) {
                    return true;
                }
            }
        }
        return false;
    }

}
