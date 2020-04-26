package com.example.socialminibtd.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.socialminibtd.R;
import com.example.socialminibtd.View.Activity.WellcomeActivity;

public class MessagerService extends Service {

    private WindowManager windowManager;
    private MyGroupView mViewIcon;
    private WindowManager.LayoutParams mIconViewParams;
    private GestureDetector gestureDetector;


/*  Bound Service trong Android
    Với loại service này thì bạn sẽ không gọi start Service bằng startService()
    , thay vào đó là sẽ gọi phương thức bindService().
    Một component(ví dụ như Activity chẳng hạn) gọi Service bằng phương thức bindService()
    . Activity sẽ liên kết với Service theo dạng client – service.
     Lúc này Activity có tương tác với Service để gửi và nhận kết quả từ Service*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    //Unbound Service trong Android
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        InitView();

        return START_STICKY;
    }

    private void InitView() {

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        createIconView();

        showIcon();

    }


    private void showIcon() {

        windowManager.addView(mViewIcon, mIconViewParams);
    }

    private void createIconView() {

        mViewIcon = new MyGroupView(this);

        View view = View.inflate(this, R.layout.view_icon, mViewIcon);

        TextView tvIcon = view.findViewById(R.id.txt_icon);


        mIconViewParams = new WindowManager.LayoutParams();
        mIconViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mIconViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mIconViewParams.gravity = Gravity.CENTER;
        mIconViewParams.format = PixelFormat.TRANSLUCENT;

        // Quan trọng nổi lên trên bề mặt
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            mIconViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {

            mIconViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }

        mIconViewParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mIconViewParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //Lắng nghe hành động khi click ra ngoài
        mIconViewParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        tvIcon.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;

            // Diem khi nhan xuong
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {

                    onIntentMain();

                    return true;

                } else {

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            //remember the initial position.
                            initialX = mIconViewParams.x;
                            initialY = mIconViewParams.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_UP:

                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);

                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (Xdiff < 10 && Ydiff < 10) {

                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                mViewIcon.setVisibility(View.VISIBLE);


                            }
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            mIconViewParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            mIconViewParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                            //Update the layout with new X & Y coordinate
                            windowManager.updateViewLayout(mViewIcon, mIconViewParams);
                            return true;

//                        case MotionEvent.ACTION_BUTTON_PRESS:
//
//                            //Open the application  click.
//                            Intent intent = new Intent(MessageService.this, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//
//                            //close the service and remove view from the view hierarchy
//                            stopSelf();
//
//                            try {
//
//                                windowManager.removeView(mViewIcon);
//
//                            } catch (Exception e) {
//
//
//                            }
//                            return true;


                    }

                }

                return false;
            }
        });


        tvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }


//    private boolean isViewCollapsed() {
//
//        return mViewIcon == null || mViewIcon.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
//    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {

            Toast.makeText(MessagerService.this, "One Tap press", Toast.LENGTH_SHORT).show();
            onIntentMain();

            return true;

        }

        @Override
        public void onLongPress(MotionEvent e) {

            Toast.makeText(MessagerService.this, "Long press", Toast.LENGTH_SHORT).show();
            onIntentMain();

            super.onLongPress(e);
        }
    }

    public void onIntentMain() {

        // single tap
        //Open the application  click.
        Intent intent = new Intent(this, WellcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // intent.putExtra("replace_messager", "replace_messager");
        startActivity(intent);

        //close the service and remove view from the view hierarchy
        stopSelf();

        try {

            windowManager.removeView(mViewIcon);

        } catch (Exception e) {


        }


    }

}

