package com.tukla.www.tukla;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class UserListAdapter extends BaseAdapter {

    private Context context;
    private List<User> data;

    public UserListAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewAddress = convertView.findViewById(R.id.textViewAddress);
        TextView textViewPhone = convertView.findViewById(R.id.textViewPhone);
        TextView textViewPlateNumber = convertView.findViewById(R.id.textViewPlateNumber);
        Button button = convertView.findViewById(R.id.button);

        User data = (User) getItem(position);
        textViewName.setText(data.getFullname());
        textViewAddress.setText(data.getAddress());
        textViewPhone.setText(data.getPhone());

        if(data.getIsDriver()) {
            DatabaseReference driverRef = database.getReference("drivers");
            textViewPlateNumber.setText(data.getFullname());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("users").child(data.getUserID()).child("isVerified").setValue(true);
                Toast.makeText(parent.getContext(), "User Verified!", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }
}
