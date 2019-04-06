package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;

import java.util.concurrent.TimeUnit;

import androidx.annotation.FloatRange;
import androidx.appcompat.widget.AppCompatImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class BrightnessView extends AppCompatImageView {

    private float bright;
    private PublishSubject<Float> subject;

    public BrightnessView(Context context) {
        super(context);
        initView();
    }

    public BrightnessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BrightnessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        subject = PublishSubject.create();
        subject.debounce(0, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap(new Function<Float, ObservableSource<ColorMatrixColorFilter>>() {
                    @Override
                    public ObservableSource<ColorMatrixColorFilter> apply(Float value) throws Exception {
                        return postBrightness(value);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ColorMatrixColorFilter>() {
                    @Override
                    public void accept(ColorMatrixColorFilter colorMatrixColorFilter) throws Exception {
                        setColorFilter(colorMatrixColorFilter);
                    }
                });
    }

    public float getBright() {
        return bright;
    }

    public void setBright(@FloatRange(from = -100, to = 100) float bright) {
        this.bright = bright;
        subject.onNext(this.bright);
    }

    private Observable<ColorMatrixColorFilter> postBrightness(float value) {
        return Observable.just(brightness(value));
    }

    private ColorMatrixColorFilter brightness(float value) {
        ColorMatrix cmB = new ColorMatrix();
        cmB.set(new float[]{
                1, 0, 0, 0, value,
                0, 1, 0, 0, value,
                0, 0, 1, 0, value,
                0, 0, 0, 1, 0});
        return new ColorMatrixColorFilter(cmB);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
