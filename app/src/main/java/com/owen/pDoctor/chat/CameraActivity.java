package com.owen.pDoctor.chat;

import com.owen.pDoctor.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class CameraActivity extends Activity{
	ImageView camera;
	Object imgUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);
		init();
	}
	private void init() {
		camera=(ImageView) findViewById(R.id.camera);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		imgUrl = bundle.get("camera");
		//��ȡ���ص���ݣ���ת��ΪͼƬ��ʽ
		//Bitmap bitmap = (Bitmap)msg.get("data");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bitmap =BitmapFactory.decodeFile(imgUrl.toString(),options);
		camera.setImageBitmap(bitmap);
	}
	public void btnClick(View v){
		switch (v.getId()) {
		case R.id.btn_back_chat:
			CameraActivity.this.finish();
			break;
		case R.id.cancel_camera:
			CameraActivity.this.finish();
			break;
		case R.id.confirm_camera:
			//�������������Ҫ������ݵ��������������������������ʾ
			//Toast.makeText(CameraActivity.this, "����ͼƬ", 1).show();
			Intent intent=new Intent();
			intent.putExtra("imgUrl", imgUrl.toString());
			CameraActivity.this.setResult(0x000002,intent);
			CameraActivity.this.finish();
			break;
		}
	}
	
}
