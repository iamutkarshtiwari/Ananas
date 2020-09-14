package iamutkarshtiwari.github.io.ananas.editimage

import android.content.Context
import android.content.Intent

class ImageEditorIntentBuilder @JvmOverloads constructor(private val context: Context,
                                                         private val sourcePath: String?,
                                                         private val outputPath: String?,
                                                         private val intent: Intent = Intent(
                                                                 context,
                                                                 EditImageActivity::class.java
                                                         )
) {

    fun withAddText(): ImageEditorIntentBuilder {
        intent.putExtra(ADD_TEXT_FEATURE, true)
        return this
    }

    fun withPaintFeature(): ImageEditorIntentBuilder {
        intent.putExtra(PAINT_FEATURE, true)
        return this
    }

    fun withFilterFeature(): ImageEditorIntentBuilder {
        intent.putExtra(FILTER_FEATURE, true)
        return this
    }

    fun withRotateFeature(): ImageEditorIntentBuilder {
        intent.putExtra(ROTATE_FEATURE, true)
        return this
    }

    fun withCropFeature(): ImageEditorIntentBuilder {
        intent.putExtra(CROP_FEATURE, true)
        return this
    }

    fun withBrightnessFeature(): ImageEditorIntentBuilder {
        intent.putExtra(BRIGHTNESS_FEATURE, true)
        return this
    }

    fun withSaturationFeature(): ImageEditorIntentBuilder {
        intent.putExtra(SATURATION_FEATURE, true)
        return this
    }

    fun withBeautyFeature(): ImageEditorIntentBuilder {
        intent.putExtra(BEAUTY_FEATURE, true)
        return this
    }

    fun withStickerFeature(): ImageEditorIntentBuilder {
        intent.putExtra(STICKER_FEATURE, true)
        return this
    }

    fun withEditorTitle(title:String): ImageEditorIntentBuilder {
        intent.putExtra(EDITOR_TITLE, title)
        return this
    }

    fun withSourcePath(sourcePath: String): ImageEditorIntentBuilder {
        intent.putExtra(SOURCE_PATH, sourcePath)
        return this
    }

    fun withOutputPath(outputPath: String): ImageEditorIntentBuilder {
        intent.putExtra(OUTPUT_PATH, outputPath)
        return this
    }

    fun forcePortrait(isForcePortrait: Boolean): ImageEditorIntentBuilder {
        intent.putExtra(FORCE_PORTRAIT, isForcePortrait)
        return this
    }

    fun setSupportActionBarVisibility(isVisible: Boolean): ImageEditorIntentBuilder {
        intent.putExtra(SUPPORT_ACTION_BAR_VISIBILITY, isVisible)
        return this
    }

    @Throws(Exception::class)
    fun build(): Intent {

        if (sourcePath.isNullOrBlank()) {
            throw Exception("Output image path required. Use withOutputPath(path) to provide the output image path.")
        } else {
            intent.putExtra(SOURCE_PATH, sourcePath)
        }

        if (outputPath.isNullOrBlank()) {
            throw Exception("Output image path required. Use withOutputPath(path) to provide the output image path.")
        } else {
            intent.putExtra(OUTPUT_PATH, outputPath)
        }

        return intent
    }

    companion object {
        const val ADD_TEXT_FEATURE = "add_text_feature"
        const val PAINT_FEATURE = "paint_feature"
        const val FILTER_FEATURE = "filter_feature"
        const val ROTATE_FEATURE = "rotate_feature"
        const val CROP_FEATURE = "crop_feature"
        const val BRIGHTNESS_FEATURE = "brightness_feature"
        const val SATURATION_FEATURE = "saturation_feature"
        const val BEAUTY_FEATURE = "beauty_feature"
        const val STICKER_FEATURE = "sticker_feature"

        const val SOURCE_PATH = "source_path"
        const val OUTPUT_PATH = "output_path"
        const val FORCE_PORTRAIT = "force_portrait"
        const val EDITOR_TITLE = "editor_title"
        const val SUPPORT_ACTION_BAR_VISIBILITY = "support_action_bar_visibility"
    }
}
