package com.example.wabisabi.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Activity.users.User;
import com.example.wabisabi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantsAdd extends RecyclerView.Adapter<AdapterParticipantsAdd.HolderParticipantsAdd>{

    private Context context;
    private ArrayList<User> userArrayList;
    private String groupId, myGroupRole;

    public AdapterParticipantsAdd(Context context, ArrayList<User> userArrayList, String groupId, String myGroupRole) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_participants_add, parent, false);

        return new HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {
        User user = userArrayList.get(position);
        String name = user.getUsername();
        String image = user.getProfileImage();
        String uid = user.getUid();

        holder.nameTv.setText(name);
        try {
            Picasso.get().load(image).placeholder(R.drawable.username_icon).into(holder.avatarIv);
        } catch (Exception e) {
            holder.avatarIv.setImageResource(R.drawable.username_icon);
        }


        checkIfAlreadyExists(user, holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String hisprevousRole = "" + snapshot.child("role").getValue();
                                    String[] options;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Options");

                                    if (myGroupRole.equals("creator")) {
                                        if (hisprevousRole.equals("admin")) {


                                            if (hisprevousRole.equals("admin")) {
                                                options = new String[]{"Remove Admin", "Remove User"};
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0) {
                                                            removeAdin(user);
                                                        } else {
                                                            removeParticipant(user);
                                                        }
                                                    }

                                                }).show();
                                            }
                                        } else if (hisprevousRole.equals("participant")) {
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        makeAdin(user);
                                                    } else {
                                                        removeParticipant(user);
                                                    }
                                                }

                                            }).show();
                                        }
                                    } else if (myGroupRole.equals("admin")) {

                                        if (hisprevousRole.equals("creator")) {
                                            Toast.makeText(context, "Creator of group...", Toast.LENGTH_SHORT).show();
                                        } else if (hisprevousRole.equals("admin")) {
                                            options = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        removeAdin(user);
                                                    } else {
                                                        removeParticipant(user);
                                                    }
                                                }
                                            }).show();
                                        } else if (hisprevousRole.equals("participant")) {
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        makeAdin(user);
                                                    } else {
                                                        removeParticipant(user);
                                                    }
                                                }
                                            }).show();
                                        }
                                    }
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setTitle("add Participants")
                                            .setMessage("Add this user in this group?").setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Addparticipant(user);
                                                }
                                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void Addparticipant(User user) {
        String timeStamp=""+System.currentTimeMillis();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",user.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timeStamp);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(user.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Added successfully ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeAdin(User user) {

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("role","participant");
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(user.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The user no longer a admin....", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeAdin(User user) {
        String timeStamp=""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("role","admin");
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(user.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The user now is admin", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void removeParticipant(User user) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Group");
        reference.child(groupId).child("Participants").child(user.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The user now is admin", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkIfAlreadyExists(User user, HolderParticipantsAdd holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String hisrole = "" + snapshot.child("role").getValue();
                            holder.statusTv.setText(hisrole);
                        } else {
                            holder.statusTv.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class HolderParticipantsAdd extends RecyclerView.ViewHolder {
        private ImageView avatarIv;
        private TextView nameTv;
        private TextView statusTv;

        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.profile_iv_participants);
            nameTv = itemView.findViewById(R.id.usernameChatTv_participants);
            statusTv = itemView.findViewById(R.id.statusChatTv_participants);
        }
    }
}