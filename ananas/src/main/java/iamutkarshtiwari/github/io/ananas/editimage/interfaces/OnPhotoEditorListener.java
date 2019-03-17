package iamutkarshtiwari.github.io.ananas.editimage.interfaces;


public interface OnPhotoEditorListener {
    void onAddViewListener(int numberOfAddedViews);

    void onRemoveViewListener(int numberOfAddedViews);

    void onStartViewChangeListener();

    void onStopViewChangeListener();
}
