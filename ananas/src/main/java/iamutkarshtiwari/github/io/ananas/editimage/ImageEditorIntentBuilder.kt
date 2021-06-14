package iamutkarshtiwari.github.io.ananas.editimage

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri

class ImageEditorIntentBuilder @JvmOverloads constructor(private val context: Context,
                                                         private val sourcePath: String?,
                                                         private val outputPath: String?,
                                                         private val intent: Intent = Intent(
                                                                 context,
                                                                 EditImageActivity::class.java
                                                         )
) {
    private var sourceUri: Uri? = null

    @JvmOverloads constructor(context: Context,
                sourceUri: Uri,
                outputPath: String?,
                intent: Intent = Intent(
                        context,
                        EditImageActivity::class.java
                )) : this(context, null, outputPath, intent) {
        this.sourceUri = sourceUri
    }

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

    fun withSourceUri(sourceUri: Uri): ImageEditorIntentBuilder {
        this.sourceUri = sourceUri
        intent.putExtra(SOURCE_URI, sourceUri.toString())
        intent.removeExtra(SOURCE_PATH)
        return this
    }

    fun withSourcePath(sourcePath: String): ImageEditorIntentBuilder {
        intent.putExtra(SOURCE_PATH, sourcePath)
        intent.removeExtra(SOURCE_URI)
        return this
    }

    fun withOutputPath(outputPath: String): ImageEditorIntentBuilder {
        intent.putExtra(OUTPUT_PATH, outputPath)
        return this
    }

    fun withFonts(fonts: HashMap<String, Typeface>): ImageEditorIntentBuilder {
        EditImageActivity.fonts = fonts
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

        if (sourcePath.isNullOrBlank() && sourceUri == null) {
            throw Exception("Source image required. Use withSourcePath(path) or withSourceUri(uri) to provide the source.")
        } else if (!sourcePath.isNullOrBlank() && sourceUri != null) {
            throw Exception("Multiple source images specified. Use either withSourcePath(path) or withSourceUri(uri) to provide the source.")
        } else if (!sourcePath.isNullOrBlank()) {
            intent.putExtra(SOURCE_PATH, sourcePath)
        } else {
            intent.putExtra(SOURCE_URI, sourceUri.toString())
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

        const val SOURCE_URI = "source_uri"
        const val SOURCE_PATH = "source_path"
        const val OUTPUT_PATH = "output_path"
        const val FORCE_PORTRAIT = "force_portrait"
        const val EDITOR_TITLE = "editor_title"
        const val SUPPORT_ACTION_BAR_VISIBILITY = "support_action_bar_visibility"
    }
}
