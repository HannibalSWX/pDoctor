package com.owen.pDoctor.chat;

import java.util.ArrayList;
import java.util.List;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.HistoryOfMessagesBean;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.model.MyGroupChildBean;
import com.owen.pDoctor.util.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMsgViewAdapter extends BaseAdapter {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();
//	private List<ChatMsgEntity> coll;
	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private Context ctx;
	Resources res;

	private MyGroupChildBean myChildBean;
	
	private MessagesBean msgBean;

	private ImageLoader mImageLoader;
	
	private String uid, loginName, userName, myHeadImg, comHeadImg;
	
	private List<HistoryOfMessagesBean> hisList = new ArrayList<HistoryOfMessagesBean>();

	public ChatMsgViewAdapter(Context context, List<HistoryOfMessagesBean> hList, MessagesBean msBean, MyGroupChildBean mBean) {
		ctx = context;
		this.hisList = hList;
		msgBean = msBean;
		myChildBean = mBean;
		mInflater = LayoutInflater.from(context);
		res = context.getResources();

		SharedPreferences sp = ctx.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		loginName = sp.getString("loginName", "");
		userName = sp.getString("userName", "");
		myHeadImg = sp.getString("head_img", "");
		
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(ctx);
		}
	}

	@Override
	public int getCount() {
		return hisList.size();
	}

	@Override
	public Object getItem(int position) {
		return hisList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public int getItemViewType(int position) {
		HistoryOfMessagesBean entity = hisList.get(position);
		if (!entity.getTo_user_type().equals("1")) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(final int postion, View converView, ViewGroup parent) {
		final HistoryOfMessagesBean entity = hisList.get(postion);
		ViewHolder viewHolder = null;
		if (converView == null) {
			if (!entity.getTo_user_type().equals("1")) {
				converView = mInflater.inflate(R.layout.chat_left, null);
			} else {
				converView = mInflater.inflate(R.layout.chat_right, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.iv_userhead = (ImageView) converView.findViewById(R.id.iv_userhead);
			viewHolder.tvSendTime = (TextView) converView.findViewById(R.id.tv_sendtime);
			viewHolder.tvContent = (TextView) converView.findViewById(R.id.tv_chatcontent);
			viewHolder.tvTime = (TextView) converView.findViewById(R.id.tv_time);
			viewHolder.tvUserName = (TextView) converView.findViewById(R.id.tv_username);
			converView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}
		viewHolder.tvSendTime.setText(entity.getSend_time());
		if (entity.getText().contains(".amr")) {
			viewHolder.tvTime.setText(entity.getSend_time());
			viewHolder.tvContent.setText("");
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
		} else {
			// viewHolder.tvContent.setText(entity.getText());
			// viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
			SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(ctx,
					entity.getText());
			viewHolder.tvContent.setText(spannableString);
			viewHolder.tvTime.setText(entity.getSend_time());
		}
		viewHolder.tvContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (entity.getText().contains(".amr")) {
					playMusic(android.os.Environment.getExternalStorageDirectory() + "/" + entity.getText());
				} else {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					// String id=entity.getText().toString();
					bundle.putInt("ID", postion);
					intent.putExtras(bundle);
					intent.setClass(ctx, PhotoActivity.class);
					ctx.startActivity(intent);
				}
			}
		});

		if (msgBean != null) {
			comHeadImg = msgBean.getImgurl();
		} else {
			comHeadImg = myChildBean.getHeadimgurl();
		}
		// // 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		viewHolder.iv_userhead.setImageResource(R.drawable.icon_username);
		viewHolder.iv_userhead.setScaleType(ImageView.ScaleType.FIT_XY);
		if (!entity.getTo_user_type().equals("1")) {
			if (msgBean != null) {
				viewHolder.tvUserName.setText(msgBean.getNickname());
			} else {
				viewHolder.tvUserName.setText(myChildBean.getNickname());
			}
			if (comHeadImg == null || "".equals(comHeadImg)) {
				viewHolder.iv_userhead.setImageResource(R.drawable.icon_username);
			} else {
				// 需要显示的网络图片
				mImageLoader.DisplayImage(comHeadImg, viewHolder.iv_userhead, false);
			}
		} else {
			viewHolder.tvUserName.setText(userName);
			if (myHeadImg == null || "".equals(myHeadImg)) {
				viewHolder.iv_userhead.setImageResource(R.drawable.icon_username);
			} else {
				// 需要显示的网络图片
				mImageLoader.DisplayImage(myHeadImg, viewHolder.iv_userhead, false);
			}
		}
		return converView;
	}

	static class ViewHolder {
		private ImageView iv_userhead;
		public TextView tvSendTime, tvUserName, tvContent, tvTime;
	}

	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stop() {
	}
}
