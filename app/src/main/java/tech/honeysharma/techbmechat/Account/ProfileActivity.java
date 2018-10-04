package tech.honeysharma.techbmechat.Account;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tech.honeysharma.techbmechat.R;

/**
 * Created by Honey Sharma
 */

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);


        mCurrent_state = "not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        /* mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        */


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                if(mCurrent_user.getUid().equals(user_id)){

                    mDeclineBtn.setEnabled(false);
                    mDeclineBtn.setVisibility(View.INVISIBLE);

                    mProfileSendReqBtn.setEnabled(false);
                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);
                }

                //--------------- FRIENDS LIST / REQUEST FEATURE -----
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            getRequestType(req_type);

                            //mProgressDialog.dismiss();


                        } else {

                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }

                                    //mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                   // mProgressDialog.dismiss();

                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                // --------------- NOT FRIENDS STATE ------------
                if(mCurrent_state.equals("not_friends")){

                    NotFriend(user_id);
                }

                // - -------------- CANCEL REQUEST STATE ------------
                if(mCurrent_state.equals("req_sent")){

                    CancelRequest(user_id);
                }

                // ------------ REQ RECEIVED STATE ----------
                if(mCurrent_state.equals("req_received")){

                    RequestReceived(user_id);

                }

                // ------------ UNFRIENDS ---------
                if(mCurrent_state.equals("friends")){

                    unfriend(user_id);

                }
            }
        });
    }

    private void getRequestType(String req_type) {
        if(req_type.equals("received")){

            mCurrent_state = "req_received";
            mProfileSendReqBtn.setText("Accept Friend Request");

            mDeclineBtn.setVisibility(View.VISIBLE);
            mDeclineBtn.setEnabled(true);


        } else if(req_type.equals("sent")) {

            mCurrent_state = "req_sent";
            mProfileSendReqBtn.setText("Cancel Friend Request");

            mDeclineBtn.setVisibility(View.INVISIBLE);
            mDeclineBtn.setEnabled(false);

        }
    }

    private void NotFriend(String user_id) {
        DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
        String newNotificationId = newNotificationref.getKey();

        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", mCurrent_user.getUid());
        notificationData.put("type", "request");

        Map requestMap = new HashMap();
        requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
        requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
        requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

        mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){

                    Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                } else {

                    mCurrent_state = "req_sent";
                    mProfileSendReqBtn.setText("Cancel Friend Request");

                }

                mProfileSendReqBtn.setEnabled(true);


            }
        });
    }

    private void CancelRequest(final String user_id) {
        mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        mProfileSendReqBtn.setEnabled(true);
                        mCurrent_state = "not_friends";
                        mProfileSendReqBtn.setText("Send Friend Request");

                        mDeclineBtn.setVisibility(View.INVISIBLE);
                        mDeclineBtn.setEnabled(false);


                    }
                });

            }
        });
    }

    private void RequestReceived(String user_id) {
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

        Map friendsMap = new HashMap();
        friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
        friendsMap.put("Friends/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


        friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
        friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);


        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                if(databaseError == null){

                    mProfileSendReqBtn.setEnabled(true);
                    mCurrent_state = "friends";
                    mProfileSendReqBtn.setText("Unfriend this Person");

                    mDeclineBtn.setVisibility(View.INVISIBLE);
                    mDeclineBtn.setEnabled(false);

                } else {

                    String error = databaseError.getMessage();

                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void unfriend(String user_id) {
        Map unfriendMap = new HashMap();
        unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
        unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

        mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                if(databaseError == null){

                    mCurrent_state = "not_friends";
                    mProfileSendReqBtn.setText("Send Friend Request");

                    mDeclineBtn.setVisibility(View.INVISIBLE);
                    mDeclineBtn.setEnabled(false);

                } else {

                    String error = databaseError.getMessage();

                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                }

                mProfileSendReqBtn.setEnabled(true);

            }
        });
    }
}
