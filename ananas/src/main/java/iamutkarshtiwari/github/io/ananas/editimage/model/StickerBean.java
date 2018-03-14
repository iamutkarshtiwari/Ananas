package iamutkarshtiwari.github.io.ananas.editimage.model;

import java.util.ArrayList;
import java.util.List;

public class StickerBean {
    private String coverPath;
    private List<String> pathList;

    public StickerBean() {
        pathList = new ArrayList<String>();
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }
}
