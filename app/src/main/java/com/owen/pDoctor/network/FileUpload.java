package com.owen.pDoctor.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.util.Log;

public class FileUpload {

	private static final String TAG = "上传文件";

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public static String postUploadFile(String actionUrl,
			Map<String, String> params, Map<String, File> files) {
		// StringBuilder sb2 = null;
		StringBuffer sb2 = null;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		try {
			URL uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(30 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				Log.e(TAG, "参数 名:" + entry.getKey());
				Log.e(TAG, "参数值" + entry.getValue());
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());

			// 发送文件数据
			if (files != null) {
				for (Map.Entry<String, File> file : files.entrySet()) {
					Log.e(TAG, "文件名 :" + file.getKey());
					Log.e(TAG, "文件路径:" + file.getValue());
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
//					sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\""
//							+ file.getValue().getName() + "\"" + LINEND);
					sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ file.getKey() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());
					Log.d("AAA", "--------sb1---------"	+ sb1.toString().getBytes());
//					FileInputStream is = new FileInputStream(new File(file.getValue(), CHARSET));
//					InputStream is =new BufferedInputStream(new FileInputStream(file.getValue()));
					InputStream is = new FileInputStream(file.getValue());
					Log.i("文件大小", file.getValue().length() + "");
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}

					is.close();
					outStream.write(LINEND.getBytes());
				}

			}
			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			Log.e(TAG, "code:" + res);
			if (res == 200) {
				InputStream in = conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				sb2 = new StringBuffer();
				String data = "";
				while ((data = br.readLine()) != null) {
					sb2.append(data + "\n");
				}
			} else {
				return null;
			}
			outStream.close();
			conn.disconnect();

		} catch (Exception e) {
			Log.e(TAG, "IOException:" + e.getMessage());

		}
		if (sb2 == null) {
			return null;
		}
		Log.e(TAG, "返回结果：" + sb2.toString());
		return sb2.toString();

	}
}
