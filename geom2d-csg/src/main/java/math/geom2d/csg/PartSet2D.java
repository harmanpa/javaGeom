/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.csg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import math.geom2d.circulinear.CirculinearCurve2D;

/**
 *
 * @author peter
 */
public class PartSet2D {

    private final List<Part2D> parts;

    public PartSet2D(List<Part2D> parts) {
        this.parts = parts;
    }

    public PartSet2D(Part2D... parts) {
        this(Arrays.asList(parts));
    }

    public List<Part2D> getParts() {
        return parts;
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public int size() {
        return parts.size();
    }

    public PartSet2D add(CirculinearCurve2D curve, double tolerance) {
        return add(new Part2D(curve, tolerance));
    }

    public PartSet2D subtract(CirculinearCurve2D curve) {
        List<Part2D> newParts = new ArrayList<>();
        parts.forEach(p -> newParts.addAll(p.subtract(curve).getParts()));
        return new PartSet2D(newParts);
    }

    public PartSet2D add(Part2D part) {
        if (isEmpty()) {
            return new PartSet2D(part);
        } else {
            Part2D currentPart = part;
            List<Part2D> unaffected = new ArrayList<>();
            for (Part2D mine : parts) {
                PartSet2D combined = mine.add(currentPart);
                if (combined.size() == 1) {
                    currentPart = combined.parts.get(0);
                } else {
                    unaffected.add(mine);
                }
            }
            unaffected.add(currentPart);
            return new PartSet2D(unaffected.toArray(new Part2D[0]));
        }
    }

    public PartSet2D add(PartSet2D partSet) {
        PartSet2D mine = this;
        for (Part2D part : partSet.parts) {
            mine = mine.add(part);
        }
        return mine;
    }
}
