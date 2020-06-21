package com.example.socialminibtd.View.Fragment.UserFragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialminibtd.Adapter.ListUserAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.RecyclerLongPressClickListener;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Dialog.GroupChatsDialog.GroupChatsDialog;
import com.example.socialminibtd.View.Dialog.GroupCreate.GroupCreateDialog;
import com.example.socialminibtd.View.Dialog.SettingsDialog.SettingsDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserFragment extends Fragment implements IUserFragmentView, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;
    private DashboardActivity mDashboardActivity;
    private RecyclerView recyc_list_user;
    private ListUserAdapter mListUserAdapter;
    private ArrayList<ListUser> mUserArrayList;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private EditText search_list_user;
    private TextView txt_group_add_user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_user, container, false);

        mAuth = FirebaseAuth.getInstance();

        mFirebaseUser = mAuth.getCurrentUser();

        onMappingView();

        onShowListUser();

        onSearchListUser();


        return mView;
    }


    @Override
    public void onMappingView() {

        recyc_list_user = mView.findViewById(R.id.recyc_list_user);

        search_list_user = mView.findViewById(R.id.search_list_user);

        txt_group_add_user = mView.findViewById(R.id.txt_group_add_user);
        txt_group_add_user.setOnClickListener(this);


        recyc_list_user.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mDashboardActivity, LinearLayoutManager.VERTICAL, false);

        recyc_list_user.setLayoutManager(linearLayoutManager);

        mUserArrayList = new ArrayList<>();


    }

    @Override
    public void onShowListUser() {


        Controller.showSimpleProgressDialog(mDashboardActivity
                , mDashboardActivity.getResources().getString(R.string.txt_loading)
                , false);

        //get data all Users
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mUserArrayList != null) {

                    mUserArrayList.clear();
                }

                Controller.removeProgressDialog();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    ListUser listUser = dataSnapshot1.getValue(ListUser.class);

                    if (!listUser.getUid().equals(mFirebaseUser.getUid())) {

                        mUserArrayList.add(listUser);

                    }

                }

                mListUserAdapter = new ListUserAdapter(mUserArrayList, mDashboardActivity);

                mListUserAdapter.notifyDataSetChanged();

                recyc_list_user.setAdapter(mListUserAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.removeProgressDialog();

            }
        });

    }

    @Override
    public void onSearchListUser() {

        search_list_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                Controller.appLogDebug(Const.LOG_DAT, "Search List : " + s.toString());

                if (!search_list_user.getText().toString().isEmpty()) {

                    //get data all Users
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (mUserArrayList != null) {

                                mUserArrayList.clear();
                            }


                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                ListUser listUser = dataSnapshot1.getValue(ListUser.class);

                                // search wwith name and email
                                if (!listUser.getUid().equals(mFirebaseUser.getUid())) {

                                    if (listUser.getName().contains(s.toString())
                                            || listUser.getEmail().contains(s.toString())) {

                                        mUserArrayList.add(listUser);

                                    }
                                }

                            }

                            mListUserAdapter = new ListUserAdapter(mUserArrayList, mDashboardActivity);

                            mListUserAdapter.notifyDataSetChanged();

                            recyc_list_user.setAdapter(mListUserAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });


                } else {


                }

            }
        });

    }

    @Override
    public void onShowDialogCreateGroup() {

        GroupCreateDialog dialog_group = new GroupCreateDialog();
        dialog_group.setCancelable(true);
        dialog_group.show(getFragmentManager(), "GroupCreateDialog");

    }

    @Override
    public void onShowDialogChatsGroup() {

        GroupChatsDialog dialog_group_chats = new GroupChatsDialog();
        dialog_group_chats.setCancelable(true);
        dialog_group_chats.show(getFragmentManager(), "GroupChatsDialog");


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_group_add_user:

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

                    PopupMenu popupMenu = new PopupMenu(mDashboardActivity, txt_group_add_user, Gravity.END);

                    popupMenu.getMenu().add(Menu.NONE, 0, 0, mDashboardActivity.getResources().getString(R.string.txt_create_group));
                    popupMenu.getMenu().add(Menu.NONE, 1, 0, mDashboardActivity.getResources().getString(R.string.txt_show_group));


                    popupMenu.show();


                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case 0:

                                    onShowDialogCreateGroup();

                                    break;

                                case 1:

                                    onShowDialogChatsGroup();

                                    break;
                            }

                            return false;
                        }
                    });

                }


                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
