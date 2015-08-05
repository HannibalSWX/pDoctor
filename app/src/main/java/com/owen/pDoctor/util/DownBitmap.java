package com.owen.pDoctor.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * ���ط���������ͼƬ
 * */
public class DownBitmap {

	private DownBitmap() {
	}

	private static DownBitmap my = null;

	public static synchronized DownBitmap getInstance() {
		if (my == null)
			my = new DownBitmap();
		return my;
	}

	// ��ȡ����ͼƬ����ʱ���ص���
	public InputStream getInputStream(String Biturl) {
		// ʹ��get����ʽ��ȡͼƬ��Դ
		HttpGet get = new HttpGet(Biturl);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		try {
			HttpResponse hr = httpClient.execute(get);
			if (hr.getStatusLine().getStatusCode() == 200) {
				return hr.getEntity().getContent();// �õ����������ص�������
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// ����response���ӴӶ���ȡ״̬��

		return null;
	}

}
