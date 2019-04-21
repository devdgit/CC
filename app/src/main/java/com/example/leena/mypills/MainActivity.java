package com.example.leena.mypills;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postsList;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef,postsRef;

    private CircleImageView navProfileImg;
    private TextView navProfileName;

    private ImageButton addNewPostBtn;

    private CircleImageView layProfileImg;
    private TextView layUsername,layFullname,layPoints,layAge;

   // private TextView postDate,postTime;

    String currentUserId,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef=FirebaseDatabase.getInstance().getReference().child("Posts");



        toolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.drawer_open,R.string.drawer_close );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);

        navProfileImg=(CircleImageView)navView.findViewById(R.id.nav_profile_img);
        navProfileName=(TextView)navView.findViewById(R.id.nav_username);

        addNewPostBtn=(ImageButton)findViewById(R.id.add_new_post);


        layProfileImg=(CircleImageView)findViewById(R.id.profile_img_in_layout);
        layFullname=(TextView)findViewById(R.id.name_in_profile);
        layUsername=(TextView)findViewById(R.id.profile_username);
        layPoints=(TextView)findViewById(R.id.profile_points);
        layAge=(TextView)findViewById(R.id.profile_age);

        postsList=(RecyclerView)findViewById(R.id.all_users_post_list);
        postsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postsList.setLayoutManager(linearLayoutManager);

        displayAllUserPosts();

        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                         name=dataSnapshot.child("fullname").getValue().toString();
                        navProfileName.setText(name);
                        layFullname.setText(name);

                        final Query findPost=postsRef.orderByChild("fullname").equalTo(name).limitToFirst(5);



                        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                                        (
                                                Posts.class,
                                                R.layout.all_posts_layout,
                                                PostsViewHolder.class,
                                                findPost
                                        )
                                {
                                    @Override
                                    protected void populateViewHolder(final PostsViewHolder viewHolder, final Posts model, int position)
                                    {
                                        viewHolder.setFullname(model.getFullname());
                                         viewHolder.setTime(model.getTime());
                                         viewHolder.setDate(model.getDate());
                                        viewHolder.setDescription(model.getDescription());
                                        // viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                        viewHolder.setPostimage( model.getImageURL());

//                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                final String delimg=model.getImageURL();
//                                                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainActivity.this);
//                                                alertDialogBuilder.setMessage("Do you want to delete this image?");
////
//                                                alertDialogBuilder.setPositiveButton("Yes",
//                                                        new DialogInterface.OnClickListener() {
//                                                           @Override
//                                                            public void onClick(DialogInterface arg0, int arg1) {
//                                                              postsRef.orderByChild("imageURL").equalTo(delimg).getRef().removeValue();
//                                                               Toast.makeText(MainActivity.this,"Image deleted",Toast.LENGTH_SHORT).show();
//                                                            }
//                                                       });
//
//                                                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
//                                                   @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        finish();
//                                                   }
//                                                });
//                                            }
//                                        });

                                    }
                                };
                        postsList.setAdapter(firebaseRecyclerAdapter);


                    }

                    if(dataSnapshot.hasChild("profile_img"))
                    {
                        String image=dataSnapshot.child("profile_img").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(navProfileImg);
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(layProfileImg);
                    }

                    if(dataSnapshot.hasChild("points"))
                    {
                        String points=dataSnapshot.child("points").getValue().toString();
                        layPoints.setText(points);
                    }

                    if(dataSnapshot.hasChild("username"))
                    {
                        String uname=dataSnapshot.child("username").getValue().toString();
                        layUsername.setText(uname);
                    }

                    if(dataSnapshot.hasChild("age"))
                    {
                        String age=dataSnapshot.child("age").getValue().toString();
                        layAge.setText(age);
                    }


                    else{
                        Toast.makeText(MainActivity.this,"Profile does not exist",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        addNewPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToPostActivity();
            }
        });




    }



    private void displayAllUserPosts() {
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Posts")
//                .limitToLast(50);
//
//        FirebaseRecyclerOptions<Posts> options =
//                new FirebaseRecyclerOptions.Builder<Posts>()
//                        .setQuery(query, Posts.class)
//                        .build();
//
//        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
//            @Override
//            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                // Create a new instance of the ViewHolder, in this case we are using a custom
//                // layout called R.layout.message for each item
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.all_posts_layout, parent, false);
//
//
//
//                return new PostsViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(PostsViewHolder holder, int position, Posts model) {
//                // Bind the Chat object to the ChatHolder
//                // ...
//
//                holder.setDescription(model.getDescription());
//                holder.setFullname(model.getFullname());
//                holder.setPostimage(model.getImageURL());
//
//
//            }
//        };



    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setFullname(String fullname)
        {
            TextView username=(TextView)mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setPostimage( String imageURL)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.post_image_layout);
            Picasso.get().load(imageURL).placeholder(R.drawable.profile).into(image);

        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description_layout);
            PostDescription.setText(description);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText(date);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText(time);
        }



    }

    private void sendUserToPostActivity() {
        Intent postIntent=new Intent(MainActivity.this,PostActivity.class);

        startActivity(postIntent);
        finish();
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser==null)
        {
            SendUserToLoginActivity();
           // Toast.makeText(this,"current user is null",Toast.LENGTH_SHORT).show();
        }
        else{
            checkUserExistance();
        }
    }

    private void checkUserExistance() {
        final String current_user_id=firebaseAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    sendUserToSetupActivity();
                }
                else if(!dataSnapshot.child(current_user_id).hasChild("fullname"))
                {
                    sendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToSetupActivity() {
        Intent setupIntent=new Intent(MainActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      //  Toast.makeText(this,"starting activity",Toast.LENGTH_SHORT).show();
        startActivity(loginIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_add:
                //Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                sendUserToPostActivity();
                break;

            case R.id.nav_home:
                drawerLayout.closeDrawers();
                Toast.makeText(this,"This is Home!",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find:
                sendUserToFindActivity();
               // Toast.makeText(this,"Find",Toast.LENGTH_SHORT).show();
                break;
                
            case R.id.nav_chatbot:
                sendUserToChatActivity();
                break;

            case R.id.nav_logout:
                firebaseAuth.signOut();
                SendUserToLoginActivity();
               // Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show();
                break;


        }
    }

    private void sendUserToChatActivity() {
        Intent findIntent=new Intent(MainActivity.this,ChatActivity.class);
        findIntent.putExtra("currentUserId",currentUserId);
      //  findIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findIntent);
    }

    private void sendUserToFindActivity() {
        Intent findIntent=new Intent(MainActivity.this,FindActivity.class);
        findIntent.putExtra("currentUserId",currentUserId);
        //findIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findIntent);
    }
}
