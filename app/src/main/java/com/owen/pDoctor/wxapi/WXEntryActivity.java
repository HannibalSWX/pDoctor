package com.owen.pDoctor.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.owen.pDoctor.R;
import com.owen.pDoctor.util.AppConstants;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 注册微信sdk
		api = WXAPIFactory.createWXAPI(this, AppConstants.Weixin_APP_ID, false);
		api.registerApp(AppConstants.Weixin_APP_ID);
		api.handleIntent(getIntent(), this);
	}

	/**
	*  微信发送请求到第三方应用时，会回调到该方法
	*/
	@Override
	public void onReq(BaseReq req) {
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	/**
	* 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	*/
	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK : // 分享成功
				result = R.string.errcode_success;
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL :// 取消分享
				result = R.string.errcode_cancel;
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED :// // 分享失败
				result = R.string.errcode_deny;
				break;
			default :
				result = R.string.errcode_unknown;
				break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}
}
