package com.nq.newvoiceassistant.service;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.business.speech.AIUIAgent;
import com.iflytek.business.speech.AIUIConstant;
import com.iflytek.business.speech.AIUIEvent;
import com.iflytek.business.speech.AIUIListener;
import com.iflytek.business.speech.AIUIMessage;
import com.iflytek.business.speech.PackageUtils;
import com.iflytek.business.speech.SpeechIntent;
import com.iflytek.business.speech.SpeechServiceUtil;
import com.iflytek.business.speech.SynthesizerListener;
import com.nq.newvoiceassistant.LogUtils;
import com.nq.newvoiceassistant.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AIUISession extends VoiceInteractionSession implements View.OnClickListener {

    private final static String TAG = "AIUISession";

    private Context mContext;
    private Intent mStartIntent;

    private SoundPool mSoundPool;
    private int mSoundID;

    private View mContentView;
    private TextView mQuestion;
    private TextView mAnswer;
    private ImageView mRecord;
    //private AssistVisualizer mAssistVisualizer;

    private SpeechServiceUtil mSpeechService;
    private AIUIAgent mAIUIAgent;
    private JSONObject mAIUIParams;
    private final static int SAMPLE_RATE = 16000;

    public AIUISession(Context context) {
        super(context);
        mContext = context;
    }

    public void onShow(Bundle args, int showFlags) {
        LogUtils.e(TAG, "onShow");

        super.onShow(args, showFlags);
        this.mStartIntent = args != null ? (Intent) args.getParcelable("intent") : null;
        if (this.mStartIntent == null) {
            onHandleScreenshot(null);
        }

        mHandler.postDelayed(runnable, 3000);
    }

    public void onHide() {
        LogUtils.e(TAG, "onHide");
        super.onHide();
    }

    @Override
    public void onHandleAssist(Bundle data,
                               AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);

        LogUtils.e(TAG, "onHandleAssist");

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
        LogUtils.e(TAG, "onCreateContentView");

        final String appid = mContext.getString(R.string.appid);
        final String appkey = mContext.getString(R.string.appkey);
        PackageUtils.getInstance(mContext).setAppid(appid);
        PackageUtils.getInstance(mContext).setAppKey(appkey);

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundID = mSoundPool.load(mContext, R.raw.sound, 1);

        //mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //registerGPS();
        //registerNetwork();
        //AppUtil.loadAsync(this);
        // init SpeechService
        Intent serviceIntent = new Intent();
        serviceIntent.putExtra(SpeechIntent.SERVICE_LOG_ENABLE, true);
        mSpeechService = new SpeechServiceUtil(mContext, mInitListener, serviceIntent);

        this.mContentView = getLayoutInflater().inflate(R.layout.aiui_main, null);
        this.mQuestion = mContentView.findViewById(R.id.question);
        this.mAnswer = mContentView.findViewById(R.id.answer);
        this.mRecord = mContentView.findViewById(R.id.record);
        this.mRecord.setOnClickListener(this);
        return mContentView;
    }

    @Override
    public void onClick(View v) {
        LogUtils.e(TAG, "onClick");
        mHandler.removeCallbacks(runnable);

        //startVoiceNlp();
        startRecognize();
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
            }

            return false;
        }
    });

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.e(TAG, "onBackPressed");
            AIUISession.super.onBackPressed();
        }
    };

    private final SpeechServiceUtil.ISpeechInitListener mInitListener = new SpeechServiceUtil.ISpeechInitListener() {
        @Override
        public void onSpeechInit(int code) {
            if (code == 0) {
                // Initialize Synthesizer
                Intent intent2 = new Intent();
                intent2.putExtra(SpeechIntent.ARG_RES_TYPE, SpeechIntent.RES_FROM_ASSETS);
                mSpeechService.initSynthesizerEngine(mTTSListener, intent2);

                // Local Wakeup
//                String[] res_files = {"ivw/ivModel_zhimakaimen.mp3"};
//                final Intent i = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putInt(SpeechIntent.ARG_RES_TYPE, SpeechIntent.RES_FROM_CLIENT);
//                bundle.putStringArray(SpeechIntent.EXT_IVW_FILES, res_files);
//                i.putExtra(SpeechIntent.ENGINE_WAKE_DEC, bundle);

                // init AIUI
                Bundle params = getAIUIParams();
                mAIUIAgent = mSpeechService.createAIUIAgent(params, mAIUIListener);

                LogUtils.e(TAG, "onSpeechInit");
            }
        }

        @Override
        public void onSpeechUninit() {
        }
    };

    private Bundle getAIUIParams() {
        if (mAIUIParams == null) {
            try {
                JSONObject joRoot = new JSONObject();
                {
                    JSONObject joInteract = new JSONObject();
                    joInteract.put(AIUIConstant.KEY_INTERACT_TIMEOUT, "60000");
                    joInteract.put(AIUIConstant.KEY_RESULT_TIMEOUT, "5000");
                    joRoot.put("interact", joInteract);
                }
                {
                    JSONObject joGlobal = new JSONObject();
                    joGlobal.put(AIUIConstant.KEY_SCENE, "main");
                    joRoot.put("global", joGlobal);
                }
                {
                    JSONObject joVad = new JSONObject();
                    joVad.put(AIUIConstant.KEY_VAD_ENABLE, "1");
                    joVad.put(AIUIConstant.KEY_VAD_BOS, "5000");
                    joVad.put(AIUIConstant.KEY_VAD_EOS, "1000");
                    joRoot.put("vad", joVad);
                }
                {
                    JSONObject joIat = new JSONObject();
                    joRoot.put("iat", joIat);
                }
                {
                    JSONObject joAsr = new JSONObject();
                    joAsr.put(AIUIConstant.KEY_SCENE, "call");
                    joAsr.put(AIUIConstant.KEY_THRESHOLD, "0");
                    joRoot.put("asr", joAsr);
                }
                {
                    JSONObject joSpeech = new JSONObject();
                    joSpeech.put(AIUIConstant.KEY_SAMPLE_RATE, "" + SAMPLE_RATE);
                    joSpeech.put(AIUIConstant.KEY_DATA_SOURCE, "sdk");
                    joSpeech.put(AIUIConstant.KEY_INTENT_ENGINE_TYPE, AIUIConstant.ENGINE_TYPE_CLOUD);
                    joRoot.put("speech", joSpeech);
                }
                {
//                    JSONObject joAudioParams = new JSONObject();
//                    // 生效用户级动态实体
//                    joAudioParams.put("pers_param", "{\"uid\":\"\"}");
//                    // 经纬度，gcj02坐标系，昆明
//                    // 经度
//                    joAudioParams.put("msc.lng", "102.834193");
//                    // 纬度
//                    joAudioParams.put("msc.lat", "24.873906");
//                    joRoot.put("audioparams", joAudioParams);
                }
                mAIUIParams = joRoot;
            } catch (JSONException e) {
                Log.e(TAG, "", e);
                return null;
            }
        }
        String params = mAIUIParams.toString();

        Bundle bundle = new Bundle();
        bundle.putString(AIUIConstant.KEY_PARAMS, params);
        return bundle;
    }

    /**
     * tts listener
     */
    private final SynthesizerListener.Stub mTTSListener = new SynthesizerListener.Stub() {
        @Override
        public void onProgressCallBack(int i) {
        }

        @Override
        public void onPlayBeginCallBack() {
        }

        @Override
        public void onPlayCompletedCallBack(int i) {
            /*
            if (pending != null) {
                pending.run();
                pending = null;
            }
            */
        }

        @Override
        public void onInterruptedCallback() {
        }

        @Override
        public void onInit(int i) {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };

    private final AIUIListener mAIUIListener = new AIUIListener() {
        @Override
        public void onEvent(AIUIEvent event) {
            LogUtils.e(TAG, "event.eventType=" + event.eventType);

            switch (event.eventType) {
                case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                    //isReady = true;
                    //uploadContacts();
                    break;
                case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                    //isReady = false;
                    break;
                case AIUIConstant.EVENT_STATE: {
                    if (AIUIConstant.STATE_IDLE == event.arg1) {
                    } else if (AIUIConstant.STATE_READY == event.arg1) {
                    } else if (AIUIConstant.STATE_WORKING == event.arg1) {
                    }
                    break;
                }
                case AIUIConstant.EVENT_WAKEUP:
                    break;
                case AIUIConstant.EVENT_START_RECORD:
                    //if (callback != null)
                    //    callback.updateState(true);
                    break;
                case AIUIConstant.EVENT_STOP_RECORD:
                    //if (callback != null)
                    //    callback.updateState(false);
                    break;
                case AIUIConstant.EVENT_VAD:
                    switch (event.arg1) {
                        case AIUIConstant.VAD_VOL:
                            break;
                        case AIUIConstant.VAD_BOS:
                            break;
                        case AIUIConstant.VAD_EOS:
                            break;
                        case AIUIConstant.VAD_BOS_TIMEOUT:
                            break;
                        default:
                            break;
                    }
                    break;
                case AIUIConstant.EVENT_RESULT:

                    String bizParams = event.bundle.getString(AIUIConstant.KEY_INFO);
                    //mAudioManager.setBluetoothScoOn(false);
                    //mAudioManager.stopBluetoothSco();
                    LogUtils.e(TAG, "bizParams=" + bizParams);

                    /*
                    AIUIResult result = AIUIJsonParser.parse(event, bizParams);
                    if (result != null) {
                        ActionManager.execute(AIUIService.this, result);
                        if (callback != null)
                            callback.updateItem(result);
                    }
                    */
                    break;
                case AIUIConstant.EVENT_ERROR:
                    String info = event.bundle.getString(AIUIConstant.KEY_INFO);
                    //mAudioManager.setBluetoothScoOn(false);
                    //mAudioManager.stopBluetoothSco();
                    Log.e(TAG, "AIUI error" + info);
                    break;
                case AIUIConstant.EVENT_CMD_RETURN:
                    //TODO
                    if (AIUIConstant.CMD_SYNC == event.arg1) {
                        // 同步数据类型
                        int dtype = event.bundle.getInt("sync_dtype");
                        if (AIUIConstant.SYNC_DATA_SCHEMA == dtype) {
                            // 给出调用同步数据时设置的标签，用于区分请求。
                            String tag = event.bundle.getString(AIUIConstant.KEY_TAG);
                            // arg2是错误码
                            if (0 == event.arg2) { // 同步成功
                                // 注：上传成功并不表示数据打包成功，打包成功与否应以同步状态查询结果为准
                                // ，数据只有打包成功后才能正常使用
                            } else {
                            }
                        }
                    } else if (AIUIConstant.CMD_QUERY_SYNC_STATUS == event.arg1) {
                        int syncType = event.bundle.getInt("sync_dtype");
                        if (AIUIConstant.SYNC_DATA_QUERY == syncType) {
                            if (0 == event.arg2) {
                            } else {
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    public void startRecognize() {
        //stopSpeakAndPlay();
        //mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        //mAudioManager.setBluetoothScoOn(true);
        //mAudioManager.startBluetoothSco();
        mSoundPool.play(mSoundID, 1.0f, 1.0f, 1, 0, 1.0f);

        AIUIMessage aiuiMsg = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null);
        mAIUIAgent.sendMessage(aiuiMsg);
        aiuiMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, null);
        mAIUIAgent.sendMessage(aiuiMsg);
        //Kaidi MAYBE need to clean history. AIUI MAY have mechanism to manage context based actions
//        aiuiMsg = new AIUIMessage(AIUIConstant.CMD_CLEAN_DIALOG_HISTORY, 0, 0, null);
//        mAIUIAgent.sendMessage(aiuiMsg);
        String params = "data_type=audio";
        Bundle bundle = new Bundle();
        bundle.putString(AIUIConstant.KEY_PARAMS, params);
        aiuiMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, bundle);
        mAIUIAgent.sendMessage(aiuiMsg);
    }
}
