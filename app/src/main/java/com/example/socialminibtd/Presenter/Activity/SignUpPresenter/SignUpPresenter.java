package com.example.socialminibtd.Presenter.Activity.SignUpPresenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Register.IRegisterActivityView;
import com.example.socialminibtd.View.Activity.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpPresenter implements ISignUpPresenter {

    private RegisterActivity registerActivity;
    private IRegisterActivityView iRegisterActivityView;

    public SignUpPresenter(RegisterActivity registerActivity, IRegisterActivityView iRegisterActivityView) {
        this.registerActivity = registerActivity;
        this.iRegisterActivityView = iRegisterActivityView;
    }

    @Override
    public void onHandleSignNormal(String email, String pass, String name, String phone, String re_pass,String sex, FirebaseAuth auth) {

        if (email.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_email_error), registerActivity);

        } else if (name.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_name_error), registerActivity);


        } else if (phone.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_phone_error), registerActivity);

        } else if (pass.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_error), registerActivity);

        } else if (re_pass.isEmpty()) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_re_pass_error), registerActivity);

        } else if (pass.length() < 6) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_six_character), registerActivity);

        } else if (re_pass.length() < 6) {

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_six_character), registerActivity);

        }else if(sex.isEmpty()){

            Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_check_gender), registerActivity);

        } else {

            if (pass.equals(re_pass)) {

                Controller.showSimpleProgressDialog(registerActivity
                        , registerActivity.getResources().getString(R.string.txt_loading), false);

                SignUpWithEmailPass(email,name,phone, pass,sex, auth);

            } else {

                Controller.showLongToast(registerActivity.getResources().getString(R.string.txt_pass_confirmpass_not_match), registerActivity);

            }

        }

    }

    @Override
    public void onPutAuthToRealTimeDatabase(FirebaseUser user,String name,String phone,String gender) {

        String user_email = user.getEmail();
        String user_uid = user.getUid();


        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("email", user_email);
        hashMap.put("uid", user_uid);
        hashMap.put("name", name);
        hashMap.put(Const.Params.ONLINE_STATUS, Const.Params.ONLINE);
        hashMap.put(Const.Params.TYPING_TO, "onOne");
        hashMap.put("phone", phone);
        hashMap.put("gender", gender);
        hashMap.put("image", "");
        hashMap.put("image_cover", "");

        // firebase database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //path to store user named "User"
        DatabaseReference reference = database.getReference("User");

        //put data within database
        reference.child(user_uid).setValue(hashMap);



        Controller.appLogDebug(Const.LOG_DAT, "PutAuthToRealTimeDatabase  " + hashMap.toString());

    }

    private void SignUpWithEmailPass(String email, final String name, final String phone, String password, final String gender, final FirebaseAuth mAuth) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d(Const.LOG_DAT, "createUserWithEmail:success");

                            Controller.removeProgressDialog();

                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.d(Const.LOG_DAT, "createUserWithEmail:success  " + user.getEmail());

                            Controller.showLongToast(user.getEmail().toString(), registerActivity);

                            onPutAuthToRealTimeDatabase(user,name,phone,gender);

                            iRegisterActivityView.onIntentProfile();

                        } else {

                            Controller.removeProgressDialog();

                            Controller.appLogDebug(Const.LOG_DAT, "createUserWithEmail:false  " + task.getException().toString());

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Controller.removeProgressDialog();

                Log.d(Const.LOG_DAT, "createUserWithEmail:false  " + e.toString());

                Controller.showLongToast(e.getMessage().toString(), registerActivity);

            }
        });
    }
}
