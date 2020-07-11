package com.mumba.funto.Comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mumba.funto.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class Comments_Adapter extends RecyclerView.Adapter<Comments_Adapter.CustomViewHolder > {

    public Context context;
    private Comments_Adapter.OnItemClickListener listener;
    private ArrayList<Comment_Get_Set> dataList;



    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, Comment_Get_Set item, View view);
    }

    public Comments_Adapter(Context context, ArrayList<Comment_Get_Set> dataList, Comments_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public Comments_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_layout,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        Comments_Adapter.CustomViewHolder viewHolder = new Comments_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }


    @Override
    public void onBindViewHolder(final Comments_Adapter.CustomViewHolder holder, final int i) {
        final Comment_Get_Set item= dataList.get(i);


        holder.username.setText(item.first_name+" "+item.last_name);

        try{
        Picasso.with(context).
                load(item.profile_pic)
                .resize(50,50)
                .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                .into(holder.user_pic);

       }catch (Exception e){

       }

        if(item.comments.equals("571f986a41ac9038929c209cf8ee9fb7")){
            //kiss emoji
            holder.message.setVisibility(View.INVISIBLE);
            holder.picturecomment.setVisibility(View.VISIBLE);
            holder.picturecomment.setImageDrawable(context.getResources().getDrawable(R.drawable.kissemoji));

        }else if(item.comments.equals("081e528ed37ac4d406f5b67f9b5a47f9")){
            //love emoji
            holder.message.setVisibility(View.INVISIBLE);
            holder.picturecomment.setVisibility(View.VISIBLE);
            holder.picturecomment.setImageDrawable(context.getResources().getDrawable(R.drawable.loveemoji));
        }else if(item.comments.equals("18753f6e0c8e21ea0b76617eb316eac3")){
            //line emoji
            holder.message.setVisibility(View.INVISIBLE);
            holder.picturecomment.setVisibility(View.VISIBLE);
            holder.picturecomment.setImageDrawable(context.getResources().getDrawable(R.drawable.lineemoji));
        }else if(item.comments.equals("eb144d0ffe2a27735d886df0ec84243a")){
            //heart emoji
            holder.message.setVisibility(View.INVISIBLE);
            holder.picturecomment.setVisibility(View.VISIBLE);
            holder.picturecomment.setImageDrawable(context.getResources().getDrawable(R.drawable.heartemoji));
        }else if(item.comments.equals("5e711433876950000c41b5f89a5d910f")){
            //celebrate emoji
            holder.message.setVisibility(View.INVISIBLE);
            holder.picturecomment.setVisibility(View.VISIBLE);
            holder.picturecomment.setImageDrawable(context.getResources().getDrawable(R.drawable.celibrateemoji));
        }else if(item.comments.equals("d96edbaa3a8f7533763d64722072780d")){
            //like emoji
            holder.message.setVisibility(View.INVISIBLE);
            holder.picturecomment.setVisibility(View.VISIBLE);
            holder.picturecomment.setImageDrawable(context.getResources().getDrawable(R.drawable.likeemoji));
        }else {
            holder.message.setText(item.comments);
        }



        holder.bind(i,item,listener);

   }



    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView username,message;
        ImageView user_pic;
        ImageView picturecomment;


        public CustomViewHolder(View view) {
            super(view);

            username=view.findViewById(R.id.username);
            user_pic=view.findViewById(R.id.user_pic);
            message=view.findViewById(R.id.message);
            picturecomment = view.findViewById(R.id.imageview);

        }

        public void bind(final int postion,final Comment_Get_Set item, final Comments_Adapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(postion,item,v);
                }
            });

        }


    }





}