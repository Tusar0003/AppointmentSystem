package com.example.no0ne.appointmentsystem.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.no0ne.appointmentsystem.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by no0ne on 2/23/18.
 */

public class TeacherViewHolder extends RecyclerView.ViewHolder {

    public View mView;

    public TeacherViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setName(String name) {
        TextView userNameTextView = mView.findViewById(R.id.text_view_user_name);
        userNameTextView.setText(name);
    }

    public void setDepartment(String department) {
        TextView departmentTextView = mView.findViewById(R.id.text_view_dept);
        departmentTextView.setText(department);
    }

    public void setImage(String image) {
        CircleImageView imageView = mView.findViewById(R.id.circle_image_view);
        Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.man).into(imageView);
    }
}
