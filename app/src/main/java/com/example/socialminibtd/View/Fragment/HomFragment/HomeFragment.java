package com.example.socialminibtd.View.Fragment.HomFragment;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.socialminibtd.Adapter.ListPostAdapter;
import com.example.socialminibtd.Adapter.NotificationAdapter;
import com.example.socialminibtd.Model.ListNotification;
import com.example.socialminibtd.Model.ListPost;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.example.socialminibtd.View.Activity.NewsActivity.NewsActivity;
import com.example.socialminibtd.View.Dialog.NotificationsDialog.NotificationsDialog;
import com.example.socialminibtd.View.Fragment.ProfileFragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements IHomeFragmentView, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ImageView img_user_sortmenu;
    private View mView;
    private DashboardActivity mDashboardActivity;
    private FirebaseAuth firebaseAuth;
    private EditText edt_search_listpost;
    private TextView txt_notification_home, txt_sumnotification_home, txt_voice_search;
    private LinearLayout linear_addpost_home;
    private CircularImageView img_current_avt_home;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RecyclerView recyclerView_Home;
    private ArrayList<ListPost> arrayList_home;
    private ListPostAdapter postAdapter;
    final int[] count = {0};


    private CompositeDisposable disposable = new CompositeDisposable();


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

        onGetImageAvatarUser();

        onGetDataListPost();

        onGetCountNotification();


        return mView;

    }

    private void onObserverArraylist() {

        if (arrayList_home != null) {

            Observable.just(arrayList_home)
                    //thread you need the work to perform on
                    .subscribeOn(Schedulers.io())
                    //thread you need to handle the result on
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<ArrayList<ListPost>>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                            disposable.add(d);
                            Log.d(Const.LOG_DAT, "Observable_onSubscribe: ");

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull ArrayList<ListPost> listPosts) {

                            postAdapter = new ListPostAdapter(arrayList_home, mDashboardActivity);
                            postAdapter.notifyDataSetChanged();
                            Log.d(Const.LOG_DAT, "Observable_onNext: " + arrayList_home);

                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            Log.d(Const.LOG_DAT, "Observable_onError: ");

                        }

                        @Override
                        public void onComplete() {

                            Log.d(Const.LOG_DAT, "Observable_onComplete: ");
                            recyclerView_Home.setAdapter(postAdapter);

                        }
                    });

        }

    }

    @Override
    public void onStop() {
        super.onStop();

        disposable.clear();
    }

    @Override
    public void onmMappingView() {

        firebaseAuth = FirebaseAuth.getInstance();

        img_user_sortmenu = mView.findViewById(R.id.img_user_sortmenu);

        recyclerView_Home = mView.findViewById(R.id.recyc_list_post_home);

        txt_voice_search = mView.findViewById(R.id.txt_voice_search);

        edt_search_listpost = mView.findViewById(R.id.edt_search_listpost);

        txt_notification_home = mView.findViewById(R.id.txt_notification_home);

        linear_addpost_home = mView.findViewById(R.id.linear_addpost_home);

        txt_sumnotification_home = mView.findViewById(R.id.txt_sumnotification_home);

        img_current_avt_home = mView.findViewById(R.id.img_current_avt_home);

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();

        onCustomRecyclerView();

        img_user_sortmenu.setOnClickListener(this);
        txt_notification_home.setOnClickListener(this);
        linear_addpost_home.setOnClickListener(this);
        txt_voice_search.setOnClickListener(this);

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

        recyclerView_Home.setItemViewCacheSize(20);
        recyclerView_Home.setDrawingCacheEnabled(true);
        recyclerView_Home.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

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

                    //      postAdapter = new ListPostAdapter(arrayList_home, mDashboardActivity);

                }

                onObserverArraylist();

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

            popupMenu.getMenu().add(Menu.NONE, 0, 0, mDashboardActivity.getResources().getString(R.string.txt_new));

            popupMenu.getMenu().add(Menu.NONE, 2, 0, mDashboardActivity.getResources().getString(R.string.txt_logout));


            popupMenu.show();


            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {

                        case 0:

                            startActivity(new Intent(mDashboardActivity, NewsActivity.class));

                            break;


                        case 2:

                            onShowDialogLogOutAccount();

                            break;

                    }

                    return false;
                }
            });

        }

    }

    private void onShowDialogLogOutAccount() {

        final Dialog dialog = new Dialog(mDashboardActivity, R.style.Custom_Dialog);

        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0.5f); //0 for no dim to 1 for full dim
        }

        dialog.setContentView(R.layout.dialog_logout);

        TextView btn_logout_yes = (TextView) dialog.findViewById(R.id.btn_logout_yes);

        dialog.show();

        btn_logout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                mDashboardActivity.onCheckUserCurrent();

            }
        });

        TextView btn_logout_no = (TextView) dialog.findViewById(R.id.btn_logout_no);

        btn_logout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onShowDialogNotification() {

        NotificationsDialog dialogOTPFragment = new NotificationsDialog();
        dialogOTPFragment.setCancelable(true);
        dialogOTPFragment.show(getFragmentManager(), "NotificationsDialog");
    }

    @Override
    public void onGetCountNotification() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(mUser.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (count != null) {

                            count[0] = 0;

                        }

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            ListNotification listNotification = ds.getValue(ListNotification.class);

                            if (!listNotification.getpUid().contains(listNotification.getsUid())) {

                                count[0] = count[0] + 1;


                            }

                        }

                        txt_sumnotification_home.setText(count[0] + "");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "onGetAllNotifications" + databaseError.getMessage());

                    }
                });

    }

    @Override
    public void onGetImageAvatarUser() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        Query query = databaseReference.orderByChild("uid").equalTo(mUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String imageUser = "" + ds.child("image").getValue();

                    if (imageUser.isEmpty()) {

                        img_current_avt_home.setImageResource(R.drawable.ic_account);

                    } else {

                        try {

                            Picasso.get().load(imageUser).placeholder(R.drawable.ic_account).into(img_current_avt_home);

                        } catch (Exception e) {

                            Picasso.get().load(R.drawable.ic_account).into(img_current_avt_home);


                        }


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onSearchListPost(final String searchQuery) {

        final DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (arrayList_home != null) {

                    arrayList_home.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListPost listPost = ds.getValue(ListPost.class);

                    if (listPost.getpTitle().toLowerCase().contains(searchQuery)
                            || listPost.getpDescription().toLowerCase().contains(searchQuery)) {

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case 1:

                if (resultCode == RESULT_OK && data != null){

                    ArrayList<String> stringArrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Log.d("Voice_search",stringArrayList.size()+"  ");

                    edt_search_listpost.setText(stringArrayList.get(0));

                }

                break;

        }
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

            case R.id.txt_notification_home:

                onShowDialogNotification();

                break;

            case R.id.txt_voice_search:

                onVoiceSearch();

                break;

            case R.id.linear_addpost_home:

                mDashboardActivity.onIntentAddPost();

                break;

            case R.id.txt_sumnotification_home:


                break;

        }

    }

    private void onVoiceSearch() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak Something");

        try {

            startActivityForResult(intent,1);

        }catch (ActivityNotFoundException e){

            Toast.makeText(mDashboardActivity, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();

        }

    }
}
