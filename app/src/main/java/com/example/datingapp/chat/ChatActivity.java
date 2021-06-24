package com.example.datingapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.datingapp.Matches.MatchesActivity;
import com.example.datingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private  RecyclerView.LayoutManager mChatLayoutManager;
    private EditText mSendEditText;
    private ImageButton mBack;
    private String notification;
    private String currentUserID,matchId,chatId;
    private String matchName,matchGive,matchNeed,matchBudget, matchProfile;
    private String lastMeesage, lastTimeStamp;
    private String message, createdByUser, isSeen, messageId, currentUserName;
    private Boolean currentUserBoolean;
    private ImageButton mSendButton;
    ValueEventListener seenListener;
    DatabaseReference mDatabaseUser, mDatabaseChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        matchId= getIntent().getExtras().getString("matchId");
        matchName= getIntent().getExtras().getString("matchName");
        matchGive= getIntent().getExtras().getString("give");
        matchNeed= getIntent().getExtras().getString("need");
        matchBudget= getIntent().getExtras().getString("budget");
        matchProfile= getIntent().getExtras().getString("profile");
        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat= FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mChatLayoutManager= new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter= new ChatAdapter(getDataSetChat(),ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
        mSendEditText= findViewById(R.id.message);
        mBack= findViewById(R.id.chatBack);
        mSendButton= findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    },100);
                }
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, MatchesActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });
        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat= new HashMap();
        onchat.put("onChat",matchId);
        reference.updateChildren(onchat);
        DatabaseReference current= FirebaseDatabase.getInstance().getReference("Users")
                .child(matchId).child("connections").child("matches").child(currentUserID);
        Map lastSeen= new HashMap();
        lastSeen.put("lastSeen","false");
        current.updateChildren(lastSeen);
    }

    @Override
    protected void onPause() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat= new HashMap();
        onchat.put("onChat","None");
        reference.updateChildren(onchat);
        super.onPause();
    }

    @Override
    protected void onStop() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat= new HashMap();
        onchat.put("onChat","None");
        reference.updateChildren(onchat);
        super.onStop();
    }

    private void seenMessage(final  String text) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(matchId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.child("onChat").exists()) {
                        if(dataSnapshot.child("notificationKey").exists())
                            notification=dataSnapshot.child("notificationKey").getValue().toString();
                        else
                            notification=" ";
                        if(!dataSnapshot.child("onChat").getValue().toString().equals(currentUserID)){
                            new SendNotification(text,"New Message from" + currentUserName,notification,"activityToBeOpened","MatchesActivity");
                        }
                        else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId);
                            Map seenInfo= new HashMap();
                            seenInfo.put("lastSeend","false");
                            reference.updateChildren(seenInfo);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu,menu);
        TextView mMatchNameTextView=(TextView)findViewById(R.id.nameMatchChat);
        mMatchNameTextView.setText(matchName);
        return true;
    }
    public void showProfile(View v) {
        LayoutInflater inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.item_profile,null);
        TextView name= (TextView) popupView.findViewById(R.id.name);
        ImageView image= (ImageView) popupView.findViewById(R.id.image);
        TextView budget= (TextView) popupView.findViewById(R.id.budget);
        ImageView mNeedImage= (ImageView) popupView.findViewById(R.id.needImage);
        ImageView mGiveImage= (ImageView) popupView.findViewById(R.id.giveImage);
        name.setText(matchName);
        budget.setText(matchBudget);
        
        
        if(matchNeed.equals("Netflix"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.netflix));
        else if(matchNeed.equals("Youtube Originals"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.youtube));
        else if(matchNeed.equals("HBO Now"))
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.hbo));
        else
            mNeedImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.none));


        //Give Image

        if(matchNeed.equals("Netflix"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.netflix));
        else if(matchNeed.equals("Youtube Originals"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.youtube));
        else if(matchNeed.equals("HBO Now"))
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.hbo));
        else
            mGiveImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.none));
        switch (matchProfile){
            case "default":
                Glide.with(popupView.getContext()).load(R.drawable.profile).into(image);
                break;
            default:
                Glide.clear(image);
                Glide.with(popupView.getContext()).load(matchProfile).into(image);
                break;

        }
        int width= LinearLayout.LayoutParams.WRAP_CONTENT;
        int height= LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable= true;
        final PopupWindow popupWindow= new PopupWindow(popupView,width,height,focusable);
        hideSoftKeyBoard();
        popupWindow.showAtLocation(v, Gravity.CENTER,0,0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.unmatch){
            new AlertDialog.Builder(ChatActivity.this)
                .setTitle("Unmatch").setMessage("Are u sure you want to unmatch").setPositiveButton("Unmatch", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteMatch(matchId);
                    Intent intent= new Intent(ChatActivity.this,MatchesActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(ChatActivity.this, "Unmatch success", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Dismiss",null)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
        else if(item.getItemId()==R.id.viewProfile) {
            showProfile(findViewById(android.R.id.content));

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMatch(String matchId) {
        DatabaseReference matchId_in_UserId_dbReference=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserID).child("connections").child("matches").child(matchId);
        DatabaseReference userId_in_matchId_dbReference=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(matchId).child("connections").child("matches").child(currentUserID);
        DatabaseReference yebs_in_matchId_dbReference=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(matchId).child("connections").child("yebs").child(currentUserID);
        DatabaseReference yebs_in_UserId_dbReference=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserID).child("connections").child("yeps").child(matchId);

        DatabaseReference matchID_chat_dbReference=FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        matchID_chat_dbReference.removeValue();
        matchId_in_UserId_dbReference.removeValue();
        userId_in_matchId_dbReference.removeValue();
        yebs_in_matchId_dbReference.removeValue();
        yebs_in_UserId_dbReference.removeValue();
    }
    private void sendMessage(){
        final String sendMessageText= mSendEditText.getText().toString();
        long now = System.currentTimeMillis();
        String timeStamp= Long.toString(now);

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb= mDatabaseChat.push();
            Map newMessage= new HashMap();
            newMessage.put("createdByUser",currentUserID);
            newMessage.put("text",sendMessageText);
            newMessage.put("timeStamp",timeStamp);
            newMessage.put("seen","false");

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("name").exists()){
                            currentUserName= dataSnapshot.child("name").getValue().toString();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            lastMeesage= sendMessageText;
            lastTimeStamp= timeStamp;
            updateLastMessage();
            seenMessage(sendMessageText);
            newMessageDb.setValue(newMessage);

        }
        mSendEditText.setText(null);
    }

    private void updateLastMessage() {
        DatabaseReference currentUserDb= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID)
                .child("connections").child("matches").child(matchId);
        DatabaseReference matchDb= FirebaseDatabase.getInstance().getReference().child("Users").child(matchId)
                .child("connections").child("matches").child(currentUserID);
        Map lastMessageMap= new HashMap();
        lastMessageMap.put("lastMessage",lastMeesage);
        Map lastTimeStampMap= new HashMap();
        lastTimeStampMap.put("lastTimeStamp",lastTimeStamp);
        Map lastSeen= new HashMap();
        lastSeen.put("lastSeen","true");
        currentUserDb.updateChildren(lastSeen);
        currentUserDb.updateChildren(lastMessageMap);
        currentUserDb.updateChildren(lastTimeStampMap);

        matchDb.updateChildren(lastMessageMap);
        matchDb.updateChildren(lastTimeStampMap);
    }


    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatId= dataSnapshot.getValue().toString();
                    mDatabaseChat= mDatabaseChat.child(chatId);
                    getChatMessage();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getChatMessage() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    messageId= null;
                    message=null;
                    createdByUser=null;
                    isSeen=null;
                    if(dataSnapshot.child("text").getValue()!= null) {
                        message=dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!= null) {
                        createdByUser=dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(dataSnapshot.child("seen").getValue()!= null) {
                        isSeen=dataSnapshot.child("seen").getValue().toString();
                    }
                    else isSeen="true";

                    messageId=dataSnapshot.getKey().toString();
                    if(message!= null && createdByUser!= null) {
                        currentUserBoolean= false;
                        if(createdByUser.equals(currentUserID)){
                            currentUserBoolean =true;

                        }
                        ChatObject newMessage= null;
                        if(isSeen.equals("false")){
                            if(!currentUserBoolean){
                                isSeen="true";
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat")
                                        .child(chatId).child(messageId);
                                Map seenInfo= new HashMap();
                                seenInfo.put("seen","true");
                                reference.updateChildren(seenInfo);
                                newMessage = new ChatObject(message,currentUserBoolean,true);

                            }
                            else {
                                newMessage= new ChatObject(message,currentUserBoolean,false);
                            }
                        }
                        else newMessage = new ChatObject(message,currentUserBoolean,true);

                        DatabaseReference usersInChat= FirebaseDatabase.getInstance().getReference().child("Chat")
                                .child(matchId);

                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        if(mRecyclerView.getAdapter()!= null && resultsChat.size()> 0){
                            mRecyclerView.smoothScrollToPosition(resultsChat.size() -1 );
                        }else
                            Toast.makeText(ChatActivity.this,"Chat Empty",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private ArrayList<ChatObject> resultsChat= new ArrayList<>();
    private List<ChatObject> getDataSetChat() {
        return  resultsChat;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }
}