package wang.ApkInstallTest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainJavaActivity extends AppCompatActivity {
	private String tag = getClass().getSimpleName();
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_java);
		
		textView = findViewById(R.id.textView);
		
		needPermission();
		if (isNeedPermission == false){
			download();
		}
	}
	
	private void download(){
		final String path = SDCardTool.getPath(this , "apk");
		final String fileName = "app123.apk";
		DownloadUtil.get().download(
				"https://github.com/ismyaki/ApkInstallTest/raw/master/sample.apk",
				path,
				fileName,
				new DownloadUtil.OnDownloadListener() {
					@Override
					public void onDownloadSuccess(final String path) {
						Log.e(tag , "onDownloadSuccess " + path);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								textView.setText("onDownloadSuccess " + path);
							}
						});
						installApk(MainJavaActivity.this , path);
					}
					
					@Override
					public void onDownloading(final int progress) {
						Log.e(tag , "onDownloading " + progress);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								textView.setText("onDownloading " + progress);
							}
						});
					}
					
					@Override
					public void onDownloadFailed(final Exception e) {
						Log.e(tag , "onDownloadFailed " + e.toString());
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								textView.setText("onDownloadFailed " + e.toString());
							}
						});
					}
				}
		);
	}
	
	private final static int PERMISSIONS_REQUEST_ACCESS_COARSE = 12345;
	private boolean isNeedPermission = false;
	private String permissionString[] = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_NETWORK_STATE,
	};
	/**
	 * 確認是否要請求權限(API > 23)
	 * API < 23 一律不用詢問權限
	 */
	private boolean needPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			ArrayList<String> list = new ArrayList<String>();
			for (String s : permissionString) {
				if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
					list.add(s);
				}
			}
			if (list.size() > 0){
				isNeedPermission = true;
			}
			String p[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				p[i] = list.get(i);
			}
			if (isNeedPermission == true) {
				ActivityCompat.requestPermissions(this, p, PERMISSIONS_REQUEST_ACCESS_COARSE);
				isNeedPermission = true;
			}
			return true;
		}else{
			isNeedPermission = false;
		}
		return false;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_ACCESS_COARSE) {
			boolean isAllTrue = true;
			for (String s : permissionString) {
				if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
					isAllTrue = false;
					break;
				}
			}
			Log.e(tag , "isAllTrue " + isAllTrue);
			if (isAllTrue == false){
				isNeedPermission = true;
//				needPermission();
			}else if (isAllTrue == true) {
				isNeedPermission = false;
				download();
			}
		}
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	private void installApk(Context context , String uri){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//放在此处
		//由于没有在Activity环境下启动Activity,所以设置下面的标签
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		File apkFile = new File(uri);
		Uri apkUri = Uri.fromFile(apkFile);
		//判断版本是否是 7.0 及 7.0 以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		startActivity(intent);
		
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
//			File file= new File(uri);
//			String authority = "wang.ApkInstallTest.fileProvider";//在AndroidManifest中的android:authorities值
//			Uri apkUri = FileProvider.getUriForFile(context, authority, file);
//			Log.e(tag , "apkUri " + apkUri.toString());
//			Intent install = new Intent(Intent.ACTION_VIEW);
//			install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//			install.setDataAndType(apkUri, "application/vnd.android.package-archive");
//			context.startActivity(install);
//		} else{
//			Intent install = new Intent(Intent.ACTION_VIEW);
//			install.setDataAndType(Uri.fromFile(new File(uri)), "application/vnd.android.package-archive");
//			install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(install);
//		}
	}
}
