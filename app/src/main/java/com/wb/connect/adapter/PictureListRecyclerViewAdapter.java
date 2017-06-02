package com.wb.connect.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wb.connect.MyApp;
import com.wb.connect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sam on 2017/5/23.
 */

public class PictureListRecyclerViewAdapter  extends RecyclerView.Adapter<PictureListRecyclerViewAdapter.ViewHolder> {

    private List<Map<String,Object>> records;

    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, Map<String,Object> item);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PictureListRecyclerViewAdapter(List<Map<String,Object>> items) {
        records = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ct_image_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {

        final Map<String,Object> item=records.get(position);

        holder.tv_title.setText(item.get("file_name").toString());

        holder.tv_title.setVisibility(View.INVISIBLE);

        if((Boolean) item.get("is_uploaded")){

            holder.tv_tips.setText("已上传");

        }else{

            holder.tv_tips.setText("未上传");
        }

        //大小调整会使布局乱掉
        Glide.with(MyApp.applicationContext).load(item.get("file_path").toString())
                //.crossFade()
                .placeholder(R.mipmap.pic_holder)
                .centerCrop()
                //.override(100, 100)
                .dontAnimate()
                .into((ImageView) holder.iv_image);
        /****
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });


        // Setup the click listener
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                    listener.onItemClick(v, position,oneActivity);
            }
        });

        holder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                    listener.onItemClick(v, position,oneActivity);
            }
        });

        holder.setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                    listener.onItemClick(v, position,oneActivity);
            }
        });

        holder.cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                    listener.onItemClick(v, position,oneActivity);
            }
        });

        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null)
                    listener.onItemClick(v, position,oneActivity);
            }
        });
         ****/

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        public TextView tv_title;

        @BindView(R.id.tv_tips)
        public  TextView tv_tips;

        @BindView(R.id.iv_image)
        public  ImageView iv_image;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + this.toString() + "'";
        }

    }

    /**
     * add items
     * @param data
     * @return
     */
    public  Integer addData(List<Map<String,Object>> data){

        this.records.addAll(data);

        return  this.records.size();
    }

    public  Integer clearData(){

        this.records=new ArrayList<>();

        return  this.records.size();
    }

}
