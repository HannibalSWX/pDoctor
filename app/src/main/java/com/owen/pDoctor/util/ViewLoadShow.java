package com.owen.pDoctor.util;

import com.owen.pDoctor.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 弹出动画效果
 * Created by 张伟 on 14-10-13.
 */
public class ViewLoadShow {
    private AnimationDrawable an;
    private Context context;
    private Dialog dialog;
    private ImageView iv_image;
    private TextView tv_text;
    private boolean isShowing=false;
    public ViewLoadShow(Context context){
        this.context = context;
        this.isShowing=false;
    }

    /**
     * 开始动画
     */
    public void show(String name,boolean hide) {
        if(dialog!=null&&dialog.isShowing()){
            return;
        }
        View window = LayoutInflater.from(context).inflate(R.layout.view_load, null);
        dialog = new Dialog(context,R.style.home_dialog);
        dialog.setContentView(window);
		tv_text = (TextView) window.findViewById(R.id.tv_text);
        tv_text.setText(name);
        iv_image = (ImageView) window.findViewById(R.id.iv_image);
        iv_image.setBackgroundResource(R.anim.load_list_animation);
        an = (AnimationDrawable) iv_image.getBackground();
        an.start();
        if(dialog!=null){
            dialog.show();
            this.isShowing=true;
            /*使弹出框不消失*/
            if(hide){
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            }
        }
    }

    /**
     * 结束动画
     */
    public void stop(){
        if(dialog!=null&&an!=null)
            if(an.isRunning()){
                dialog.cancel();
                an.stop();
                an=null;
                dialog = null;
            }
        this.isShowing=false;
    }

    public boolean isShowing()
    {
        return this.isShowing;
    }
}
