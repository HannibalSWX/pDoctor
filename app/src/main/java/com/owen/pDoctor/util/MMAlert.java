package com.owen.pDoctor.util;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;

public final class MMAlert {

	private static final int VALUSE = 100;
	public interface OnAlertSelectId {
		void onClick(int whichButton);
	}

	private MMAlert() {

	}

	public static AlertDialog showAlert(final Context context,
			final String title, final View view, final String ok,
			final String cancel, final DialogInterface.OnClickListener lOk,
			final DialogInterface.OnClickListener lCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton(ok, lOk);
		builder.setNegativeButton(cancel, lCancel);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, VALUSE, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
