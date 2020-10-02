package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop;

public class AspectRatio implements java.io.Serializable {
    private final String name;
    private final int x;
    private final int y;

    public AspectRatio(String name) {
        this(name, 0, 0);
    }

    public AspectRatio(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFree() {
        return x == 0 && y == 0;
    }

    public boolean isFitImage() {
        return x == -1 && y == -1;
    }
}
