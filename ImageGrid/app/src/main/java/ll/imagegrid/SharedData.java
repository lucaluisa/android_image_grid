package ll.imagegrid;

import java.util.ArrayList;

/**
 * Created by lucaluisa on 07/02/16.
 */
public class SharedData {
    private static final SharedData INSTANCE = new SharedData();

    public ArrayList<ImageItem> mImageItems = new ArrayList<>();

    // Private constructor prevents instantiation from other classes
    private SharedData() {}

    public static SharedData getInstance() {
        return INSTANCE;
    }

}
