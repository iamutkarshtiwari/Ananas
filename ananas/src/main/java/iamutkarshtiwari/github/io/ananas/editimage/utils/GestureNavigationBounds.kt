package iamutkarshtiwari.github.io.ananas.editimage.utils

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout

class GestureNavigationBounds(view: View) {
    private val exclusionRect: Rect = Rect()

    private val exclusionRects: List<Rect> by lazy {
        listOf(exclusionRect)
    }

    private fun exclusionRects(view: View): List<Rect> {
        updateExclusionRect(exclusionRect, view)
        return exclusionRects
    }

    init {
        view.doOnLayout {
            ViewCompat.setSystemGestureExclusionRects(it, exclusionRects(it))
        }
    }

    private fun updateExclusionRect(exclusionRect: Rect, view: View): Rect {
        exclusionRect.set(0, 0, view.width, view.height)
        return exclusionRect
    }
}
