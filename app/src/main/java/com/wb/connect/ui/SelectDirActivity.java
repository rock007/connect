package com.wb.connect.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.wb.connect.MainActivity;
import com.wb.connect.R;
import com.wb.connect.adapter.ChooseListRecyclerViewAdapter;
import com.wb.connect.adapter.PictureListRecyclerViewAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDirActivity extends AppCompatActivity {

    private final String TAG=SelectDirActivity.class.getName();

    @BindView(R.id.list)
    RecyclerView recyclerView;

    ActionBar actionBar;

    ChooseListRecyclerViewAdapter recyclerViewAdapter;

    List<Map<String,Object>> data_map =new ArrayList<>();

    private String select_dir="";

    final int RESULT_SELECT_FINISHED=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_select_dir);

        ButterKnife.bind(this);

        actionBar =getSupportActionBar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        actionBar.setTitle("择选路径");

        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = 40;
        imageView.setLayoutParams(layoutParams);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler_seleced();
            }
        });

        actionBar.setCustomView(imageView);

        /***
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
            }
        });
         *****/

        final Activity mActivity=this;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        recyclerViewAdapter =new ChooseListRecyclerViewAdapter(data_map);


        recyclerViewAdapter.setOnItemClickListener(new ChooseListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final Map<String,Object> item) {

                Log.d(TAG,"select item: "+item.get("file_name"));
                select_dir=item.get("file_path").toString();
            }
        });

        recyclerView.setAdapter(recyclerViewAdapter);
        getDirs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handler_seleced(){

        Intent aintent = new Intent(SelectDirActivity.this, MainActivity.class);

        aintent.putExtra("file_dir", select_dir);
        this.setResult(RESULT_SELECT_FINISHED,aintent);
        finish();
    }

    private void getDirs(){

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            File[] files = listValidFiles(Environment.getExternalStorageDirectory());

            if(files==null) return;

            for(File f:files){

                Log.i(TAG,"dir:"+f.getAbsolutePath());

                Map<String,Object> item=new HashMap<>();

                item.put("file_name",f.getName());
                item.put("file_path",f.getAbsolutePath());
                item.put("file",f);//???
                data_map.add(item);
            }

            recyclerViewAdapter.notifyDataSetChanged();
        }

    }

    private File[] listValidFiles(File file) {
        return file.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                File file2 = new File(dir, filename);
                return (file2
                        .isDirectory())
                        && !file2.isHidden()
                        && !filename.startsWith(".");

            }
        });
    }
}
