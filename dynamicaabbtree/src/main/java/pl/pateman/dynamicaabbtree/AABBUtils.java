package pl.pateman.dynamicaabbtree;

import org.joml.AABBf;

/**
 * Created by pateman.
 */
public final class AABBUtils {

    private AABBUtils() {

    }

    public static float getMinDistance(AABBf aabba, AABBf aabbb) {
        return (float) Math.hypot(Math.hypot(
                minDistance(aabba.minX, aabba.maxX, aabbb.minX, aabbb.maxX),
                minDistance(aabba.minY, aabba.maxY, aabbb.minY, aabbb.maxY)),
                minDistance(aabba.minZ, aabba.maxZ, aabbb.minZ, aabbb.maxZ)
        );
    }

    static float minDistance(float minA, float maxA, float minB, float maxB) {
        return (float) Math.min(Math.max(0.0f, minA - maxB), Math.max(0.0f, minB - maxA));
    }

    public static float getWidth(AABBf aabb) {
        return aabb.maxX - aabb.minX;
    }

    public static float getHeight(AABBf aabb) {
        return aabb.maxY - aabb.minY;
    }

    public static float getDepth(AABBf aabb) {
        return aabb.maxZ - aabb.minZ;
    }

    public static float getArea(AABBf aabb) {
        final float width = getWidth(aabb);
        final float height = getHeight(aabb);
        final float depth = getDepth(aabb);
        return 2.0f * (width * height + width * depth + height * depth);
    }
}
