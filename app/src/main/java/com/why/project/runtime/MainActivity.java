package com.why.project.runtime;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MultPermission2();
		clickPermission(findViewById(R.id.btn_getpermission));

		findViewById(R.id.btn_opensetting).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//打开应用权限设置界面
				PermissionSettingPage.start(MainActivity.this,false);
			}
		});
	}

	/**只有一个运行时权限申请的情况*/
	private void onePermission(){
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this); // where this is an Activity instance
		rxPermissions.request(Manifest.permission.READ_PHONE_STATE) //权限名称，多个权限之间逗号分隔开
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean granted) throws Exception {
						Log.e(TAG, "{accept}granted=" + granted);//执行顺序——1【多个权限的情况，只有所有的权限均允许的情况下granted==true】
						if (granted) { // 在android 6.0之前会默认返回true
							// 已经获取权限
							Toast.makeText(MainActivity.this, "已经获取权限", Toast.LENGTH_SHORT).show();
							String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();//根据不同的手机设备返回IMEI，MEID或者ESN码
							Toast.makeText(MainActivity.this, "{accept}deviceId=" + deviceId, Toast.LENGTH_SHORT).show();
						} else {
							// 未获取权限
							Toast.makeText(MainActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
						}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						Log.e(TAG,"{accept}");//可能是授权异常的情况下的处理
					}
				}, new Action() {
					@Override
					public void run() throws Exception {
						Log.e(TAG,"{run}");//执行顺序——2
					}
				});
	}

	/**同时请求多个权限（合并结果）的情况*/
	private void MultPermission(){
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this); // where this is an Activity instance
		rxPermissions.request(Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_EXTERNAL_STORAGE)//权限名称，多个权限之间逗号分隔开
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean granted) throws Exception {
						Log.e(TAG, "{accept}granted=" + granted);//执行顺序——1【多个权限的情况，只有所有的权限均允许的情况下granted==true】
						if (granted) { // 在android 6.0之前会默认返回true
							// 已经获取权限
							Toast.makeText(MainActivity.this, "已经获取权限", Toast.LENGTH_SHORT).show();
						} else {
							// 未获取权限
							Toast.makeText(MainActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
						}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						Log.e(TAG,"{accept}");//可能是授权异常的情况下的处理
					}
				}, new Action() {
					@Override
					public void run() throws Exception {
						Log.e(TAG,"{run}");//执行顺序——2
					}
				});
	}

	/**同时请求多个权限（分别获取结果）的情况*/
	private void MultPermission2(){
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this); // where this is an Activity instance
		rxPermissions.requestEach(Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_EXTERNAL_STORAGE)//权限名称，多个权限之间逗号分隔开
				.subscribe(new Consumer<Permission>(){
					@Override
					public void accept(Permission permission) throws Exception {
						Log.e(TAG, "{accept}permission.name=" + permission.name);
						Log.e(TAG, "{accept}permission.granted=" + permission.granted);
						if(permission.name.equals(Manifest.permission.READ_PHONE_STATE) && permission.granted){
							// 已经获取权限
							Toast.makeText(MainActivity.this, "已经获取权限", Toast.LENGTH_SHORT).show();
							String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();//根据不同的手机设备返回IMEI，MEID或者ESN码
							Toast.makeText(MainActivity.this, "{accept}deviceId=" + deviceId, Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/**条件触发获取权限(结合RxBinding使用)的情况*/
	private void clickPermission(View view){
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this); // where this is an Activity instance
		RxView.clicks(view)
				.compose(rxPermissions.ensure(Manifest.permission.CAMERA))
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean granted) {
						Log.e(TAG, "{accept}granted=" + granted);//【多个权限的情况，只有所有的权限均允许的情况下granted==true】
						if (granted) { // 在android 6.0之前会默认返回true
							// 已经获取权限
							Toast.makeText(MainActivity.this, "已经获取CAMERA权限", Toast.LENGTH_SHORT).show();
						} else {
							// 未获取权限
							Toast.makeText(MainActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
}
