package iamutkarshtiwari.github.io.ananas.editimage.gesture;

import android.view.MotionEvent;
import android.view.View;

import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnScaleGestureListener;

/**
 * Detects transformation gestures involving more than one pointer ("multitouch")
 * using the supplied {@link MotionEvent}s. The {@link OnScaleGestureListener}
 * callback will notify users when a particular gesture event has occurred.
 * This class should only be used with {@link MotionEvent}s reported via touch.
 * <p>
 * To use this class:
 * <ul>
 * <li>Create an instance of the {@code ScaleGestureDetector} for your
 * {@link View}
 * </ul>
 */
public class ScaleGestureDetector {

    /**
     * This value is the threshold ratio between our previous combined pressure
     * and the current combined pressure. We will only fire an onScale event if
     * the computed ratio between the current and previous event pressures is
     * greater than this value. When pressure decreases rapidly between events
     * the position values can often be imprecise, as it usually indicates
     * that the user is in the process of lifting a pointer off of the device.
     * Its value was tuned experimentally.
     */
    private static final float PRESSURE_THRESHOLD = 0.67f;

    private final OnScaleGestureListener listener;
    private boolean gestureInProgress;

    private MotionEvent prevEvent;
    private MotionEvent currEvent;

    private Vector2D currSpanVector;
    private float focusX;
    private float focusY;
    private float prevFingerDiffX;
    private float prevFingerDiffY;
    private float currFingerDiffX;
    private float currFingerDiffY;
    private float currLen;
    private float prevLen;
    private float scaleFactor;
    private float currPressure;
    private float prevPressure;
    private long timeDelta;

    private boolean invalidGesture;

    // Pointer IDs currently responsible for the two fingers controlling the gesture
    private int activeId0;
    private int activeId1;
    private boolean active0MostRecent;

    ScaleGestureDetector(OnScaleGestureListener listener) {
        this.listener = listener;
        currSpanVector = new Vector2D();
    }

    boolean onTouchEvent(View view, MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            reset(); // Start fresh
        }

