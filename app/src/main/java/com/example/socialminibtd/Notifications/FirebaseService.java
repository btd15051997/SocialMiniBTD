package com.example.socialminibtd.Notifications;

import androidx.annotation.NonNull;

import com.example.socialminibtd.Notifications.ModelNotifi.Token;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.PreferenceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseService extends FirebaseInstanceIdService {

    private String token;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        token = FirebaseInstanceId.getInstance().getToken();
        Controller.appLogDebug(Const.LOG_DAT," Token :"+token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            UpdateToken(token);
        }

    }

    private void UpdateToken(String token) {

        new PreferenceHelper(this).putDeviceToken(token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token1 = new Token(token);

        reference.child(user.getUid()).setValue(token1);

    }
}
