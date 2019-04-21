package com.example.leena.mypills;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class clickPostActivity extends AppCompatActivity {

    private CircleImageView profImgClick;
    private EditText editClick;
    private TextView yourPointsView,userPointsView,userName,userAge,userPhone;
    private Button payBtn;
  //  private FirebaseAuth firebaseAuth;
    String currentUserId,yourPoints,userPoints;

    private String userKey;

    private DatabaseReference clickUserRef,yourkeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

//        firebaseAuth=FirebaseAuth.getInstance();
//        currentUserId=firebaseAuth.getCurrentUser().getUid();

        profImgClick=(CircleImageView)findViewById(R.id.user_pic_click);
        editClick=(EditText)findViewById(R.id.edit_points_click);
        yourPointsView=(TextView)findViewById(R.id.your_points_click);
        userPointsView=(TextView)findViewById(R.id.user_points_click);
        payBtn=(Button)findViewById(R.id.pay_btn_click);
        userName=(TextView)findViewById(R.id.user_name_click);
        userAge=(TextView)findViewById(R.id.click_age);
        userPhone=(TextView)findViewById(R.id.click_phone);

        userKey=getIntent().getExtras().get("userKey").toString();
        currentUserId=getIntent().getExtras().getString("currentUserId").toString();

        clickUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
        clickUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String img="https://firebasestorage.googleapis.com/v0/b/mypills-c2d76.appspot.com/o/profile%20images%2Fprofile.png?alt=media&token=3f2ea0bc-d32c-42fb-8f7c-7ad0506ef74e";
                if(dataSnapshot.hasChild("profile_img"))
                {
                    img=dataSnapshot.child("profile_img").getValue().toString();
                }

                String name=dataSnapshot.child("fullname").getValue().toString();

                userPoints=dataSnapshot.child("points").getValue().toString();


                userName.setText(name);
                userPointsView.setText(userPoints);
                Picasso.get().load(img).placeholder(R.drawable.profile).into(profImgClick);

                String age=dataSnapshot.child("age").getValue().toString();
                String phone=dataSnapshot.child("username").getValue().toString();
                userPhone.setText(phone);
                userAge.setText(age);


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        yourkeyRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        yourkeyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yourPoints=dataSnapshot.child("points").getValue().toString();

                yourPointsView.setText(yourPoints);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yourp,userp;
                yourp=Integer.parseInt(yourPoints);
                userp=Integer.parseInt(userPoints);

                int payPoints=Integer.parseInt(editClick.getText().toString());
                editClick.setText("");

                if(payPoints<0 || payPoints>yourp)
                {
                    Toast.makeText(clickPostActivity.this,"Insufficient balance",Toast.LENGTH_SHORT).show();
                }
                else{
                    userp=userp+payPoints;
                    yourp=yourp-payPoints;

                    yourkeyRef.child("points").setValue(String.valueOf(yourp));
                    clickUserRef.child("points").setValue(String.valueOf(userp));

                    Toast.makeText(clickPostActivity.this,"Payment successful",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

//    @Override
//    protected void onStart() {
//
//        super.onStart();
//
//        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
//        if(currentUser==null)
//        {
//           // SendUserToLoginActivity();
//            // Toast.makeText(this,"current user is null",Toast.LENGTH_SHORT).show();
//        }
//        else{
//           // checkUserExistance();
//
//
//        }
//    }
}
