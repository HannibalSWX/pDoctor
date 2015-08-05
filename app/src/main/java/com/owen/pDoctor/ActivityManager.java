package com.owen.pDoctor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

public class ActivityManager {

	public static List<Activity> activityList = new ArrayList<Activity>();

	public static void removeActivity(Activity activity) {

		activityList.remove(activity);

		activity.getClass().getName();
	}

	public static void addActivity(Activity activity) {

		activityList.add(activity);

	}

	public static void finishApplication() {

		// APP.getInstance().stopService();

		for (Activity activity : activityList) {

			activity.finish();

		}

		android.os.Process.killProcess(android.os.Process.myPid());

	}

	public static void backHome() {

		for (Activity activity : activityList) {
			Log.i("activity name", "activity name : "
					+ activity.getClass().getName());
			if (!(activity.getClass().getName()
					.equals("com.ysb.environmental.ui.MainActivity"))) {
				activity.finish();
			}
		}
	}

}
