package com.el.ariby.ui.main.menu.groupRiding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.el.ariby.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupRideActivity extends AppCompatActivity {
    Button createGroup;
    GroupRidingAdapter adapter;
    ArrayList<GroupRideItem> groupRideItems = new ArrayList<GroupRideItem>();

    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference myGroupRef;
    DatabaseReference userRef;
    final ArrayList<String> myGroupList = new ArrayList<>();
    int leaderNo = 0;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        adapter = new GroupRidingAdapter();
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view) ;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        createGroup = findViewById(R.id.btn_createG);

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("GROUP_RIDING");
        myGroupRef = database.getReference("GROUP_RIDING_MEMBERS");
        final String myUid = mUser.getUid();

        myGroupRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String groupName = dataSnapshot1.getKey();
                        Log.d("내 그룹 : ", groupName);
                        myGroupList.add(groupName);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            int index = 0;
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i=0 ; i < myGroupList.size(); i++ ) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String groupName = snapshot.getKey();
                        if (myGroupList.get(index).equals(groupName)) {
                            Log.e("hhhh", groupName);
                            String startPoint = snapshot.child("startPoint").child("name").getValue().toString();
                            String endPoint = snapshot.child("endPoint").child("name").getValue().toString();
                            String leader = snapshot.child("members").child("0").child("nickname").getValue().toString();
                            groupRideItems.add(new GroupRideItem(groupName, startPoint, endPoint, leader));
                            index++;
                            break;
                        }
                    }
                }
                recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), groupRideItems, R.layout.activity_group));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupRideActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    public class GroupRidingAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            CustomGroupRide view = new CustomGroupRide(context);
            return view;
        }

       public void addItem(GroupRideItem item){ groupRideItems.add(item); }
    }
}


