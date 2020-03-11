package com.example.socialminibtd.View.Fragment.ChatListFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialminibtd.Adapter.ListChatAdapter;
import com.example.socialminibtd.Model.ChatList;
import com.example.socialminibtd.Model.ListUser;
import com.example.socialminibtd.Model.ModelChat;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.View.Activity.Dashbroad.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.interfaces.RSAPrivateCrtKey;
import java.util.ArrayList;


public class ChatListFragment extends Fragment implements IChatListFragmentView {

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

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        chatListlist = new ArrayList<>();

        Reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(mCurrentUser.getUid());

        Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (chatListlist != null) {

                    chatListlist.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ChatList chatList = ds.getValue(ChatList.class);
                    chatListlist.add(chatList);

                }

                onLoadsChat();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onLoadsChat() {

        userList = new ArrayList<>();
        Reference = FirebaseDatabase.getInstance().getReference("User");
        Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (userList != null) {

                    userList.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ListUser user = ds.getValue(ListUser.class);

                    for (ChatList chatList : chatListlist) {

                        //check all uid in node User contais node Chatlist
                        if (user.getUid() != null && user.getUid().equals(chatList.getId())) {

                            userList.add(user);

                            break;

                        }

                    }

                    adapter = new ListChatAdapter(userList, dashboardActivity);

                    recyc_listuser_chat.setAdapter(adapter);

                    for (int i = 0; i < userList.size(); i++) {

                        onLastMessage(userList.get(i).getUid());

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(dashboardActivity, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void onLastMessage(final String userId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String theLastMessage = "default";

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat == null) {

                        continue;

                    }

                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();

                    if (sender == null || receiver == null) {

                        continue;
                    }

                    if (chat.getReceiver().equals(mCurrentUser.getUid()) && chat.getSender().equals(userId)

                            || chat.getReceiver().equals(userId) && chat.getSender().equals(mCurrentUser.getUid())) {

                        if (chat.getType().equals("image")){

                            theLastMessage = "Sent a photo";

                        }else {

                            theLastMessage = chat.getMessage();

                        }

                    }
                }

                adapter.setLastMessageMap(userId, theLastMessage);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Controller.showLongToast(databaseError.getMessage(), dashboardActivity);
            }
        });

    }
}
