package com.tukla.www.tukla;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserListAdapter extends BaseAdapter {

    private Context context;
    private List<User> data;
    private List<User> filteredUsers;

    public UserListAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
        this.filteredUsers = data;
    }

    @Override
    public int getCount() {
        if(filteredUsers.isEmpty())
            return 1;
        return filteredUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (data.isEmpty()) {
            convertView = LayoutInflater.from(context).inflate(R.layout.empty_list, parent, false);
            return convertView;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewAddress = convertView.findViewById(R.id.textViewAddress);
        TextView textViewPhone = convertView.findViewById(R.id.textViewPhone);
        TextView textViewPlateNumber = convertView.findViewById(R.id.textViewPlateNumber);
        Button button = convertView.findViewById(R.id.button);
        Button button2 = convertView.findViewById(R.id.button2);
        LinearLayout linearLayout = convertView.findViewById(R.id.layout_admin_userlist);

        User data = (User) filteredUsers.get(position);
        textViewName.setText(data.getFullname());
        textViewAddress.setText(data.getAddress());
        textViewPhone.setText(data.getPhone());
        textViewPlateNumber.setText(data.getDriver().getPlateNumber());
        StorageReference imgRef = storageRef.child("images/" + data.getUserID() + ".jpg");
        StorageReference licRef = storageRef.child("licenses/" + data.getUserID() + ".jpg");
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(imageView.getContext())
                        .load(uri)
                        .fitCenter()
                        .into(imageView);
            }
        });

        //        if(data.getIsDriver()) {
        //            DatabaseReference driverRef = database.getReference("drivers");
        //            textViewPlateNumber.setText(data.getFullname());
        //        }
        button.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);

        if (data.getIsVerified()) {
            button.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
        }
        if(data.getIsRejected()) {
            button.setVisibility(View.VISIBLE);
            button2.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("users").child(data.getUserID()).child("isVerified").setValue(true);
                database.getReference().child("users").child(data.getUserID()).child("isRejected").setValue(false);
                //Toast.makeText(parent.getContext(), "User Verified!", Toast.LENGTH_SHORT);
                showDialog("User Verified!");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("users").child(data.getUserID()).child("isRejected").setValue(true);
                showDialog("User Rejected!");
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout for the dialog box
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.custom_dialog_imageview, null);
                // get the ImageView from dialog_box.xml
                ImageView dialogImageView = dialogView.findViewById(R.id.admin_id_img);
                // set the image for ImageView
                licRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(dialogImageView.getContext())
                                .load(uri)
                                .fitCenter()
                                .into(dialogImageView);
                    }
                });
                // create the dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);
                // show the dialog box
                builder.show();
            }
        });

        return convertView;
    }

    public void showDialog(String message) {

        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this.context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void search(String query) {
        filteredUsers.clear();
        if(query.isEmpty()){
            filteredUsers.addAll(data);
        } else {
            query = query.toLowerCase();
            for (User user : data) {
                if (user.getFullname().contains(query) || user.getDriver().getPlateNumber().contains(query)) {
                    filteredUsers.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
