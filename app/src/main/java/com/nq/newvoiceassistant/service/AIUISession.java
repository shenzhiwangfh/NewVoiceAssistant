package com.nq.newvoiceassistant.service;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nq.newvoiceassistant.R;

public class AIUISession extends VoiceInteractionSession implements View.OnClickListener {

    private Context mContext;
    private Intent mStartIntent;

    private SoundPool mSoundPool;
    private int mSoundID;

    private View mContentView;
    private TextView mQuestion;
    private TextView mAnswer;
    private ImageView mRecord;
    //private AssistVisualizer mAssistVisualizer;

    public AIUISession(Context context) {
        super(context);
        mContext = context;

        Log.e("NewVoiceAssist", "AIUISession");
    }

    public void onShow(Bundle args, int showFlags) {
        Log.i("NewVoiceAssist", "AIUISession/onShow");

        super.onShow(args, showFlags);
        this.mStartIntent = args != null ? (Intent) args.getParcelable("intent") : null;
        if (this.mStartIntent == null) {
            onHandleScreenshot(null);
        }

        mHandler.postDelayed(runnable, 3000);
    }

    public void onHide() {
        Log.i("NewVoiceAssist", "AIUISession/onHide");

        super.onHide();
    }

    @Override
    public void onHandleAssist(Bundle data,
                               AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);

        Log.e("NewVoiceAssist", "AIUISession/onHandleAssist");

        /*
        try {
            // Fetch structured data
            JSONObject structuredData =
                    new JSONObject(content.getStructuredData());

            // Display description as Toast
            Toast.makeText(
                    mContext, structuredData.optString("description"), Toast.LENGTH_LONG
            ).show();


            Log.e("NewVoiceAssist", "AIUISession/onHandleAssist,Toast.show");
        } catch (JSONException e) {

            Log.e("NewVoiceAssist", "AIUISession/onHandleAssist:e=" + e);
            e.printStackTrace();
        }
        */
    }

    public View onCreateContentView() {
        Log.i("NewVoiceAssist", "AIUISession/onCreateContentView");

        this.mContentView = getLayoutInflater().inflate(R.layout.aiui_main, null);

        this.mQuestion = mContentView.findViewById(R.id.question);
        this.mAnswer = mContentView.findViewById(R.id.answer);
        this.mRecord = mContentView.findViewById(R.id.record);
        this.mRecord.setOnClickListener(this);

        //this.getWindow().getWindow().getDecorView();

        return mContentView;
    }

    @Override
    public void onClick(View v) {
        Log.i("NewVoiceAssist", "AIUISession/onClick");

        //mHandler.removeMessages(1);
        mHandler.removeCallbacks(runnable);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if(what == 1) {
            }

            return false;
        }
    });

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("NewVoiceAssist", "onBackPressed");
            AIUISession.super.onBackPressed();
        }
    };
}
