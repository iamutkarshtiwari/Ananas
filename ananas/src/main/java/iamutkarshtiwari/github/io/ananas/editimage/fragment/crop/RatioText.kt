package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop

import androidx.annotation.StringRes
import iamutkarshtiwari.github.io.ananas.R

enum class RatioText constructor(@StringRes val ratioTextId: Int, val aspectRatio: AspectRatio) {
    FREE(R.string.iamutkarshtiwari_github_io_ananas_free_size, AspectRatio()),
    FIT_IMAGE(R.string.iamutkarshtiwari_github_io_ananas_fit_image, AspectRatio(-1, -1)),
    SQUARE(R.string.iamutkarshtiwari_github_io_ananas_square, AspectRatio(1, 1)),
    RATIO_3_4(R.string.iamutkarshtiwari_github_io_ananas_ratio3_4, AspectRatio(3, 4)),
    RATIO_4_3(R.string.iamutkarshtiwari_github_io_ananas_ratio4_3, AspectRatio(4, 3)),
    RATIO_9_16(R.string.iamutkarshtiwari_github_io_ananas_ratio9_16, AspectRatio(9, 16)),
    RATIO_16_9(R.string.iamutkarshtiwari_github_io_ananas_ratio16_9, AspectRatio(16, 9))
}

data class AspectRatio(
        val aspectX: Int = 0,
        val aspectY: Int = 0
)
