package ll.imagegrid;

import android.graphics.Bitmap;

/**
 * Created by lucaluisa on 31/01/16.
 */
public class ImageItem {
    private Bitmap mImage;
    private String mTitle;
    private String mAbsPath;

    public ImageItem(Bitmap iImage, String iTitle, String iPath) {
        super();
        this.mImage = iImage;
        this.mTitle = iTitle;
        this.mAbsPath = iPath;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public  String getAbsPath() {
        return mAbsPath;
    }

    public void setImage(Bitmap image) {
        this.mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
