package tech.honeysharma.techbmechat.Chat;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import tech.honeysharma.techbmechat.Account.ProfileActivity;
import tech.honeysharma.techbmechat.R;

/**
 * created by Honey Sharma
 * */

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;

    //Search Attribute
    private EditText searchtext;
    private ImageView searchbtn,refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //search attribute
        searchtext=(EditText)findViewById(R.id.searchtext);
        searchbtn=(ImageView)findViewById(R.id.searchbtn);
        refresh=(ImageView)findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh.setVisibility(View.VISIBLE);
                String SearchText=searchtext.getText().toString();
                firebaseSearch(SearchText);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UsersActivity.this,"All Users",Toast.LENGTH_SHORT).show();
                FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = FirebaseRecyclerAdapter();
                mUsersList.setAdapter(firebaseRecyclerAdapter);
                refresh.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void firebaseSearch(String searchText) {
        Toast.makeText(UsersActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseSearchRecyclerAdapter = FirebaseSearchRecyclerAdapter(searchText);

        mUsersList.setAdapter(firebaseSearchRecyclerAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = FirebaseRecyclerAdapter();

        mUsersList.setAdapter(firebaseRecyclerAdapter);
        }

    @NonNull
    private FirebaseRecyclerAdapter<Users, UsersViewHolder> FirebaseRecyclerAdapter() {
        return new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                        Users.class,
                        R.layout.users_single_layout,
                        UsersViewHolder.class,
                        mUsersDatabase

                ) {
                    @Override
                    protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, int position) {

                        usersViewHolder.setDisplayName(users.getName());
                        usersViewHolder.setUserStatus(users.getStatus());
                        usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                        final String user_id = getRef(position).getKey();

                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View view) {

                                Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);

                                Pair[] pairs=new Pair[3];
                                pairs=usersViewHolder.getPairs();

                                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this,
                                        pairs);

                                startActivity(profileIntent,options.toBundle());

                            }
                        });

                    }
                };
    }

    @NonNull
    private FirebaseRecyclerAdapter<Users, UsersViewHolder> FirebaseSearchRecyclerAdapter(String searchText) {
        Query firebaseSearchQuery = mUsersDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        return new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users model, int position) {


                usersViewHolder.setDisplayName(model.getName());
                usersViewHolder.setUserStatus(model.getStatus());
                usersViewHolder.setUserImage(model.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        Pair[] pairs=new Pair[3];
                        pairs=usersViewHolder.getPairs();

                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this,
                                pairs);

                        startActivity(profileIntent,options.toBundle());

                    }
                });

            }
        };
    }

}
