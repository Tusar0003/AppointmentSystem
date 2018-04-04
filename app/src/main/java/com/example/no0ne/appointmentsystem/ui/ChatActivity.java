package com.example.no0ne.appointmentsystem.ui;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.no0ne.appointmentsystem.R;
import com.example.no0ne.appointmentsystem.model.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText mMessageEditText;
    private ImageView mSendImageView;
    private ListView mMessageListView;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootReference;

    private String mChatUserId;
    private String mChatUserName;
    private String mCurrentUserId;

    private List<Message> mMessageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessageEditText = findViewById(R.id.edit_text_message);
        mSendImageView = findViewById(R.id.image_button_send);
        mMessageListView = findViewById(R.id.list_view_message);

        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();

        mChatUserId = getIntent().getStringExtra("userId");
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        loadMessages();
        setCurrentUserName();

        mSendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mMessageEditText.getText().toString();

                if (!TextUtils.isEmpty(message)) {
                    DatabaseReference userPushReference = mRootReference.child("Message")
                            .child(mCurrentUserId).child(mChatUserId).push();

                    String currentUserRef = "Message/" + mCurrentUserId + "/" + mChatUserId;
                    String chatUserRef = "Message/" + mChatUserId + "/" + mCurrentUserId;
                    String pushId = userPushReference.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message", message);
                    messageMap.put("time", new Date().getTime());
                    messageMap.put("from", mChatUserName);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
                    messageUserMap.put(chatUserRef + "/" + pushId, messageMap);

                    mMessageEditText.setText(null);

                    mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.e("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });

//                    mRootReference.child("Message").child(mCurrentUserId).child(mChatUserId)
//                            .push().setValue(new ChatMessage(message, mChatUserName));
//
//                    mMessageEditText.setText("");
//                    mMessageEditText.requestFocus();
                }
            }
        });
    }

    private void loadMessages() {
        try {
            Query query = mRootReference.child("Messages")
                    .child(mCurrentUserId).child(mChatUserId);

            FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                    .setQuery(query, ChatMessage.class)
                    .setLayout(R.layout.single_message_view)
                    .build();

            FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(options) {
                @Override
                protected void populateView(View view, ChatMessage model, int position) {
                    TextView userNameTextView = view.findViewById(R.id.text_view_user_name);
                    TextView timeTextView = view.findViewById(R.id.text_view_time);
                    TextView messageTextView = view.findViewById(R.id.text_view_message);

                    userNameTextView.setText(model.getUser_name());
                    timeTextView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
                    messageTextView.setText(model.getMessage());
                }
            };

            mMessageListView.setAdapter(adapter);
        } catch (Exception e) {

        }
    }

    private void setCurrentUserName() {
        DatabaseReference userReference = mRootReference.child("Users").child(mCurrentUserId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatUserName = dataSnapshot.child("user_name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
