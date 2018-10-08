package tech.honeysharma.techbmechat.Blog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tech.honeysharma.techbmechat.Account.MainActivity;
import tech.honeysharma.techbmechat.Account.SettingsActivity;
import tech.honeysharma.techbmechat.Account.StartActivity;
import tech.honeysharma.techbmechat.Chat.UsersActivity;
import tech.honeysharma.techbmechat.R;
import tech.honeysharma.techbmechat.Utility.Utility;

public class BlogActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private Toolbar mToolbar;
    private DatabaseReference mDatabase,mDatabaseUser,mDatabaseLike;
    private DatabaseReference mUserRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private ProgressDialog mProgress;
    private FirebaseAuth auth;

    private List<Blog> blogList = new ArrayList<>();
    private List<Blog> blogs = new ArrayList<>();

    private boolean mProcessLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TechbMe Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        auth = FirebaseAuth.getInstance();
        mProgress=new ProgressDialog(this);

        String uid=auth.getCurrentUser().getUid();


        mDatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Like");

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");

        if (auth.getCurrentUser()!=null)
        {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
        }

        mDatabaseLike.keepSynced(true);

        mBlogList=(RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(BlogActivity.this,PostActivity.class));
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnectivity(this);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (Blog.class, R.layout.blog_row, BlogViewHolder.class,
                        mDatabase) {


            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, final int position) {



                final String post_key = getRef(position).getKey();



                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getUid())) {

                    viewHolder.deleteIcon.setVisibility(View.VISIBLE);

                } else {
                    viewHolder.deleteIcon.setVisibility(View.INVISIBLE);
                }




                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setDate(model.getDate());

                viewHolder.setLikeBtn(post_key);

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(BlogActivity.this, "Post Clicked", Toast.LENGTH_LONG).show();

                    }
                });

                viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showAlertDialog(viewHolder, position, model);

                    }
                });


                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                    } else {
                                        mDatabaseLike.child(post_key).child(auth.getCurrentUser().getUid()).setValue(mDatabaseUser.child(auth.getCurrentUser().getUid()).child("name").toString());
                                        mProcessLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                });

            }

        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void showAlertDialog(final BlogViewHolder viewHolder, final int position, final Blog model) {

        AlertDialog.Builder builder = new AlertDialog.Builder(BlogActivity.this);
        builder.setTitle("Delete Blog");
        builder.setMessage("Are you sure you want to delete this blog?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Log.e("BlogActivity", "onClick: ");

                Query query = FirebaseDatabase.getInstance().getReference().child("Blog");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot singlesnapshot : dataSnapshot.getChildren()) {

                             String key = singlesnapshot.getKey();

                            Log.e("BlogActivity", "onDataChange: Model UID: " + model.getUid());
                            Log.e("BlogActivity", "onDataChange: Model title: " + model.getTitle());
                            Log.e("BlogActivity", "onDataChange: Model desc: " + model.getDesc());

                             if (model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                     && model.getTitle().equals(singlesnapshot.getValue(Blog.class).getTitle())
                                     && model.getDesc().equals(singlesnapshot.getValue(Blog.class).getDesc())) {

                                 FirebaseDatabase.getInstance().getReference().child("Blog").child(key).removeValue();

                             }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("BlogActivity", "onCancelled: " + databaseError.getMessage());
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });
        builder.show();

    }

    @Override
    protected void onResume() {
        checkConnectivity(this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        checkConnectivity(this);
        super.onRestart();
    }

    @Override
    protected void onPostResume() {
        checkConnectivity(this);
        super.onPostResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId() == R.id.main_logout_btn){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(BlogActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(BlogActivity.this, UsersActivity.class);
            startActivity(settingsIntent);

        }

        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return true;
    }

    private void sendToStart() {

        Intent startIntent = new Intent(BlogActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mview;
        TextView post_uname,post_date,liketext, post_title, post_desc;

        ImageButton mLikebtn;
        ImageView deleteIcon;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mview=itemView;


            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Like");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);

            mLikebtn=(ImageButton)mview.findViewById(R.id.likeimg);
            liketext=(TextView)mview.findViewById(R.id.liketext);
            post_title=(TextView)mview.findViewById(R.id.post_title);
            post_desc=(TextView)mview.findViewById(R.id.post_desc);

            deleteIcon = mview.findViewById(R.id.delete_icon);

            post_uname= (TextView) mview.findViewById(R.id.post_uname);
            post_date= (TextView) mview.findViewById(R.id.post_date);

            post_uname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Clicked the Username");
                }
            });

            post_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Clicked the date");
                }
            });

        }

        public void setLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikebtn.setImageResource(R.drawable.ic_action_like_n);
                        liketext.setText("Dislike");
                    }else{
                        mLikebtn.setImageResource(R.drawable.ic_action_like);
                        liketext.setText("Like");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title){
           // TextView post_title=(TextView)mview.findViewById(R.id.post_title);
            post_title.setText(title);
        }


        public void setDesc(String desc){
          //  TextView post_title=(TextView)mview.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image=(ImageView)mview.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

        }

        public void setUsername(String uname){
            //TextView post_uname=(TextView)mview.findViewById(R.id.post_uname);
            post_uname.setText(uname);
        }

        public void setDate(String date){

            //TextView post_date=(TextView)mview.findViewById(R.id.post_date);

            post_date.setText(date);
        }
    }

    public void checkConnectivity(Context context){
        if(!Utility.isOnline(this)){
            Snackbar snackbar=Snackbar.make(findViewById(R.id.drawer),"No internet connection",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkConnectivity(BlogActivity.this);
                        }
                    });
            snackbar.show();
        }
    }






}
