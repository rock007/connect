package com.wb.connect.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wb.connect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sam on 2017/5/23.
 */

public class ChooseListRecyclerViewAdapter extends RecyclerView.Adapter<ChooseListRecyclerViewAdapter.ViewHolder> {

    private List<Map<String,Object>> records;

    private static OnItemClickListener listener;

    private int lastCheckedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, Map<String, Object> item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChooseListRecyclerViewAdapter(List<Map<String,Object>> items) {
        records = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ct_choose_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {

        final Map<String,Object> item=records.get(position);
        holder.mItem = item;
        holder.select_rb.setText(item.get("file_path").toString());
        holder.select_rb.setChecked(position == lastCheckedPosition);

        holder.select_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckedPosition = position;
                notifyItemRangeChanged(0, records.size());

                listener.onItemClick(v, position,item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.select_rb)
        public RadioButton select_rb;

        public Map<String,Object> mItem;

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
