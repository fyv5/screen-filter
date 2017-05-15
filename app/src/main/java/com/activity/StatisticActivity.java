package com.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.MyApp;
import com.amastigote.darker.R;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by fy on 2017/5/11.
 */

public class StatisticActivity extends AppCompatActivity{
    private static final String TAG = "StatisticActivity";
    public MyApp myApp;
    public PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        pm = getPackageManager();  //获取包管理器，在这里主要通过包名获取程序的图标和程序名
        myApp = MyApp.getInstance();
        List<AndroidAppProcess> list = myApp.listApp;
        ListAdapter adapter = new ListAdapter(list,this);
        ListView listView=(ListView)this.findViewById(R.id.listView1);
        listView.setAdapter(adapter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.app_list, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    public class ListAdapter extends BaseAdapter {
        List<AndroidAppProcess> list = new ArrayList<>();
        LayoutInflater la;
        Context context;

        public ListAdapter(List<AndroidAppProcess> list ,Context context){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null)
            {
                la = LayoutInflater.from(context);
                convertView=la.inflate(R.layout.list_item, null);

                holder = new ViewHolder();
                holder.imgage=(ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            AndroidAppProcess process = list.get(position);
            Stat stat = null;
            PackageInfo packageInfo = null;
            try {
                stat = process.stat();
                packageInfo = process.getPackageInfo(context, 0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("KK:mm:ss a", Locale.getDefault());
            
            long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            long startTime = bootTime + (10 * stat.starttime());
          
            long userModeTicks = stat.utime();
            long kernelModeTicks = stat.stime();
            long percentOfTimeUserMode = 0;
            long percentOfTimeKernelMode = 0;
            String start = sdf.format(startTime);
            
            
            if ((kernelModeTicks + userModeTicks) > 0) {
                percentOfTimeUserMode = (userModeTicks * 100) / (userModeTicks + kernelModeTicks);
                percentOfTimeKernelMode = (kernelModeTicks * 100) / (userModeTicks + kernelModeTicks);
            }

            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(pm);
            long endTime = System.currentTimeMillis();
            //设置图标
            holder.imgage.setImageDrawable(appIcon);
            //设置程序名
            holder.text.setText(appName + " " +
                    "应用启动时间:" + start +
                    "  用户模式占比:" + percentOfTimeUserMode + "%  " +
                    "内核模式占比:" + percentOfTimeKernelMode + "%");
            return convertView;
        }
    }
    class ViewHolder{
        TextView text;
        ImageView imgage;
    }
}
