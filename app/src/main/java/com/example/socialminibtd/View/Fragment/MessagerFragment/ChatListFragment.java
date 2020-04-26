package com.example.socialminibtd.View.Fragment.MessagerFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialminibtd.Adapter.ListChatAdapter;
import com.example.socialminibtd.Adapter.TabLayoutAdapter;
import com.example.socialminibtd.Model.ChatList;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Service.MessagerService;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class ChatListFragment extends Fragment implements IChatListFragmentView {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 100;

    //firebase auth

    private FirebaseAuth mAuth;
    private RecyclerView recyc_listuser_chat;
    private View view;

    private ArrayList<ListUser> userList;
    private ArrayList<ChatList> chatListlist;
    private ListChatAdapter adapter;

    private DatabaseReference Reference;
    private FirebaseUser mCurrentUser;

    private DashboardActivity dashboardActivity;

   // private TextView minimize_messager;
    private TabLayout tablayout;
    private TabLayoutAdapter tabLayoutAdapter;
    private ViewPager viewpager_listchat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        onMappingView();

        return view;
    }


    @Override
    public void onMappingView() {

        recyc_listuser_chat = view.findViewById(R.id.recyc_listuser_chat);
        tablayout = view.findViewById(R.id.tablayout);
      //  minimize_messager = view.findViewById(R.id.minimize_messager);
        viewpager_listchat = view.findViewById(R.id.viewpager_listchat);

        tabLayoutAdapter = new TabLayoutAdapter(getFragmentManager(), dashboardActivity);
        viewpager_listchat.setAdapter(tabLayoutAdapter);
        tablayout.setupWithViewPager(viewpager_listchat);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


//        minimize_messager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Check if the application has draw over other apps permission or not?
//                //This permission is by default available for API<23. But for API > 23
//                //you have to ask for the permission in runtime.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(dashboardActivity)) {
//
//                    //If the draw over permission is not available open the settings screen
//                    //to grant the permission.
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                            Uri.parse("package:" + dashboardActivity.getPackageName()));
//                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
//
//
//                } else {
//
//                    initializeView();
//
//                }
//
//            }
//        });


    }

    private void initializeView() {

        dashboardActivity.startService(new Intent(dashboardActivity, MessagerService.class));

        dashboardActivity.finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {

                initializeView();

            } else { //Permission is not available

                Toast.makeText(dashboardActivity,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                dashboardActivity.finish();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLoadsChat() {


    }


}
