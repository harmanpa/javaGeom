/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.pateman.dynamicaabbtree;

import java.util.Comparator;
import org.joml.AABBf;

/**
 *
 * @author peter
 */
public class DistanceComparator implements Comparator<AABBf[]> {

    @Override
    public int compare(AABBf[] o1, AABBf[] o2) {
        return Float.compare(AABBUtils.getMinDistance(o1[0], o1[1]), AABBUtils.getMinDistance(o2[0], o2[1]));
    }

}
