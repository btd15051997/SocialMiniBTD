package com.example.socialminibtd.Adapter;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.socialminibtd.View.Fragment.ListGroupChatFragment.ListGroupChatFragment;
import com.example.socialminibtd.View.Fragment.ListUserChatFragment.ListUserChatFragment;

public class TabLayoutAdapter extends FragmentStatePagerAdapter {

    private Activity activity;

    String listTab[];
    private ListUserChatFragment listUserChatFragment;
    private ListGroupChatFragment listGroupChatFragment;

    public TabLayoutAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;

        listTab = new String[]{"User Chat", "Group Chats"};

        listUserChatFragment = new ListUserChatFragment();
        listGroupChatFragment = new ListGroupChatFragment();
    }

    @Override
    public Fragment getItem(int i) {

        if (i == 0) {

            return listUserChatFragment;

        } else if (i == 1) {

            return listGroupChatFragment;

        }

        return null;
    }

    @Override
    public int getCount() {
        return listTab.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTab[position];
    }
}
