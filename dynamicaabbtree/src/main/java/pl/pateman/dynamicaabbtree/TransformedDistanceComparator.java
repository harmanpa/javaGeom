/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.pateman.dynamicaabbtree;

import java.util.Comparator;
import org.joml.AABBf;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class TransformedDistanceComparator implements Comparator<AABBf[]> {

    private final AffineTransform3D transformA;
    private final AffineTransform3D transformB;

    public TransformedDistanceComparator(AffineTransform3D transformA, AffineTransform3D transformB) {
        this.transformA = transformA;
        this.transformB = transformB;
    }

    @Override
    public int compare(AABBf[] o1, AABBf[] o2) {
        return Float.compare(AABBUtils.getMinDistance(o1[0], transformA, o1[1], transformB), AABBUtils.getMinDistance(o2[0], transformA, o2[1], transformB));
    }

}
