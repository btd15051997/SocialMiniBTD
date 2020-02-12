package com.example.socialminibtd.View.Activity.Dashbroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Login.LoginActivity;
import com.example.socialminibtd.View.Fragment.HomFragment.HomeFragment;
import com.example.socialminibtd.View.Fragment.ProfileFragment.ProfileFragment;
import com.example.socialminibtd.View.Fragment.UserFragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity implements IDashboardActivityView, View.OnClickListener
        , BottomNavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth firebaseAuth;
    private ImageView img_profile_logout;
    private BottomNavigationView mBottomNavigationView;
    private String mCurrentFramentHome = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        onMappingView();
    }

    @Override
    public void onMappingView() {

      //  img_profile_logout = findViewById(R.id.img_profile_logout);
        mBottomNavigationView = findViewById(R.id.bottom_navi_home);
       // img_profile_logout.setOnClickListener(this);

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


        } else {

            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
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
    protected void onStart() {

        onCheckUserCurrent();

        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

//            case R.id.img_profile_logout:
//
//                firebaseAuth.signOut();
//                onCheckUserCurrent();
//
//                break;

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.navigation_home:

                Controller.appLogDebug("", "");

                onAddFragment(new HomeFragment(), false, false, Const.TagFragment.HOME_MAP_FRAGMENT, true);

                return true;

            case R.id.navigation_user:

                Controller.appLogDebug("", "");

                onAddFragment(new UserFragment(), false, false, Const.TagFragment.USER_FRAGMENT, true);

                return true;

            case R.id.navigation_profile:

                Controller.appLogDebug("", "");

                onAddFragment(new ProfileFragment(), false, false, Const.TagFragment.PROFILE_FRAGMENT, true);

                return true;


        }
        return false;
    }
}
