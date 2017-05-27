package com.wb.connect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wb.connect.adapter.PictureListRecyclerViewAdapter;
import com.wb.connect.helper.StringHelper;
import com.wb.connect.socket.TcpWorkJob;
import com.wb.connect.socket.UdpClient;
import com.wb.connect.ui.SelectDirActivity;
import com.wb.connect.ui.SettingActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private final String TAG=MainActivity.class.getName();

    @BindView(R.id.dir_txt)
    TextView dir_txt;

    @BindView(R.id.status_tv)
    TextView status_tv;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.list)
    RecyclerView recyclerView;

    PictureListRecyclerViewAdapter refreshAdapter;

    final int RESULT_SELECT_FINISHED=100;

    List<Map<String,Object>> data_map =new ArrayList<>();

    Thread socketThread,searchHostThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_main);

        ButterKnife.bind(this);

        //下拉
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.green,
                R.color.red,
                R.color.black
        );

        // Set the adapter
        Context context = this.getBaseContext();
        final Activity mActivity=this;
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));

        refreshAdapter =new PictureListRecyclerViewAdapter(data_map);

        refreshAdapter.setOnItemClickListener(new PictureListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final Map<String,Object> item) {

                /*****
                String where="";
                selectItemIndex=position;
                switch (view.getId()){
                    case R.id.editbtn:
                        where="编辑";
                        Bundle args=new Bundle();
                        args.putParcelable("edit_item",item);
                        Utils.start_Activity(mActivity, NewActivity.class,args);

                        break;
                    case R.id.setbtn:
                        where="设置签到点";
                        Utils.start_Activity(mActivity, AddresSetActivity.class,new BasicNameValuePair("activity_id", item.getId().toString()));

                        break;
                    case R.id.deletebtn:
                        where="删除";
                        dlg=new WarnTipDialog(mActivity,"确定要删除吗？");
                        dlg.setBtnOkLinstener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dlg.dismiss();
                                pd.show();
                                action_delete(mActivity,item);
                            }
                        });
                        dlg.show();
                        break;
                    case R.id.cancelbtn:
                        where="取消行程";
                        dlg=new WarnTipDialog(mActivity,"确定要取消行程吗？");
                        dlg.setBtnOkLinstener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dlg.dismiss();
                                pd.show();
                                action_cancel(mActivity,item);
                            }
                        });
                        dlg.show();
                        break;

                    default:
                        where="其他"+view.getId();
                        break;
                }
                 ****/
                Toast.makeText(mActivity,  " was clicked! ", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(refreshAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.ct_activity_select_dir_menu, menu);
        //menu.getItem(0).getSubMenu().getItem(3).setVisible(false);
        //menu.getItem(0).getSubMenu().getItem(0).setVisible(true);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_setting:
                //composeMessage();
                Intent bintent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(bintent);

                return true;
            case R.id.menu_upload:
                startTaskJob();
                return true;
            //case R.id.menu_host:
            //    searchHost();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @OnClick({R.id.dir_sel_btn})
    void handler_btn_click( View v){

        switch (v.getId()){
            case R.id.dir_sel_btn:

                Intent bintent = new Intent(MainActivity.this, SelectDirActivity.class);

                String bsay = "Hello, this is B speaking";
                bintent.putExtra("listenB", bsay);
                startActivityForResult(bintent,0);

                break;
            default:

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case RESULT_SELECT_FINISHED:
                Bundle b=data.getExtras();
                String file_dir=b.getString("file_dir");

                dir_txt.setText(file_dir);
                onRefresh();
                break;
            default:
                break;
        }
    }

    private void getImages(String file_dir){

        if(StringUtils.isEmpty(file_dir)){

            if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            return;
        }

        File dirFile=new File(file_dir);

        if(!dirFile.exists()||!dirFile.isDirectory()){

            if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this.getApplication(),"参数不正确",Toast.LENGTH_SHORT);

            return;
        }

        data_map.clear();
        File file[] = listValidFiles(dirFile);

        for(File f:file){

            Log.i(TAG,"dir:"+f.getAbsolutePath());

            Map<String,Object> item=new HashMap<>();

            item.put("file_name",f.getName());
            item.put("file_path",f.getAbsolutePath());
            item.put("file",f);//
            data_map.add(item);
        }
        refreshAdapter.notifyDataSetChanged();

        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

    }


    private File[] listValidFiles(File file) {
        return file.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                File file2 = new File(dir, filename);
                return (filename.contains(".png") || filename.contains(".jpg") || file2
                        .isDirectory())
                        && !file2.isHidden()
                        && !filename.startsWith(".");

            }
        });
    }

    @Override
    public void onRefresh() {

        getImages(dir_txt.getText().toString());

    }

    private void searchHost(){

        if(searchHostThread!=null){
            searchHostThread.interrupt();
        }
        searchHostThread=new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    String localIp=StringHelper.getWifiIpaddr();
                    Log.d(TAG,"hostAddress:"+ localIp);

                    //find server
                    if(StringUtils.isEmpty(localIp)){

                        Log.d(TAG,"get wifi fail");
                        return;
                    }

                    //if(!localIp.startsWith("192.168")){
                    //    Log.d(TAG,"可能不在同一个网段，不执行");
                    //    return;
                    //}

                    String[] otherIps =localIp.split("\\.");

                    Integer lastNum=Integer.parseInt(otherIps[3]);

                    for(int i=0;i<255;i++){

                        if(i==lastNum) continue;

                        String searchIp=otherIps[0]+"."+otherIps[1]+"."+otherIps[2]+"."+i;
                        InetAddress RemoteIP =StringHelper.convert2IpAddress(searchIp);
                        String resp= UdpClient.sendUdp(RemoteIP,10024,"hi");

                        Log.d(TAG,"searchHost "+searchIp+":"+resp);

                        if(StringUtils.isNotEmpty(resp)){

                            Log.d(TAG,"find server ip"+searchIp);

                            StringHelper.saveKey("server_host",searchIp);
                            break;
                        }

                    }

                }catch (Exception ex){

                    Log.e(TAG,"searchHost fail:",ex);
                }

            }
        });
        searchHostThread.start();

    }

    private void startTaskJob(){

        if(data_map.size()==0){

            status_tv.setText("没有待上传文件");
            return;
        }
        if(socketThread!=null&&!socketThread.isInterrupted()){

            return;

        }
        socketThread=new Thread(new TcpWorkJob(data_map));
        socketThread.start();

    }

}
