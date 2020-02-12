package com.example.socialminibtd.Presenter.Fragment.ProfilePresenter;

import com.google.firebase.database.DataSnapshot;

public interface IProfilePresenter {
    void onHandleEditTextProfile();
    void onGetDataShowProfile(DataSnapshot dataSnapshot);
}
