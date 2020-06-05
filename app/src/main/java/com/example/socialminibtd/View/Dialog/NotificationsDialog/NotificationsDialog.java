package com.example.socialminibtd.View.Dialog.NotificationsDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialminibtd.Adapter.NotificationAdapter;
import com.example.socialminibtd.Model.ListNotification;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsDialog extends DialogFragment implements INotificationsDialogView {

    private DashboardActivity dashboardActivity;
    private Dialog dialog_notification;
    private RecyclerView recyc_notifications_dialog;
    private FirebaseAuth auth;

    private ArrayList<ListNotification> arrayList;
    private NotificationAdapter notificationAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = (DashboardActivity) getActivity();

        if (getArguments() != null) {


        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialog_notification = new Dialog(dashboardActivity, R.style.DialogThemeforview);

        dialog_notification.setContentView(R.layout.dialog_notifications);

        recyc_notifications_dialog = dialog_notification.findViewById(R.id.recyc_notifications_dialog);


        auth = FirebaseAuth.getInstance();

        onGetAllNotifications();


        return dialog_notification;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onGetAllNotifications() {


        arrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(auth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (arrayList != null) {

                            arrayList.clear();
                        }

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            ListNotification listNotification = ds.getValue(ListNotification.class);

                            if (!listNotification.getpUid().contains(listNotification.getsUid())) {

                                arrayList.add(listNotification);

                            }

                        }

                        notificationAdapter = new NotificationAdapter(arrayList, dashboardActivity);

                        Controller.appLogDebug(Const.LOG_DAT, "onGetAllNotifications  :" + arrayList.toString());

                        recyc_notifications_dialog.setAdapter(notificationAdapter);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Controller.appLogDebug(Const.LOG_DAT, "onGetAllNotifications" + databaseError.getMessage());

                    }
                });


    }
}
