package com.example.socialminibtd.Presenter.Fragment.ProfilePresenter;

import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Fragment.ProfileFragment.IProfileFragmentView;
import com.google.firebase.database.DataSnapshot;

public class ProfilePresenter implements IProfilePresenter {

    private DashboardActivity mDashboardActivity;
    private IProfileFragmentView iProfileFragmentView;

    public ProfilePresenter(DashboardActivity mDashboardActivity, IProfileFragmentView iProfileFragmentView) {
        this.mDashboardActivity = mDashboardActivity;
        this.iProfileFragmentView = iProfileFragmentView;
    }


    @Override
    public void onHandleEditTextProfile() {


    }

    @Override
    public void onGetDataShowProfile(DataSnapshot dataSnapshot) {

        Controller.removeProgressDialog();

        String email = String.valueOf(dataSnapshot.child("email").getValue());
        String name = String.valueOf(dataSnapshot.child("name").getValue());
        String phone = String.valueOf(dataSnapshot.child("phone").getValue());
        String image = String.valueOf(dataSnapshot.child("image").getValue());
        String image_cover = String.valueOf(dataSnapshot.child("image_cover").getValue());

        iProfileFragmentView.onShowUIProfile(name, email, phone, image, image_cover);

    }


}
