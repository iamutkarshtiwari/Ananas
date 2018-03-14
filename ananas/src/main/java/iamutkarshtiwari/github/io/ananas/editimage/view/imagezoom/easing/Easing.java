package iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.easing;

public interface Easing {

	double easeOut(double time, double start, double end, double duration);

	double easeIn(double time, double start, double end, double duration);

	double easeInOut(double time, double start, double end, double duration);
}
