package com.example.socialminibtd.View.Activity.Dashbroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.socialminibtd.Notifications.ModelNotifi.Token;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.PreferenceHelper;
import com.example.socialminibtd.View.Activity.AddPost.AddPostActivity;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.example.socialminibtd.View.Fragment.MessagerFragment.ChatListFragment;
import com.example.socialminibtd.View.Fragment.HomFragment.HomeFragment;
import com.example.socialminibtd.View.Fragment.ProfileFragment.ProfileFragment;
import com.example.socialminibtd.View.Fragment.UserFragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class DashboardActivity extends AppCompatActivity implements IDashboardActivityView
        , View.OnClickListener
        , BottomNavigationView.OnNavigationItemSelectedListener
        , LocationListener {

    private FirebaseAuth firebaseAuth;
    private BottomNavigationView mBottomNavigationView;
    private String mCurrentFramentHome = "";
    private String myUid;
    private boolean darkmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        darkmode = new PreferenceHelper(this).getDarkMode();
        if (darkmode == true) {

            //light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {

            //dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        firebaseAuth = FirebaseAuth.getInstance();

        onMappingView();

    }

    @Override
    public void onMappingView() {

        mBottomNavigationView = findViewById(R.id.bottom_navi_home);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (mBottomNavigationView != null) {

            mBottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);

            onAddFragment(new HomeFragment(), false, false, Const.TagFragment.HOME_MAP_FRAGMENT, true);

        }

    }

    @Override
    public void onCheckUserCurrent() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {

            myUid = user.getUid();

            new PreferenceHelper(DashboardActivity.this).putMyUid(myUid);

            //token message
            onUpdateToken(FirebaseInstanceId.getInstance().getToken());

        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            new PreferenceHelper(DashboardActivity.this).putMyUid("");
            finish();

        }

    }

    @Override
    public void onAddFragment(Fragment fragment, boolean addToBackStack, boolean TransactionAdd, String Tag, boolean isAnimate) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        if (addToBackStack) {

            fragmentTransaction.addToBackStack(Tag);

        } else {

            fragmentTransaction.addToBackStack(null);
        }

        if (isAnimate) {

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left
                    , R.anim.slide_in_left,
                    R.anim.slide_out_right);

        }

        if (TransactionAdd == true) {


            fragmentTransaction.add(R.id.content_home, fragment, Tag);

        } else {


            fragmentTransaction.replace(R.id.content_home, fragment, Tag);
        }


        mCurrentFramentHome = Tag;

        Controller.appLogDebug(Const.LOG_DAT, "CurrentFramentHome  : " + mCurrentFramentHome);

        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onUpdateToken(String token) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(myUid).setValue(token1);
        Controller.appLogDebug(Const.LOG_DAT, " Device Token : " + token);

    }

    @Override
    public void onIntentAddPost() {

        startActivity(new Intent(DashboardActivity.this, AddPostActivity.class));

    }

    @Override
    protected void onStart() {

        onCheckUserCurrent();

        super.onStart();
    }

    @Override
    protected void onResume() {

        onCheckUserCurrent();

        super.onResume();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.navigation_home:

                onAddFragment(new HomeFragment(), false, false, Const.TagFragment.HOME_MAP_FRAGMENT, true);

                return true;

            case R.id.navigation_user:

                onAddFragment(new UserFragment(), false, false, Const.TagFragment.USER_FRAGMENT, true);

                return true;

            case R.id.navigation_profile:

                onAddFragment(new ProfileFragment(), false, false, Const.TagFragment.PROFILE_FRAGMENT, true);

                return true;

            case R.id.navigation_listchat:

                onAddFragment(new ChatListFragment(), false, false, Const.TagFragment.CHATLIST_FRAGMENT, true);

                return true;

        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if (!mCurrentFramentHome.equals(Const.TagFragment.HOME_MAP_FRAGMENT)) {

            if (mCurrentFramentHome.equals(Const.TagFragment.HOME_MAP_FRAGMENT)) {

                DashboardActivity.this.finishAffinity();

            } else if (mCurrentFramentHome.equals(Const.TagFragment.USER_FRAGMENT)) {

                DashboardActivity.this.finishAffinity();

            } else if (mCurrentFramentHome.equals(Const.TagFragment.PROFILE_FRAGMENT)) {

                DashboardActivity.this.finishAffinity();

            } else if (mCurrentFramentHome.equals(Const.TagFragment.CHATLIST_FRAGMENT)) {

                DashboardActivity.this.finishAffinity();

            }

        } else {

            DashboardActivity.this.finishAffinity();
        }


        super.onBackPressed();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
