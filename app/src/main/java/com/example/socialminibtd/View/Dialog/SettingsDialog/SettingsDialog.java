package com.example.socialminibtd.View.Dialog.SettingsDialog;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.PreferenceHelper;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsDialog extends DialogFragment implements ISettingsDialogView {

    private DashboardActivity dashboardActivity;

    private FirebaseAuth mFirebaseAuth;
    private SwitchCompat switch_compat;
    private RelativeLayout content_setting;
    private EditText edt_currentpass_setting, edt_newpass_setting, edt_re_newpass_setting;
    private Button btn_changepass_confirm;
    private SwitchCompat switch_darkmode;
    private TextView txt_darkmode_settings;

    private static final String TOPIC_POST_NOTIFICATION = "POST"; // assign any value but user same for  this kind of notifications
    private boolean darkMode;

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

        edt_currentpass_setting = dialog_setting.findViewById(R.id.edt_currentpass_setting);
        edt_newpass_setting = dialog_setting.findViewById(R.id.edt_newpass_setting);
        txt_darkmode_settings = dialog_setting.findViewById(R.id.txt_darkmode_settings);
        edt_re_newpass_setting = dialog_setting.findViewById(R.id.edt_re_newpass_setting);
        btn_changepass_confirm = dialog_setting.findViewById(R.id.btn_changepass_confirm);
        switch_darkmode = dialog_setting.findViewById(R.id.switch_darkmode);

//        //restore preferences
//        SharedPreferences settings0 = this.getSharedPreferences(PREFS_NAME, 0);
//        lightMode = settings0.getBoolean("key0", true);
//
//        //retrieve selected mode
//        if (lightMode) {
//
//            //light mode
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//
//        } else {
//
//            //dark mode
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
        onCheckNotificationPost();

        onCheckChangeDarkMode();

        btn_changepass_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentPass = edt_currentpass_setting.getText().toString().trim();
                String newPass = edt_newpass_setting.getText().toString().trim();
                String reNewPass = edt_re_newpass_setting.getText().toString().trim();

                if (TextUtils.isEmpty(currentPass)) {

                    Controller.showLongToast(dashboardActivity.getResources().getString(R.string.txt_pass_error), dashboardActivity);

                    return;
                }

                if (TextUtils.isEmpty(newPass) || TextUtils.isEmpty(reNewPass)) {

                    Controller.showLongToast(dashboardActivity.getResources().getString(R.string.txt_re_pass_error), dashboardActivity);

                    return;
                }

                if (newPass.length() < 6 || reNewPass.length() < 6) {

                    Controller.showLongToast(dashboardActivity.getResources().getString(R.string.txt_pass_six_character), dashboardActivity);


                } else if (reNewPass.equals(newPass)) {

                    onUpdatePasswordUser(currentPass, newPass);


                } else {

                    Controller.showLongToast(dashboardActivity.getResources().getString(R.string.txt_pass_confirmpass_not_match), dashboardActivity);

                }

            }
        });

        return dialog_setting;
    }

    private void onCheckNotificationPost() {

        boolean isPostEnable = new PreferenceHelper(dashboardActivity).getNotification_SP();

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
    }

    private void onCheckChangeDarkMode() {

        Log.d("Dark mode:", new PreferenceHelper(dashboardActivity).getDarkMode() + "");

        darkMode = new PreferenceHelper(dashboardActivity).getDarkMode();

        if (darkMode == true) {

            txt_darkmode_settings.setText("Mode :Dark");
            switch_darkmode.setChecked(true);

        } else {

            txt_darkmode_settings.setText("Mode :Light");
            switch_darkmode.setChecked(false);

        }


        switch_darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    //save music preferences
                    new PreferenceHelper(dashboardActivity).putDarkMode(true);
                    //light mode
                    dashboardActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    getDialog().dismiss();
                    startActivity(new Intent(dashboardActivity, DashboardActivity.class));

                } else {

                    //save music preferences
                    new PreferenceHelper(dashboardActivity).putDarkMode(false);
                    //dark mode
                    dashboardActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    getDialog().dismiss();
                    startActivity(new Intent(dashboardActivity, DashboardActivity.class));

                }

            }
        });
    }

    @Override
    public void onUpdatePasswordUser(String currentPass, final String newPass) {

        Controller.showProgressDialog(dashboardActivity,
                "Updating password ...");

        final FirebaseUser user = mFirebaseAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //successfully authenticated, begin update

                        user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //updated new password
                                Controller.dimissProgressDialog();
                                Controller.showLongToast("Updated New Password", dashboardActivity);

                                if (getDialog().isShowing()) {

                                    getDialog().dismiss();

                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Controller.dimissProgressDialog();
                                Controller.appLogDebug(Const.LOG_DAT, e.toString());

                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.dimissProgressDialog();
                Controller.appLogDebug(Const.LOG_DAT, e.toString());

            }
        });


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
