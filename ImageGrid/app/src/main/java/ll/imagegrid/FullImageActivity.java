package ll.imagegrid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by lucaluisa on 03/02/16.
 */
//public class FullImageActivity extends Activity

public class FullImageActivity extends AppCompatActivity {
    private int mId;
    private String mAbsPath;
    android.support.v7.widget.Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // Get the image Id from the intent
        Intent i = getIntent();
        mId = i.getExtras().getInt("id");

        setToolbar();
        setImage();
        setTouch();

    }

    private void setToolbar() {
        // Tell activity to add a toolbar
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // aToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.toolbar_icon);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("Full Image");
    }

    private void setTouch() {
        // Set the Swipe manager attached to the current Image View
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                SharedData aSharedData = SharedData.getInstance();
                mId++;
                if (mId >= aSharedData.mImageItems.size()) {
                    mId = 0;
                }

                setImage();
            }

            @Override
            public void onSwipeRight() {
                SharedData aSharedData = SharedData.getInstance();
                mId--;
                if (mId <= 0) {
                    mId = aSharedData.mImageItems.size() - 1;
                }

                setImage();
            }

            @Override
            public void onSwipeDown() {
                close();
            }
        });
    }

    private void setImage() {
        SharedData aSharedData = SharedData.getInstance();
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setImageBitmap(aSharedData.mImageItems.get(mId).getImage());
        mAbsPath = aSharedData.mImageItems.get(mId).getAbsPath();
    }

    // Set result OK to the listener and finish the view
    private void close() {
        setResult(RESULT_OK);
        FullImageActivity.this.finish();
    }

    // Close the view as requested by external action like a button
    public void closeView(View v) {
        close();
    }

    // Delete the picture from the memory
    public void deleteImage(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_delete_image_title) //
                .setMessage(mAbsPath) //
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File aFile = new File(mAbsPath);
                        aFile.delete();
                        dialog.dismiss();
                        close();
                    }
                }) //
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share_pic:
                share();
                return true;
            case R.id.delete_image:
                deleteImage(item.getActionView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        File aPictureFile = new File(mAbsPath);;
        Uri screenshotUri = Uri.fromFile(aPictureFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_picture)));
    }

}
