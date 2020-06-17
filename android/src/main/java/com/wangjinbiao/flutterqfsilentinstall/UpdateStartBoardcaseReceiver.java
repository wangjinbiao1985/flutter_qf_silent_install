package com.wangjinbiao.flutterqfsilentinstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Printer;
import android.widget.Toast;

import static com.wangjinbiao.flutterqfsilentinstall.FlutterQfSilentInstallPlugin.rootStartApk;

/**
 * Created by 24179 on 2017/11/9. 不允许更改
 */

public class UpdateStartBoardcaseReceiver extends BroadcastReceiver {
   private static final String Action =  "android.intent.action.PACKAGE_REPLACED";
    LogUtil logUtil = new LogUtil(FlutterQfSilentInstallPlugin.class, true);
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getSchemeSpecificPart();


        logUtil.d(packageName+">-<"+context.getPackageName());
        Toast.makeText(context, packageName+">-<"+context.getPackageName(), Toast.LENGTH_SHORT).show();
        if(intent.getAction().equals(Action)  && packageName.equals(/*context.getPackageName()*/"com.wangjinbiao.batterytest")){ //广播和包名确认
            Toast.makeText(context, "哇，重新安装了", Toast.LENGTH_SHORT).show();
            //            Intent activity = new Intent(context,MainActivity.class);
//            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(activity);
                      if(rootStartApk("com.wangjinbiao.batterytest", "MainActivity")){
            logUtil.d("静默安装后启动APP成功");
          }else{
            logUtil.e("静默安装后启动APP失败！！！");
            //Toast.makeText(this,"静默安装后启动APP失败！！！",Toast.LENGTH_SHORT).show();
          }
        }
    }
}