        boolean handled = true;
        if (invalidGesture) {
            handled = false;
        } else if (!gestureInProgress) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    activeId0 = event.getPointerId(0);
                    active0MostRecent = true;
                }
                break;

                case MotionEvent.ACTION_UP:
                    reset();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: {
                    // We have a new multi-finger gesture
                    if (prevEvent != null) prevEvent.recycle();
                    prevEvent = MotionEvent.obtain(event);
                    timeDelta = 0;

                    int index1 = event.getActionIndex();
                    int index0 = event.findPointerIndex(activeId0);
                    activeId1 = event.getPointerId(index1);
                    if (index0 < 0 || index0 == index1) {
                        // Probably someone sending us a broken event stream.
                        index0 = findNewActiveIndex(event, activeId1, -1);
                        activeId0 = event.getPointerId(index0);
                    }
                    active0MostRecent = false;

                    setContext(view, event);

                    gestureInProgress = listener.onScaleBegin(view, this);
                    break;
                }
            }
        } else {
            // Transform gesture in progress - attempt to handle it
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN: {
                    // End the old gesture and begin a new one with the most recent two fingers.
                    listener.onScaleEnd(view, this);
                    final int oldActive0 = activeId0;
                    final int oldActive1 = activeId1;
                    reset();

                    prevEvent = MotionEvent.obtain(event);
                    activeId0 = active0MostRecent ? oldActive0 : oldActive1;
                    activeId1 = event.getPointerId(event.getActionIndex());
                    active0MostRecent = false;

                    int index0 = event.findPointerIndex(activeId0);
                    if (index0 < 0 || activeId0 == activeId1) {
                        // Probably someone sending us a broken event stream.
                        index0 = findNewActiveIndex(event, activeId1, -1);
                        activeId0 = event.getPointerId(index0);
                    }

                    setContext(view, event);

                    gestureInProgress = listener.onScaleBegin(view, this);
                }
                break;

                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerCount = event.getPointerCount();
                    final int actionIndex = event.getActionIndex();
                    final int actionId = event.getPointerId(actionIndex);

                    boolean gestureEnded = false;
                    if (pointerCount > 2) {
                        if (actionId == activeId0) {
                            final int newIndex = findNewActiveIndex(event, activeId1, actionIndex);
                            if (newIndex >= 0) {
                                listener.onScaleEnd(view, this);
                                activeId0 = event.getPointerId(newIndex);
                                active0MostRecent = true;
                                prevEvent = MotionEvent.obtain(event);
                                setContext(view, event);
                                gestureInProgress = listener.onScaleBegin(view, this);
                            } else {
                                gestureEnded = true;
                            }
                        } else if (actionId == activeId1) {
                            final int newIndex = findNewActiveIndex(event, activeId0, actionIndex);
                            if (newIndex >= 0) {
                                listener.onScaleEnd(view, this);
                                activeId1 = event.getPointerId(newIndex);
                                active0MostRecent = false;
                                prevEvent = MotionEvent.obtain(event);
                                setContext(view, event);
                                gestureInProgress = listener.onScaleBegin(view, this);
                            } else {
                                gestureEnded = true;
                            }
                        }
                        prevEvent.recycle();
                        prevEvent = MotionEvent.obtain(event);
                        setContext(view, event);
                    } else {
                        gestureEnded = true;
                    }

                    if (gestureEnded) {
                        // Gesture ended
                        setContext(view, event);

                        // Set focus point to the remaining finger
                        final int activeId = actionId == activeId0 ? activeId1 : activeId0;
                        final int index = event.findPointerIndex(activeId);
                        focusX = event.getX(index);
                        focusY = event.getY(index);

                        listener.onScaleEnd(view, this);
                        reset();
                        activeId0 = activeId;
                        active0MostRecent = true;
                    }
                }
                break;

                case MotionEvent.ACTION_CANCEL:
                    listener.onScaleEnd(view, this);
                    reset();
                    break;

                case MotionEvent.ACTION_UP:
                    reset();
                    break;

                case MotionEvent.ACTION_MOVE: {
                    setContext(view, event);

                    // Only accept the event if our relative pressure is within
                    // a certain limit - this can help filter shaky data as a
                    // finger is lifted.
                    if (currPressure / prevPressure > PRESSURE_THRESHOLD) {
                        final boolean updatePrevious = listener.onScale(view, this);

                        if (updatePrevious) {
                            prevEvent.recycle();
                            prevEvent = MotionEvent.obtain(event);
                        }
                    }
                }
                break;
            }
        }
        return handled;
    }

    private int findNewActiveIndex(MotionEvent ev, int otherActiveId, int removedPointerIndex) {
        final int pointerCount = ev.getPointerCount();

        // It's ok if this isn't found and returns -1, it simply won't match.
        final int otherActiveIndex = ev.findPointerIndex(otherActiveId);

        // Pick a new id and update tracking state.
        for (int i = 0; i < pointerCount; i++) {
            if (i != removedPointerIndex && i != otherActiveIndex) {
                return i;
            }
        }
        return -1;
    }

    private void setContext(View view, MotionEvent curr) {
        if (currEvent != null) {
            currEvent.recycle();
        }
        currEvent = MotionEvent.obtain(curr);

        currLen = -1;
        prevLen = -1;
        scaleFactor = -1;
        currSpanVector.set(0.0f, 0.0f);

        final MotionEvent prev = prevEvent;

        final int prevIndex0 = prev.findPointerIndex(activeId0);
        final int prevIndex1 = prev.findPointerIndex(activeId1);
        final int currIndex0 = curr.findPointerIndex(activeId0);
        final int currIndex1 = curr.findPointerIndex(activeId1);

        if (prevIndex0 < 0 || prevIndex1 < 0 || currIndex0 < 0 || currIndex1 < 0) {
            invalidGesture = true;
            if (gestureInProgress) {
                listener.onScaleEnd(view, this);
            }
            return;
        }

        final float px0 = prev.getX(prevIndex0);
        final float py0 = prev.getY(prevIndex0);
        final float px1 = prev.getX(prevIndex1);
        final float py1 = prev.getY(prevIndex1);
        final float cx0 = curr.getX(currIndex0);
        final float cy0 = curr.getY(currIndex0);
        final float cx1 = curr.getX(currIndex1);
        final float cy1 = curr.getY(currIndex1);

        final float pvx = px1 - px0;
        final float pvy = py1 - py0;
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;

        currSpanVector.set(cvx, cvy);

        prevFingerDiffX = pvx;
        prevFingerDiffY = pvy;
        currFingerDiffX = cvx;
        currFingerDiffY = cvy;

        focusX = cx0 + cvx * 0.5f;
        focusY = cy0 + cvy * 0.5f;
        timeDelta = curr.getEventTime() - prev.getEventTime();
        currPressure = curr.getPressure(currIndex0) + curr.getPressure(currIndex1);
        prevPressure = prev.getPressure(prevIndex0) + prev.getPressure(prevIndex1);
    }

    private void reset() {
        if (prevEvent != null) {
            prevEvent.recycle();
            prevEvent = null;
        }
        if (currEvent != null) {
            currEvent.recycle();
            currEvent = null;
        }
        gestureInProgress = false;
        activeId0 = -1;
        activeId1 = -1;
        invalidGesture = false;
    }

    /**
     * Returns {@code true} if a two-finger scale gesture is in progress.
     *
     * @return {@code true} if a scale gesture is in progress, {@code false} otherwise.
     */
    boolean isInProgress() {
        return gestureInProgress;
    }

    /**
     * Get the X coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is directly between
     * the two pointers forming the gesture.
     * If a gesture is ending, the focal point is the location of the
     * remaining pointer on the screen.
     * If {@link #isInProgress()} would return false, the result of this
     * function is undefined.
     *
     * @return X coordinate of the focal point in pixels.
     */
    float getFocusX() {
        return focusX;
    }

    /**
     * Get the Y coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is directly between
     * the two pointers forming the gesture.
     * If a gesture is ending, the focal point is the location of the
     * remaining pointer on the screen.
     * If {@link #isInProgress()} would return false, the result of this
     * function is undefined.
     *
     * @return Y coordinate of the focal point in pixels.
     */
    float getFocusY() {
        return focusY;
    }

    /**
     * Return the current distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    private float getCurrentSpan() {
        if (currLen == -1) {
            final float cvx = currFingerDiffX;
            final float cvy = currFingerDiffY;
            currLen = (float) Math.sqrt(cvx * cvx + cvy * cvy);
        }
        return currLen;
    }

    Vector2D getCurrentSpanVector() {
        return currSpanVector;
    }

    /**
     * Return the current x distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    public float getCurrentSpanX() {
        return currFingerDiffX;
    }

    /**
     * Return the current y distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    public float getCurrentSpanY() {
        return currFingerDiffY;
    }

    /**
     * Return the previous distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    private float getPreviousSpan() {
        if (prevLen == -1) {
            final float pvx = prevFingerDiffX;
            final float pvy = prevFingerDiffY;
            prevLen = (float) Math.sqrt(pvx * pvx + pvy * pvy);
        }
        return prevLen;
    }

    /**
     * Return the previous x distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    public float getPreviousSpanX() {
        return prevFingerDiffX;
    }

    /**
     * Return the previous y distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    public float getPreviousSpanY() {
        return prevFingerDiffY;
    }

    /**
     * Return the scaling factor from the previous scale event to the current
     * event. This value is defined as
     * ({@link #getCurrentSpan()} / {@link #getPreviousSpan()}).
     *
     * @return The current scaling factor.
     */
    public float getScaleFactor() {
        if (scaleFactor == -1) {
            scaleFactor = getCurrentSpan() / getPreviousSpan();
        }
        return scaleFactor;
    }

    /**
     * Return the time difference in milliseconds between the previous
     * accepted scaling event and the current scaling event.
     *
     * @return Time difference since the last scaling event in milliseconds.
     */
    public long getTimeDelta() {
        return timeDelta;
    }

    /**
     * Return the event time of the current event being processed.
     *
     * @return Current event time in milliseconds.
     */
    public long getEventTime() {
        return currEvent.getEventTime();
    }
}
