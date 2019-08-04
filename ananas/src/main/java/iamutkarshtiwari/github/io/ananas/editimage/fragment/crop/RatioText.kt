package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop

import androidx.annotation.StringRes
import iamutkarshtiwari.github.io.ananas.R

enum class RatioText constructor(@StringRes val ratioTextId: Int, val aspectRatio: AspectRatio) {
    FREE(R.string.free_size, AspectRatio()),
    FIT_IMAGE(R.string.fit_image, AspectRatio(-1, -1)),
    SQUARE(R.string.square, AspectRatio(1, 1)),
    RATIO_3_4(R.string.ratio3_4, AspectRatio(3, 4)),
    RATIO_4_3(R.string.ratio4_3, AspectRatio(4, 3)),
    RATIO_9_16(R.string.ratio9_16, AspectRatio(9, 16)),
    RATIO_16_9(R.string.ratio16_9, AspectRatio(16, 9))
}

data class AspectRatio(
        val aspectX: Int = 0,
        val aspectY: Int = 0
)
