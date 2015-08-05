package com.owen.pDoctor.view;


import java.io.File;
import java.io.IOException;

import com.owen.pDoctor.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class RecordButton extends Button  {
    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSavePath(String path) {
        mFileName = path;
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    private String mFileName = null;

    private OnFinishedRecordListener finishedListener;

    private static final int MIN_INTERVAL_TIME = 2000;// 2s
    private long startTime;
    private boolean isTimeOutFinish=false;//是否是超出时间的录音结束


    /**
     * 取消语音发送
     */
    private Dialog recordIndicator=null;

    private static int[] res = { R.drawable.icon_voice, R.drawable.icon_voice2,
            R.drawable.icon_voice, R.drawable.icon_voice2,R.drawable.icon_voice,R.drawable.icon_voice2 };

    private static ImageView iv_voice_indicator;

    private static TextView tv_leaveTime;

    private MediaRecorder recorder;

    private ObtainDecibelThread thread;

    private Handler volumeHandler;

    public final static int   MAX_TIME =60;//一分钟

    private void init() {
        volumeHandler = new ShowVolumeHandler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mFileName == null)
            return false;

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //setText("松开结束");
                initDialogAndStartRecord();
                break;
            case MotionEvent.ACTION_UP:
                //this.setText("按住录音");
                if(!isTimeOutFinish){
                    finishRecord();
                }
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                cancelRecord();
//                Toast.makeText(getContext(), "cancel", 1).show();
                break;
        }

        return true;
    }

    private void initDialogAndStartRecord() {
        this.setText("松开结束");
        startTime = System.currentTimeMillis();
        recordIndicator = new Dialog(getContext(),R.style.home_dialog);
        View window = LayoutInflater.from(getContext()).inflate(R.layout.dialog_voice_indicator, null);
        recordIndicator.setContentView(window);
        tv_leaveTime= (TextView) window.findViewById(R.id.tv_leaveTime);
        iv_voice_indicator= (ImageView) window.findViewById(R.id.iv_voice_indicator);
        recordIndicator.setOnDismissListener(onDismiss);
        startRecording();
        recordIndicator.show();
    }

    private void finishRecord() {
        this.setText("按住录音");
        stopRecording();
        recordIndicator.dismiss();
        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
            File file = new File(mFileName);
            file.delete();
            return;
        }
        if (finishedListener != null)
            finishedListener.onFinishedRecord(mFileName,(int) (intervalTime/1000));
    }

    private void cancelRecord() {
        stopRecording();
        recordIndicator.dismiss();
        Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
        File file = new File(mFileName);
        file.delete();
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setAudioChannels(1);
        recorder.setAudioEncodingBitRate(4000);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //recorder.setVideoFrameRate(4000);
        recorder.setOutputFile(mFileName);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        thread = new ObtainDecibelThread();
        thread.start(); 
    }
    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;
        public void exit() {
            running = false;
        }
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                int x = recorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    if (f < 20)
                        volumeHandler.sendEmptyMessage(0);
                    else if (f < 26)
                        volumeHandler.sendEmptyMessage(1);
                    else if (f < 32)
                        volumeHandler.sendEmptyMessage(2);
                    else if (f < 38)
                        volumeHandler.sendEmptyMessage(3);
                    else if (f < 44)
                        volumeHandler.sendEmptyMessage(4);
                    else
                        volumeHandler.sendEmptyMessage(5);
                }
            }
        }
    }

    private OnDismissListener onDismiss = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            iv_voice_indicator.setImageResource(res[msg.what]);
            //录音时间进行控制
            long intervalTime = System.currentTimeMillis() - startTime;
            if(intervalTime>=50000 && intervalTime<=60000){
                tv_leaveTime.setVisibility(VISIBLE);
                tv_leaveTime.setText("还可以说"+(60-intervalTime/1000)+"秒");
            }else if(intervalTime>60000){
                isTimeOutFinish=true;
                finishRecord();
            }
        }
    }

    public interface OnFinishedRecordListener {
        public void onFinishedRecord(String audioPath,int time);
    }
}
