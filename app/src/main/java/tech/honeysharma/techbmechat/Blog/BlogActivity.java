package tech.honeysharma.techbmechat.Blog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tech.honeysharma.techbmechat.Account.MainActivity;
import tech.honeysharma.techbmechat.Account.SettingsActivity;
import tech.honeysharma.techbmechat.Account.StartActivity;
import tech.honeysharma.techbmechat.Chat.UsersActivity;
import tech.honeysharma.techbmechat.R;
import tech.honeysharma.techbmechat.Utility.Utility;

public class BlogActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private DatabaseReference mUserRef;
    private FirebaseAuth auth;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CustomFragmentPagerAdapter mAdapter;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private TextView tvuserName;
    private ImageView ivUserImage;

    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        auth = FirebaseAuth.getInstance();

       initViews();
        if (auth.getCurrentUser()!=null)
        {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
            mUserRef.addValueEventListener(new ValueEventListener() {
                //
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    setUserData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TechnoJam Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.nv);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        tvuserName = view.findViewById(R.id.tv_user_name);
        ivUserImage = view.findViewById(R.id.iv_user_image);


        setNavigationDrawer();




    }
    private void setUserData(DataSnapshot dataSnapshot) {

        String name = dataSnapshot.child("name").getValue().toString();
        final String image = dataSnapshot.child("image").getValue().toString();

        tvuserName.setText(name);

        if (!image.equals("default")) {

            Picasso.with(this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_account_circle_white_24dp).into(ivUserImage);

        }
    }

    private void setNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            //
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.main_logout_btn:
                        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

                        FirebaseAuth.getInstance().signOut();
                        sendToStart();
                        break;
                    case R.id.main_settings_btn:

                        Intent settingsIntent = new Intent(BlogActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.main_all_btn:

                        Intent settingIntent = new Intent(BlogActivity.this, UsersActivity.class);
                        startActivity(settingIntent);
                        break;
                    case android.R.id.home:
                        NavUtils.navigateUpFromSameTask(BlogActivity.this);
                        break;
                    default:
                        return true;
                }

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }



    private void sendToStart() {

        Intent startIntent = new Intent(BlogActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    public void checkConnectivity(final Context context){
        if(!Utility.isOnline(context)){
            Snackbar snackbar=Snackbar.make(findViewById(R.id.drawer),"No internet connection",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkConnectivity(context);
                        }
                    });
            snackbar.show();
        }
    }
}
