package com.example.socialminibtd.View.Fragment.UserFragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.socialminibtd.Adapter.ListUserAdapter;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.RecyclerLongPressClickListener;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserFragment extends Fragment implements IUserFragmentView {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;


    private View mView;
    private DashboardActivity mDashboardActivity;
    private RecyclerView recyc_list_user;
    private ListUserAdapter mListUserAdapter;
    private ArrayList<ListUser> mUserArrayList;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private EditText search_list_user;


//    public static UserFragment newInstance(String param1, String param2) {
//        UserFragment fragment = new UserFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
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

        onClickToChat();

        return mView;
    }


    @Override
    public void onMappingView() {

        recyc_list_user = mView.findViewById(R.id.recyc_list_user);

        search_list_user = mView.findViewById(R.id.search_list_user);

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
    public void onClickToChat() {

//        if (mUserArrayList != null) {
//
//
//            recyc_list_user.addOnItemTouchListener(new RecyclerLongPressClickListener(mDashboardActivity, recyc_list_user, new RecyclerLongPressClickListener.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//
//                    Intent intent = new Intent(mDashboardActivity, ChatActivity.class);
//                    intent.putExtra("hisUid", mUserArrayList.get(position).getUid());
//                    mDashboardActivity.startActivity(intent);
//
//
//                }
//
//                @Override
//                public void onLongItemClick(View view, int position) {
//
//                }
//            }));
//        }

    }
}
