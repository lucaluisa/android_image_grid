package ll.imagegrid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lucaluisa on 31/01/16.
 */
public class MainActivity extends ActionBarActivity {
    // TODO: move constants to integer resources
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSION_EXTERNAL_STORAGE_WRITE = 2;
    static final int REQUEST_PERMISSION_EXTERNAL_STORAGE_READ = 3;
    static final int UPDATE_ACTIVITY  = 4;
    static final int PICURE_SUB_SAMPLES = 10;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private String mCurrentPhotoPath;
    private boolean mWriteStorageGranted = false;
    private boolean mReadStorageGranted = false;
    private String LOG_TAG = "MainActivity";
    static final String[] PICTURE_EXTENSIONS = new String[]{"gif", "png", "bmp", "jpg"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        mGridView.setAdapter(mGridAdapter);

        /**
         * On Click event for Single Gridview Item
         * */
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending clicked image to FullScreenActivity
                Intent aFullPicIntent = new Intent(getApplicationContext(), FullImageActivity.class);
                GridView aParentGrid = (GridView)parent;
                ImageItem aImageItemClicked = (ImageItem) aParentGrid.getAdapter().getItem(position);

                // Passing parameters to callback
                aFullPicIntent.putExtra("id", position);
                startActivityForResult(aFullPicIntent, UPDATE_ACTIVITY);
            }
        });
    }

    private void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                // Error occurred while creating the File
                Log.e(LOG_TAG, ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void buttonOnClick(View v) {
        Button aBB = (Button)v;
        aBB.setText("Take another picture");

        checkAndRequestPermissionExternalStorageWriting();
        takePicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Read the picture just stored in external storage as sampled bitmap
            BitmapFactory.Options aOptions = new BitmapFactory.Options();
            aOptions.inSampleSize = PICURE_SUB_SAMPLES;
            Bitmap aBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, aOptions);

            SharedData aSharedData = SharedData.getInstance();
            aSharedData.mImageItems.add(new ImageItem(aBitmap, "Image#" + aSharedData.mImageItems.size(), mCurrentPhotoPath));

            // Trigger the refresh of the Gridview
            mGridAdapter.notifyDataSetChanged();
            mGridView.setAdapter(mGridAdapter);
        }
        else if (requestCode == UPDATE_ACTIVITY && resultCode == RESULT_OK) {
            getData();
            mGridAdapter.notifyDataSetChanged();
            mGridView.setAdapter(mGridAdapter);
        }
    }


    private void checkAndRequestPermissionExternalStorageWriting()
    {
        // http://developer.android.com/training/permissions/requesting.html
        mWriteStorageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED;
        
        if (!mWriteStorageGranted)
        {
            //The callback method gets the result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_EXTERNAL_STORAGE_WRITE);
        }
    }

    private void checkAndRequestPermissionExternalStorageReading()
    {
        // http://developer.android.com/training/permissions/requesting.html
        mReadStorageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED;

        if (!mReadStorageGranted)
        {
            //The callback method gets the result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_EXTERNAL_STORAGE_READ);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_EXTERNAL_STORAGE_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     mWriteStorageGranted = true;

                    // TODO: is this needed again here?
                    takePicture();
                } else {
                    mWriteStorageGranted = false;
                }
                return;
            }
            case REQUEST_PERMISSION_EXTERNAL_STORAGE_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mReadStorageGranted = true;
                    takePicture();
                } else {
                    mReadStorageGranted = false;
                }
                return;
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = null;

        if (mWriteStorageGranted)
        {
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            mCurrentPhotoPath = image.getAbsolutePath();
        }
        else
        {
            Log.e(LOG_TAG, "External storage does not have writing permission");
        }
        return image;
    }


    private ArrayList<ImageItem> getData()
    {
        checkAndRequestPermissionExternalStorageReading();
        SharedData aSharedData = SharedData.getInstance();

        if (mReadStorageGranted)
        {
            aSharedData.mImageItems.clear();
            BitmapFactory.Options aOptions = new BitmapFactory.Options();
            aOptions.inSampleSize = PICURE_SUB_SAMPLES;

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File[] sdDirFiles = storageDir.listFiles();

            // TODO: improve these loops with maybe some filter on file type
            for(File aFile : sdDirFiles)
            {
                if (!aFile.isDirectory())
                {
                    for (String aEx : PICTURE_EXTENSIONS) {
                        if (aFile.getName().toLowerCase().endsWith(aEx)) {
                            // Read each picture ina small size
                            Bitmap aBitmap = BitmapFactory.decodeFile(aFile.getAbsolutePath(), aOptions);
                            aSharedData.mImageItems.add(new ImageItem(aBitmap, "Image#" + aSharedData.mImageItems.size(), aFile.getAbsolutePath()));
                        }
                    }
                }
            }
        }
        return aSharedData.mImageItems;
    }

}