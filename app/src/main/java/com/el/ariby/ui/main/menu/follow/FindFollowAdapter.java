package com.el.ariby.ui.main.menu.follow;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.el.ariby.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFollowAdapter extends RecyclerView.Adapter<FindFollowAdapter.ViewHolder> implements Filterable {
    Context context;
    private ArrayList<FollowItem> mlist ;
    ArrayList<FollowItem> unFilteredlist;
    int item_layout;
    FirebaseUser auth;
    DatabaseReference Userref, Followref, Followerref;
    FirebaseDatabase database;
    Filter listFilter;

    FindFollowAdapter(Context context, ArrayList<FollowItem> list, int item_layout) {
        this.context = context;
        this.mlist = list;
        this.unFilteredlist = list;
        this.item_layout = item_layout;
        Log.d("mlist", String.valueOf(mlist));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.custom_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance().getCurrentUser();
        final String myUid = auth.getUid();
        final DatabaseReference myRef = database.getReference();
        final String uid = mlist.get(i).getUid();
        Userref = database.getReference("USER");
        Followref = database.getReference("FRIEND").child("following").child(myUid);
        Followerref = database.getReference("FRIEND").child("follower").child(uid);
        String nickname = mlist.get(i).getNick();
        String followNum = mlist.get(i).getFollwingNum();
        String followerNum = mlist.get(i).getFollowerNum();
        Glide.with(context)
                .load(mlist.get(i).getIconDrawable())
                .centerCrop()
                .into(holder.imgFollowProfile);
        holder.txtFollowNickname.setText(nickname);
        holder.txtFollowNum.setText(followNum);
        holder.txtFollowerNum.setText(followerNum);
        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addFriend.setEnabled(false);
                Toast.makeText(context, "팔로잉 되었습니다.", Toast.LENGTH_SHORT).show();
                //USER의 팔로잉 팔로워 수 증가
                Userref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //사용자의 팔로잉 수 증가
                        String followNum = dataSnapshot.child(myUid).child("followNum").getValue().toString();
                        int integer_followNum = Integer.parseInt(followNum);
                        integer_followNum = integer_followNum + 1;

                        myRef.child("USER").child(myUid).child("followNum").setValue(integer_followNum);

                        //사용자가 팔로잉 한 해당 유저의 팔로워 수 증가
                        String followerNum = dataSnapshot.child(uid).child("followerNum").getValue().toString();
                        int integer_followerNum = Integer.parseInt(followerNum);
                        integer_followerNum = integer_followerNum + 1;

                        myRef.child("USER").child(uid).child("followerNum").setValue(integer_followerNum);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //팔로워 증가
                Followerref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myRef.child("FRIEND").child("follower").child(uid).child(myUid).setValue("true");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //팔로잉 증가
                Followref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myRef.child("FRIEND").child("following").child(myUid).child(uid).setValue("true");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    mlist = unFilteredlist;
                } else {
                    ArrayList<FollowItem> filteringList = new ArrayList<>();
                    for(FollowItem name : unFilteredlist) {
                        if(name.getNick().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filteringList.add(name);
                        }
                    }
                    mlist = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mlist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mlist = (ArrayList<FollowItem>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFollowProfile;
        TextView txtFollowNickname;
        TextView txtFollowNum;
        TextView txtFollowerNum;
        Button addFriend;
        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            imgFollowProfile = itemView.findViewById(R.id.img_user_profile);
            txtFollowNickname = itemView.findViewById(R.id.txt_user_nickname);
            txtFollowNum = itemView.findViewById(R.id.txt_user_follow_num);
            txtFollowerNum = itemView.findViewById(R.id.txt_user_follower_num);
            addFriend =itemView.findViewById(R.id.add_friend);
        }
    }
}
