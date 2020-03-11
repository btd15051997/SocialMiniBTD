package com.example.socialminibtd.View.Fragment.HomFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.ListPostAdapter;
import com.example.socialminibtd.Model.ListPost;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Fragment.ProfileFragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements IHomeFragmentView, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ImageView img_user_sortmenu;
    private View mView;
    private DashboardActivity mDashboardActivity;
    private FirebaseAuth firebaseAuth;
    private ImageView img_add_post_home;
    private EditText edt_search_listpost;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RecyclerView recyclerView_Home;
    private ArrayList<ListPost> arrayList_home;
    private ListPostAdapter postAdapter;


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
        mView = inflater.inflate(R.layout.fragment_home_map, container, false);

        onmMappingView();

        onGetDataListPost();

        return mView;

    }


    @Override
    public void onmMappingView() {

        firebaseAuth = FirebaseAuth.getInstance();

        img_user_sortmenu = mView.findViewById(R.id.img_user_sortmenu);

        img_add_post_home = mView.findViewById(R.id.img_add_post_home);

        recyclerView_Home = mView.findViewById(R.id.recyc_list_post_home);

        edt_search_listpost = mView.findViewById(R.id.edt_search_listpost);

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();

        onCustomRecyclerView();

        img_user_sortmenu.setOnClickListener(this);
        img_add_post_home.setOnClickListener(this);

        edt_search_listpost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                onSearchListPost(s.toString());

            }
        });

    }

    @Override
    public void onCustomRecyclerView() {

        recyclerView_Home.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mDashboardActivity
                , LinearLayoutManager.VERTICAL, true);

        //show newest post first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView_Home.setLayoutManager(linearLayoutManager);

        arrayList_home = new ArrayList<>();

    }

    @Override
    public void onGetDataListPost() {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (arrayList_home != null) {

                    arrayList_home.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListPost listPost = ds.getValue(ListPost.class);

                    arrayList_home.add(listPost);

                    postAdapter = new ListPostAdapter(arrayList_home, mDashboardActivity);

                }

                recyclerView_Home.setAdapter(postAdapter);

                //    postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.getMessage().toString(), mDashboardActivity);

            }
        });


    }

    @Override
    public void onShowPopupMenu() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            PopupMenu popupMenu = new PopupMenu(mDashboardActivity, img_user_sortmenu, Gravity.CENTER_HORIZONTAL);

            popupMenu.getMenu().add(Menu.NONE, 0, 0, mDashboardActivity.getResources().getString(R.string.txt_logout));

            popupMenu.getMenu().add(Menu.NONE, 1, 0, mDashboardActivity.getResources().getString(R.string.txt_ggmap));

            popupMenu.getMenu().add(Menu.NONE, 2, 0, mDashboardActivity.getResources().getString(R.string.txt_new));

            popupMenu.show();


            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {

                        case 0:

                            firebaseAuth.signOut();
                            mDashboardActivity.onCheckUserCurrent();

                            break;

                        case 1:

                            Toast.makeText(mDashboardActivity, "Google map coming soon...", Toast.LENGTH_SHORT).show();

                            break;

                        case 2:

                            Toast.makeText(mDashboardActivity, "News coming soon...", Toast.LENGTH_SHORT).show();

                            break;

                    }

                    return false;
                }
            });

        }

    }

    @Override
    public void onSearchListPost(final String searchQuery) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (arrayList_home != null) {

                    arrayList_home.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListPost listPost = ds.getValue(ListPost.class);

                    if (listPost.getuTitle().toLowerCase().contains(searchQuery)
                            || listPost.getuDescription().toLowerCase().contains(searchQuery)) {

                        arrayList_home.add(listPost);

                    }

                    postAdapter = new ListPostAdapter(arrayList_home, mDashboardActivity);

                }

                recyclerView_Home.setAdapter(postAdapter);

                //    postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.getMessage().toString(), mDashboardActivity);

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_user_sortmenu:

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

                    onShowPopupMenu();

                } else {

                    firebaseAuth.signOut();
                    mDashboardActivity.onCheckUserCurrent();
                }

                break;

            case R.id.img_add_post_home:

                mDashboardActivity.onIntentAddPost();

                break;


            case R.id.img_user_home:

                mDashboardActivity.onAddFragment(new ProfileFragment(), false
                        , false, Const.TagFragment.PROFILE_FRAGMENT, true);

                break;

        }

    }
}
