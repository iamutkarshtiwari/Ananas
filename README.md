<img align="left" src="art/ananas.png" width="100" height="100" />

# Ananas Photo Editor

[![Download](https://img.shields.io/badge/JitPack-1.2.6-blue.svg)](https://jitpack.io/#iamutkarshtiwari/Ananas/1.2.6) ![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg)

An easy photo editor integration for your Android apps.

## Features

- [**Paint**](#paint) option with Brush Color, Size and Eraser.
- Adding/Editing [**Text**](#text) with option to change its color.
- Adding [**Stickers**](#adding-imagesstickers)
- Pinch to [**Zoom**](), [**Rotate**]() and [**Crop**]() views.
- [**Undo and Redo**](#undo-and-redo) for all changes.
- [**Saving**](#saving) Photo after editing.
- Applying [**Filters**]() to your image
- Changing the [**Contrast**]() and [**Saturation**]() of images
- Addition [**Beauty**]() settings for images with face


## Benefits
- Plug and play
- Easy image editing

<br>

## Previews

Main Menu | Text Mode
:--: | :--:
<img src="/static/main_menu.gif" width="300" /> | <img src="/static/text_mode.gif" width="300" />

Rotate Mode | Crop Mode
:--: | :--:
<img src="/static/rotate_mode.gif" width="300" /> | <img src="/static/crop_feature.gif" width="300" />

Filter Mode |  Paint Mode
:--: | :--:
<img src="/static/filter_mode.gif" width="300" /> | <img src="/static/paint_mode.gif" width="300" />

Beauty Mode | Saturation Mode
:--: | :--:
<img src="/static/beauty_mode.gif" width="300" /> | <img src="/static/saturation_mode.gif" width="300" />

Brightness Mode | Sticker Mode
:--: | :--:
<img src="/static/brightness_mode.gif" width="300" /> | <img src="/static/sticker_mode.gif" width="300" />

<br>

## Getting Started
Add it in your root build.gradle at the end of repositories:
```
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
  }
```


Add the dependency in gradle file of app module like this
```
implementation 'com.github.iamutkarshtiwari:Ananas:1.2.6'
```

## [Important!]

Add this to your app's `proguard-rules.pro` file:

```pro
-keepclasseswithmembers class * {
    native <methods>;
}
```

And this to your app's `build.gradle`:

```
compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
}
```

## NOTE:

Since this library uses `RxJava 2.0` and if your project uses `RxJava 1.0`, then you need to add the below code to the gradle file of you app so that both versions can co-exist-
```
android {
    packagingOptions {
        exclude 'META-INF/rxjava.properties'
    }
}
```

## Starting the PhotoEditor activity

Add this constant to your activity class with your preferred request code:
```java
private final int PHOTO_EDITOR_REQUEST_CODE = 231;// Any integer value as a request code.
```

Use the following code to build and launch the photo editor:
```java

 try {
  Intent intent = new ImageEditorIntentBuilder(this, sourceImagePath, outputFilePath)
         .withAddText() // Add the features you need
         .withPaintFeature()
         .withFilterFeature()
         .withRotateFeature()
         .withCropFeature()
         .withBrightnessFeature()
         .withSaturationFeature()
         .withBeautyFeature()
         .withStickerFeature()
         .forcePortrait(true)  // Add this to force portrait mode (It's set to false by default)
         .setSupportActionBarVisibility(false) // To hide app's default action bar
         .build();

 EditImageActivity.start(activity, intent, PHOTO_EDITOR_REQUEST_CODE);
 } catch (Exception e) {
     Log.e("Demo App", e.getMessage()); // This could throw if either `sourcePath` or `outputPath` is blank or Null
 }
```

## Receiving the output image

You can receive the new processed image path and it's edit status like this-
```
 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_EDITOR_REQUEST_CODE) { // same code you used while starting
            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);
        }
    }
```

## Special Note
The photo editor locks the current orientation in which it is started -
1) If you started in `Portrait` mode, you can't switch to `Landscape` while the image editor activity is running.
2) If you started in `Landscape` mode, you can't switch to `Portrait` during the same.

But once you navigate back to your original app, you are reverted back to your previous configuration change settings.

## How to contribute?
* Fork the project.
* Make required changes and commit.
* Generate pull request. Mention all the required description regarding changes you made.

Happy coding! :)


## What's next?
- Add support for configuration change during photo editing


## Questions?🤔
Hit me on twitter
[![Twitter](https://img.shields.io/badge/Twitter-%40iamutkarsht-blue.svg)](https://twitter.com/iamutkarsht)
[![Facebook](https://img.shields.io/badge/Facebook-Utkarsh%20Tiwari-blue.svg)](https://www.facebook.com/iamutkarshtiwari)

## How to submit a valid issue
- **Make sure you compiled the latest version.** If it still doesn't work out, don't hesitate to open a new issue.
- Describe the scenarios when crash happened as much as possible (pictures would be better).
- Sharing your device type and Android OS version is very helpful.
- Paste your XML or Java code.
- Paste the crash log.
- Be polite.


## Credits

 Name | Library
------------ | -------------
siwangqishiq | [ImageEditor Android](https://github.com/siwangqishiq/ImageEditor-Android)
ArthurHub | [Android Image Cropper](https://github.com/ArthurHub/Android-Image-Cropper)
hoanganhtuan95ptit | [Contrast and Brightness feature](https://github.com/hoanganhtuan95ptit/EditPhoto)
eltos | [Color Picker Dialog](https://github.com/eltos/SimpleDialogFragments)
Russell Jurney | [Kelly's 22 colors list](https://medium.com/@rjurney/kellys-22-colours-of-maximum-contrast-58edb70c90d1)
burhanrashid52 | [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor)
