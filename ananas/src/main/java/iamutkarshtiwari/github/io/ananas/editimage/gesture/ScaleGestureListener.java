package iamutkarshtiwari.github.io.ananas.editimage.gesture;

import android.view.View;

import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnScaleGestureListener;

public class ScaleGestureListener implements OnScaleGestureListener {

    private MultiTouchListener multiTouchListener;

    ScaleGestureListener(MultiTouchListener multiTouchListener) {
        this.multiTouchListener = multiTouchListener;
    }

    private float pivotX;
    private float pivotY;
    private Vector2D prevSpanVector = new Vector2D();

    @Override
    public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
        pivotX = detector.getFocusX();
        pivotY = detector.getFocusY();
        prevSpanVector.set(detector.getCurrentSpanVector());
        return multiTouchListener.isTextPinchZoomable;
    }

    @Override
    public void onScaleEnd(View view, ScaleGestureDetector detector) {

    }

    @Override
    public boolean onScale(View view, ScaleGestureDetector detector) {
        TransformInfo info = new TransformInfo();
        info.deltaScale = multiTouchListener.isScaleEnabled ? detector.getScaleFactor() : 1.0f;
        info.deltaAngle = multiTouchListener.isRotateEnabled ? VectorAngle.getAngle(prevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
        info.deltaX = multiTouchListener.isTranslateEnabled ? detector.getFocusX() - pivotX : 0.0f;
        info.deltaY = multiTouchListener.isTranslateEnabled ? detector.getFocusY() - pivotY : 0.0f;
        info.pivotX = pivotX;
        info.pivotY = pivotY;
        info.minimumScale = multiTouchListener.minimumScale;
        info.maximumScale = multiTouchListener.maximumScale;
        multiTouchListener.move(view, info);
        return !multiTouchListener.isTextPinchZoomable;
    }
}
