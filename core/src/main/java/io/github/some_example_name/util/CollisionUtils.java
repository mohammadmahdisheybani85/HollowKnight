package io.github.some_example_name.util;

public final class CollisionUtils {
    private CollisionUtils() {
    }

    public static boolean overlaps(
        float firstX,
        float firstY,
        float firstWidth,
        float firstHeight,
        float secondX,
        float secondY,
        float secondWidth,
        float secondHeight
    ) {
        return firstX + firstWidth > secondX
            && firstX < secondX + secondWidth
            && firstY + firstHeight > secondY
            && firstY < secondY + secondHeight;
    }
}
