package com.example.socialminibtd.View.Dialog.SettingsDialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.PreferenceHelper;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsDialog extends DialogFragment {

    private DashboardActivity dashboardActivity;

    private FirebaseAuth mFirebaseAuth;
    private SwitchCompat switch_compat;
    private RelativeLayout content_setting;


    private static final String TOPIC_POST_NOTIFICATION = "POST"; // assign any value but user same for  this kind of notifications

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = (DashboardActivity) getActivity();

        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog_setting = new Dialog(dashboardActivity, R.style.DialogThemeforview);

        dialog_setting.setContentView(R.layout.dialog_settings);

        switch_compat = dialog_setting.findViewById(R.id.switch_compat);
        content_setting = dialog_setting.findViewById(R.id.content_setting);

        boolean isPostEnable = new PreferenceHelper(dashboardActivity).getNotification_SP();

        Controller.appLogDebug(Const.LOG_DAT, " SettingsDialog " + String.valueOf(isPostEnable));

        if (isPostEnable == true) {

            switch_compat.setChecked(true);

        } else {

            switch_compat.setChecked(false);

        }


        switch_compat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                new PreferenceHelper(dashboardActivity).putNotification_SP(isChecked);

                Controller.appLogDebug(Const.LOG_DAT, " SettingsDialog " + String.valueOf(isChecked));

                if (isChecked) {

                    onSubscribePostNotification();

                } else {

                    onUnsubscribePostNotification();

                }

            }
        });

        return dialog_setting;
    }

    private void onUnsubscribePostNotification() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_POST_NOTIFICATION + "")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "You will not receive post notifications";

                        if (!task.isSuccessful()) {

                            msg = "UnSubscription failed";

                        }

                        Controller.showLongToast(msg, dashboardActivity);

                    }
                });


    }
    //now, in AddPostAcivity when user pulish post send notification with same topic "POST"

    private void onSubscribePostNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_POST_NOTIFICATION + "")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "You will receive post notifications";

                        if (!task.isSuccessful()) {

                            msg = "Subscription failed";

                        }

                        Controller.showLongToast(msg, dashboardActivity);


                    }
                });

    }

}
