package com.el.ariby.ui.main.menu.groupRiding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.el.ariby.R;

public class CreateGroupActivity extends AppCompatActivity {
    Button makeGroup;
    Button addFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        makeGroup = findViewById(R.id.btn_makeGroup);
        addFriend = findViewById(R.id.btn_add_rider);
        makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreateGroupActivity.this, GroupRideActivity.class);
                startActivity(intent);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGroupActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });
    }
}
