package com.wangjinbiao.flutterqfsilentinstall;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterqfsilentinstallPlugin */
public class FlutterQfSilentInstallPlugin implements FlutterPlugin, MethodCallHandler {

  private MethodChannel channel;
  private SuCommand suCommand;
  private final String TAG = getClass().getSimpleName();


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "silent_install");
    channel.setMethodCallHandler(this);
  }

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "silent_install");
    channel.setMethodCallHandler(new FlutterQfSilentInstallPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch(call.method) {
      case "install":
        String path = call.argument("path");
        //boolean bInstall = install(path);
        result.success(installSilent(path));
        break;
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  public int installSilent(String apkPath) {
    final String cmd = "pm install -r " + apkPath;
    suCommand = new SuCommand();
    new Thread(new Runnable() {
      @Override
      public void run() {
        int ret = suCommand.execRootCmdSilent(cmd);
        if (ret == 0) {
          if(rootStartApk("com.wangjinbiao.batterytest", "MainActivity")){
            Log.d(TAG,"静默安装后启动APP成功");
          }else{
            Log.e(TAG,"静默安装后启动APP失败！！！");
            //Toast.makeText(this,"静默安装后启动APP失败！！！",Toast.LENGTH_SHORT).show();
          }
        } else {
          Log.e(TAG,"静默安装失败！！！");
        }
      }
    }).start();
    return 0;
  }

  /**
   * 执行具体的静默安装逻辑，需要手机ROOT。
   * @param apkPath
   *          要安装的apk文件的路径
   * @return 安装成功返回true，安装失败返回false。
   */
  public boolean install(String apkPath) {
    boolean result = false;
    DataOutputStream dataOutputStream = null;
    BufferedReader errorStream = null;
    try {
      // 申请su权限
      Process process = Runtime.getRuntime().exec("su");
      dataOutputStream = new DataOutputStream(process.getOutputStream());
      // 执行pm install命令
      String command = "pm install -r " + apkPath + "\n";
      dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
      dataOutputStream.flush();
      dataOutputStream.writeBytes("exit\n");
      dataOutputStream.flush();
      int nWaitFor = process.waitFor();
      boolean bWaitFor = returnResult(nWaitFor);
      if (bWaitFor) {
        result = true;
        if(rootStartApk("com.wangjinbiao.batterytest", "MainActivity")){
          Log.d("TAG","静默安装后启动APP成功");
        }else{
          Log.e("TAG","静默安装后启动APP失败！！！");
          //Toast.makeText(this,"静默安装后启动APP失败！！！",Toast.LENGTH_SHORT).show();
        }
      } else {
        result = false;
      }
      errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      String msg = "";
      String line;
      // 读取命令的执行结果
      while ((line = errorStream.readLine()) != null) {
        msg += line;
      }
      Log.d("TAG", "install msg is " + msg);
      // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
      if (!msg.contains("Failure")) {
        result = true;

      }
    } catch(Exception e) {
      Log.e("TAG", e.getMessage(),e);
    } finally {
      try{
        if (dataOutputStream != null) {
          dataOutputStream.close();
        }
        if (errorStream != null) {
          errorStream.close();
        }
      } catch (IOException e) {
        Log.e("TAG", e.getMessage(),e);
      }
    }

    return result;
  }

  public static boolean rootStartApk(String packageName,String activityName){
    boolean isSuccess = false;
    String cmd = "am start -n " + packageName + "/" + activityName + " \n";
    Process process = null;
    try {
        process = Runtime.getRuntime().exec(cmd);
        int value = process.waitFor();
        return returnResult(value);
    } catch (Exception e) {
        e.printStackTrace();
    } finally{
        if(process!=null){
            process.destroy();
        }
    }
    return isSuccess;
}

  /**
   * root下执行cmd的返回值
   * */
  private static boolean returnResult(int value){
    // 代表成功
    if (value == 0) {
      return true;
    } else if (value == 1) { // 失败
      return false;
    } else { // 未知情况
      return false;
    }
  }
}
