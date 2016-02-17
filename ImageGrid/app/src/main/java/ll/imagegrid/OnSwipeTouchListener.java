package ll.imagegrid;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by lucaluisa on 06/02/16.
 */
public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    // this function will be override by the who attach this class
    // in order to be notified when there is a swipe
    public void onSwipeLeft() {
    }

    // this function will be override by the who attach this class
    // in order to be notified when there is a swipe
    public void onSwipeRight() {
    }

    // this function will be override by the who attach this class
    // in order to be notified when there is a swipe
    public void onSwipeDown() {
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    // Call the function that do the business, that can be override
                    onSwipeRight();
                }
                else {
                    // Call the function that do the business, that can be override
                    onSwipeLeft();
                }
                return true;
            }

            if (Math.abs(distanceX) < SWIPE_DISTANCE_THRESHOLD && Math.abs(distanceY)  > SWIPE_DISTANCE_THRESHOLD) {
                onSwipeDown();
            }
            return false;
        }
    }
}