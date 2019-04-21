package com.example.leena.mypills;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView usersList;
    private DatabaseReference userRef;
    private ImageButton searchBtn;
    private EditText searchName;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        usersList=(RecyclerView)findViewById(R.id.all_users_list);
        usersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        usersList.setLayoutManager(linearLayoutManager);

        searchBtn=(ImageButton)findViewById(R.id.find_user_btn);
        searchBtn.setBackgroundDrawable(null);
        searchName=(EditText)findViewById(R.id.find_user_edit);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String find=searchName.getText().toString();
                displayTheUser(find);
            }
        });

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");



       // displayAllUsers();


        mToolbar=(Toolbar)findViewById(R.id.find_friends_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Users");



    }

    private void displayTheUser(String find) {
        //Toast.makeText(this,"Finding...",Toast.LENGTH_SHORT).show();
        Query findUser=userRef.orderByChild("nametolower").startAt(find.toLowerCase()).endAt(find.toLowerCase()+"\uf8ff");
        FirebaseRecyclerAdapter<Users, FindViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, FindViewHolder>
                        (
                                Users.class,
                                R.layout.individual_user,
                                FindViewHolder.class,
                                findUser
                        )
                {
                    @Override
                    protected void populateViewHolder(FindViewHolder viewHolder, Users model, int position)
                    {
                        final String userKey=getRef(position).getKey();

                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setPoints(model.getPoints());
                        viewHolder.setProfileImg(model.getProfile_img());
                        viewHolder.setAge(model.getAge());
                        viewHolder.setUsername(model.getUsername());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickUser=new Intent(FindActivity.this,clickPostActivity.class);
                                currentUserId=getIntent().getExtras().getString("currentUserId").toString();
                                clickUser.putExtra("userKey",userKey);
                                clickUser.putExtra("currentUserId",currentUserId);
                                startActivity(clickUser);
                            }
                        });
                    }
                };
        usersList.setAdapter(firebaseRecyclerAdapter);

    }

    private void displayAllUsers() {

        FirebaseRecyclerAdapter<Users, FindViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, FindViewHolder>
                        (
                                Users.class,
                                R.layout.individual_user,
                                FindViewHolder.class,
                                userRef
                        )
                {
                    @Override
                    protected void populateViewHolder(FindViewHolder viewHolder, Users model, int position)
                    {
                        viewHolder.setFullname(model.getFullname());
                       viewHolder.setPoints(model.getPoints());
                       viewHolder.setProfileImg(model.getProfile_img());
                       viewHolder.setAge(model.getAge());
                       viewHolder.setUsername(model.getUsername());
                    }
                };
        usersList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class FindViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FindViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setFullname(String fullname)
        {
            TextView name=(TextView)mView.findViewById(R.id.individual_name);
            name.setText(fullname);
        }

        public void setAge(String age)
        {
            TextView ageview=(TextView)mView.findViewById(R.id.individual_age);
            ageview.setText(age);
        }

        public void setUsername(String username)
        {
            TextView user=(TextView)mView.findViewById(R.id.individual_username);
            user.setText(username);
        }

        public void setPoints(String points)
        {
            TextView point=(TextView)mView.findViewById(R.id.individual_points);
            point.setText(points);
        }

        public void setProfileImg(String profile_img)
        {
            CircleImageView profile=(CircleImageView)mView.findViewById(R.id.individual_profile_img);
            Picasso.get().load(profile_img).placeholder(R.drawable.profile).into(profile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            sendUserToMainActivity();

        }
        return super.onOptionsItemSelected(item);

    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(FindActivity.this,MainActivity.class);
        // mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
