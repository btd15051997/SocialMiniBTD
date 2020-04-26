package com.example.socialminibtd.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialminibtd.R;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class Controller {

    public static Dialog mProgressDialog;

    public static ProgressDialog progressDialog;

    public static Animation anim_blink,anim_bounce;

    public static void showProgressDialog(Context context, String message) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    public static  Animation onShowAnimationBlink(Context context){

        anim_blink = AnimationUtils.loadAnimation(context,R.anim.slide_blink);

        return anim_blink;

    } public static  Animation onShowAnimationBounce(Context context){


        anim_bounce = AnimationUtils.loadAnimation(context,R.anim.slide_bounce);

        return anim_bounce;

    }

    public static void dimissProgressDialog() {

        if (progressDialog != null) {

            progressDialog.dismiss();
        }

    }

    public static String convertDateTime(String time) {

        Calendar cal =Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String dateTime = DateFormat.format("dd-MM-yyyy hh:mm aa",cal).toString();


        return dateTime;
    }



    public static void showSimpleProgressDialog(Context context, String msg, boolean isCancelable) {

        if (context != null) {

            mProgressDialog = new Dialog(context, R.style.DialogSlideAnim_leftright);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.animation_loading);
            TextView tv_title = (TextView) mProgressDialog.findViewById(R.id.tv_title);
            tv_title.setText(msg);
            mProgressDialog.show();
        }
    }

    public static void removeProgressDialog() {
        if (null != mProgressDialog)
            mProgressDialog.dismiss();
    }

    public static void appLogDebug(String key, String value) {
        Log.d(key, value);
    }

    public static void appLogInfo(String key, String value) {
        Log.i(key, value);
    }

    public static void appLogError(String key, String value) {
        Log.e(key, value);
    }

    public static void showLongToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void hideKeyBoard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // if (!imm.isAcceptingText())
        View view = activity.getCurrentFocus();

        if (view == null) {

            view = new View(activity);

        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static File getImage(String imagename) {

        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/Social/" + imagename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mediaImage;
    }

}
