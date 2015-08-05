package com.owen.pDoctor.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class ZyNet {
	private static final String CHARSET = "UTF-8";
	private static final String TAG = "Http";

	private static final int POST_STARTING = 0;
	private static final int POST_STOPED = 2;

	private HttpPost httpPost = null;
	private DefaultHttpClient defaultHttpClient = null;
	private int state = POST_STARTING;

	public void closePost() {
		try {
			state = POST_STOPED;
			if (httpPost != null) {
				if (!httpPost.isAborted()) {
					httpPost.abort();
					if (defaultHttpClient != null) {
						defaultHttpClient.getConnectionManager().shutdown();
					}
				}
			}
			httpPost = null;
			defaultHttpClient = null;
		} catch (Exception e) {
			Log.e(TAG, "closePost error");
		}
	}
//public void startPost(final Context context, final String url,
	public void startPost(final String url,
			final Map<String, String> map, final INetCallBack callback) {
		closePost();
		new Thread(new Runnable() {
			public void run() {
				state = POST_STARTING;
				final String result = doPost(url, map);
				if (state == POST_STARTING) {
					state = POST_STOPED;
					
					callback.onComplete(result);
					/*((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							callback.onComplete(result);
						}
					});*/
				}
			}
		}).start();
	}

	public String doPost(String url, Map<String, String> map) {
		Log.e("do post", "url:" + url);
		try {
			defaultHttpClient = new DefaultHttpClient();
			httpPost = new HttpPost(url);
			HttpParams httpParams = new BasicHttpParams();

			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, CHARSET);
			HttpProtocolParams.setUseExpectContinue(httpParams, false);

			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
//			HttpConnectionParams.setSoTimeout(httpParams, 10000);   //最好注释掉，防止数据量大的时候，出现连接超时
			httpPost.setParams(httpParams);

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager(
					httpParams, schReg);
			defaultHttpClient = new DefaultHttpClient(conMgr, httpParams);

			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					Log.e(TAG, "参数名     " + entry.getKey());
					Log.e(TAG, "参数值     " + entry.getValue());
					BasicNameValuePair valuePair = new BasicNameValuePair(
							entry.getKey(), entry.getValue());
					nameValuePairs.add(valuePair);
				}
			}
			HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8);
			httpPost.setEntity(httpEntity);

			HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
			if (httpResponse == null) {
				Log.e(TAG, "http response result null");
				return null;
			}
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
				Log.e(TAG, "http response sucessful");
				if (result == null || "".equals(result)) {
					Log.e(TAG,
							"request was sucessful, but paser value was null or empty");
				}
//				Log.e(TAG, "respnse result:" + result);
				return result;
			} else {
				Log.e(TAG, "http response code:"
						+ httpResponse.getStatusLine().getStatusCode());
				return null;
			}
		} catch (ConnectTimeoutException e) {
			Log.e(TAG, "connection time out exception");
		} catch (ClientProtocolException e) {
			Log.e(TAG, "client protocol exception" + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "exception" + e.getMessage());
		}
		return null;
	}

}
