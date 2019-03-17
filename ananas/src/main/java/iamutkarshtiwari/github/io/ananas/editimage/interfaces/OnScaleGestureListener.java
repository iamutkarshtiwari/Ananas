package iamutkarshtiwari.github.io.ananas.editimage.interfaces;

import android.view.View;

import iamutkarshtiwari.github.io.ananas.editimage.gesture.ScaleGestureDetector;

/**
 * The listener for receiving notifications when gestures occur.
 * If you want to listen for all the different gestures then implement
 * this interface. If you only want to listen for a subset it might
 * be easier to extend {@link ScaleGestureDetector.SimpleOnScaleGestureListener}.
 * <p>
 * An application will receive events in the following order:
 */
public interface OnScaleGestureListener {
    /**
     * Responds to scaling events for a gesture in progress.
     * Reported by pointer motion.
     *
     * @param detector The detector reporting the event - use this to
     *                 retrieve extended info about event state.
     * @return Whether or not the detector should consider this event
     * as handled. If an event was not handled, the detector
     * will continue to accumulate movement until an event is
     * handled. This can be useful if an application, for example,
     * only wants to update scaling factors if the change is
     * greater than 0.01.
     */
    boolean onScale(View view, ScaleGestureDetector detector);

    /**
     * Responds to the beginning of a scaling gesture. Reported by
     * new pointers going down.
     *
     * @param detector The detector reporting the event - use this to
     *                 retrieve extended info about event state.
     * @return Whether or not the detector should continue recognizing
     * this gesture. For example, if a gesture is beginning
     * with a focal point outside of a region where it makes
     * sense, onScaleBegin() may return false to ignore the
     * rest of the gesture.
     */
    boolean onScaleBegin(View view, ScaleGestureDetector detector);

    /**
     * Responds to the end of a scale gesture. Reported by existing
     * pointers going up.
     * <p>
     * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
     * and {@link ScaleGestureDetector#getFocusY()} will return the location
     * of the pointer remaining on the screen.
     *
     * @param detector The detector reporting the event - use this to
     *                 retrieve extended info about event state.
     */
    void onScaleEnd(View view, ScaleGestureDetector detector);
}
