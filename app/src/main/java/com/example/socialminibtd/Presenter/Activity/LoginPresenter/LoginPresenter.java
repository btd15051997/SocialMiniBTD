package com.example.socialminibtd.Presenter.Activity.LoginPresenter;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Login.ILoginActivityView;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class LoginPresenter implements ILoginPresenter {

    private LoginActivity mLoginActivity;
    private ILoginActivityView iLoginActivityView;


    public LoginPresenter(LoginActivity mLoginActivity, ILoginActivityView iLoginActivityView) {
        this.mLoginActivity = mLoginActivity;
        this.iLoginActivityView = iLoginActivityView;
    }

    @Override
    public void onHandleLogin(String email, String pass, FirebaseAuth auth) {

        if (email.isEmpty()) {

            Controller.showLongToast(mLoginActivity.getResources().getString(R.string.txt_email_error)
                    , mLoginActivity);

        } else if (pass.isEmpty()) {

            Controller.showLongToast(mLoginActivity.getResources().getString(R.string.txt_pass_error)
                    , mLoginActivity);

        } else {

            Controller.showSimpleProgressDialog(mLoginActivity
                    , mLoginActivity.getResources().getString(R.string.txt_loading)
                    , false);

            LoginUserSocial(email, pass, auth);

        }


    }

    private void LoginUserSocial(String email, String pass, final FirebaseAuth mAuth) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(mLoginActivity, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        Controller.appLogDebug(Const.LOG_DAT, "loginUserWithEmail:success  " + user.getEmail());

                        iLoginActivityView.onLoginIntentHome();

                        Controller.showLongToast(user.getEmail().toString(), mLoginActivity);

                        Controller.removeProgressDialog();

                    } else {

                        // If sign in fails, display a message to the user.
                        Controller.removeProgressDialog();

                        Controller.appLogDebug(Const.LOG_DAT, "loginUserWithEmail:false  " + task.getException().toString());

                    }

                }).addOnFailureListener(e -> {

            Controller.appLogDebug(Const.LOG_DAT, "loginUserWithEmail:false  " + e.toString());

            Controller.showLongToast(e.getMessage().toString(), mLoginActivity);

        });

    }

    @Override
    public void onfirebaseAuthWithGoogle(GoogleSignInAccount account, final FirebaseAuth mAuth) {

        Controller.appLogDebug(Const.LOG_DAT + "  ", "firebaseAuthWithGoogle :" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mLoginActivity, task -> {

                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser user = mAuth.getCurrentUser();

                        Controller.appLogDebug(Const.LOG_DAT + "  ", "signInWithCredential:success" + user.getDisplayName() + " : " + user.getEmail());

                        Controller.showLongToast(user.getEmail().toString(), mLoginActivity);

                        onPutAuthToRealTimeDatabase(user);

                        mLoginActivity.onLoginIntentHome();

                    } else {

                        // If sign in fails, display a message to the user.
                        Controller.appLogDebug(Const.LOG_DAT, "signInWithCredential:failure  " + task.toString());
                    }

                }).addOnFailureListener(e -> {

            Controller.appLogDebug(Const.LOG_DAT, "signInWithCredential:failure  " + e.toString());
            Controller.showLongToast(e.toString(), mLoginActivity);

        });

    }

    @Override
    public void onPutAuthToRealTimeDatabase(FirebaseUser user) {

        String user_email = user.getEmail();
        String user_uid = user.getUid();
        String user_name = user.getDisplayName();
        String user_phone = user.getPhoneNumber();
        String user_picture = String.valueOf(user.getPhotoUrl());


        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("email", user_email);
        hashMap.put("uid", user_uid);
        hashMap.put("name", user_name);
        hashMap.put(Const.Params.ONLINE_STATUS, Const.Params.ONLINE);
        hashMap.put(Const.Params.TYPING_TO, "onOne");
        hashMap.put("phone", user_phone);
        hashMap.put("image", user_picture);
        hashMap.put("image_cover", "");

        // firebase database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //path to store user named "User"
        DatabaseReference reference = database.getReference("User");

        //put data within database
        reference.child(user_uid).setValue(hashMap);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    onAddNodeFollow(user);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Controller.appLogDebug(Const.LOG_DAT, "PutAuthToRealTimeDatabase  " + hashMap.toString());

    }

    private void onAddNodeFollow(FirebaseUser user) {

        Log.d(Const.LOG_DAT, "Check_follow_Vao day 1");

        Map<String, Object> map = new HashMap<>();
        map.put("fUid", user.getUid());
        map.put("fCountFl", "0");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference

                .child(user.getUid())
                .child("Follow")
                .setValue(map)
                .addOnSuccessListener(aVoid ->
                        Log.d(Const.LOG_DAT, "Check_follow_Vao day 2"))
                .addOnFailureListener(e ->
                        Log.d(Const.LOG_DAT, "Check_follow_Vao day " + e.getMessage()));

    }

}
