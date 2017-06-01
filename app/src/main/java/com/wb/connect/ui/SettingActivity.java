package com.wb.connect.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wb.connect.MainActivity;
import com.wb.connect.R;
import com.wb.connect.helper.StringHelper;
import com.wb.connect.socket.UdpClient;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    private final String TAG=SettingActivity.class.getName();

    ProgressDialog pd;

    ActionBar actionBar;

    @BindView(R.id.tv_host)
    TextView tv_host;

    @BindView(R.id.tv_lochost)
    TextView tv_lochost;

    Thread searchHostThread;

    String resp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_setting);
        ButterKnife.bind(this);

        actionBar =getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setTitle("参数");

        pd = new ProgressDialog(this);
        pd.setMessage("查询中...");

        tv_host.setText(StringHelper.getKey("server_host"));

    }

    @OnClick(R.id.host_layout)
    void handler_host_layout_click(){
        searchHost();
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

    private void searchHost(){

        if(searchHostThread!=null){
            searchHostThread.interrupt();
        }
        pd.show();
        final Activity where =this;
        searchHostThread=new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    final String localIp=StringHelper.getWifiIpaddr();
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

                    where.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tv_lochost.setText(localIp);
                            tv_host.setText("");
                            StringHelper.saveKey("server_host","");
                        }
                    });
                    for(int i=0;i<255;i++){

                        if(i==lastNum) continue;

                        resp="";
                        final String searchIp="192.168.0.107";//otherIps[0]+"."+otherIps[1]+"."+otherIps[2]+"."+String.valueOf(i);
                        InetAddress RemoteIP =StringHelper.convert2IpAddress(searchIp);

                        Log.d(TAG,"searchHost "+searchIp);

                        where.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                pd.setMessage("正在查询 ip: "+searchIp+"    ,返回:"+resp);
                            }
                        });

                        int times=0;

                        while(times<1&&StringUtils.isEmpty(resp)){

                            try{
                                new  UdpClient(RemoteIP,10024).send("hi");

                                resp="ok";
                            }catch (Exception ex){

                                Log.i(TAG,"UdpClient check ip:",ex);
                                resp="";
                            }

                            Log.d(TAG,"times:"+times+"  resp:"+resp);

                            times++;
                        }

                        if(StringUtils.isNotEmpty(resp)){

                            Log.d(TAG,"find server ip:"+searchIp);

                            StringHelper.saveKey("server_host",searchIp);

                            where.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_host.setText(searchIp);
                                }
                            });
                            break;
                        }

                    }

                }catch (Exception ex){

                    Log.e(TAG,"searchHost fail:",ex);
                }
                pd.dismiss();

            }
        });
        searchHostThread.start();

    }

}
