package iamutkarshtiwari.github.io.ananas.editimage.gesture;

public class VectorAngle {

    public static float getAngle(Vector2D vector1, Vector2D vector2) {
        vector1.normalize();
        vector2.normalize();
        double degrees = (180.0 / Math.PI) * (Math.atan2(vector2.y, vector2.x) - Math.atan2(vector1.y, vector1.x));
        return (float) degrees;
    }
}
