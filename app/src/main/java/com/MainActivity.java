package com;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.activity.AboutActivity;
import com.activity.ExerciseActivity;
import com.activity.StatisticActivity;
import com.activity.TrainActivity;
import com.amastigote.darker.R;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.model.DarkerNotification;
import com.model.DarkerSettings;
import com.service.AlermService;
import com.view.ColorSeekBar;
import com.service.ScreenFilterService;

import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * Created by fy on 2017/5/11.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    public DarkerSettings currentDarkerSettings;
    public DarkerNotification darkerNotification;
    public TextView brightness_indicator;
    public TextView alpha_indicator;
    public CircularSeekBar circleSeekBar_brightness;
    public CircularSeekBar circleSeekBar_alpha;
    public ColorSeekBar colorSeekBar;
    public Switch aSwitch;
    public Switch bSwitch;
    public Switch sw_auto;
    public AppCompatButton appCompatButton;
    public Intent intent;
    public View view;
    public BroadcastReceiver broadcastReceiver;

    public TextView tv_statistic;
    public TextView tv_exercise;
    public TextView tv_train;

    public EditText et_limit;
    public Button bt_ensure;
    public Button bt_cancel;
    public SharedPreferences sp;

    public boolean isServiceRunning = false;

    public Intent alerm;
    public MyApp myApp;
    public Thread thread = null;
    public boolean isAuto = false;
    public boolean isTime = false;

    @Override
    protected void onDestroy() {
        doCleanBeforeExit();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int hour = t.hour;    // 0-23
        Log.d(TAG, "Time类hour "+hour);
        if (hour >= 22 && hour <7 || true){
            isTime = true;
        }

        myApp = MyApp.getInstance();
        if(thread == null){
            thread = new Thread(new getProcessThread());
            thread.start();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleMarginStart(100);
        toolbar.setLogo(R.mipmap.night_128);
        setSupportActionBar(toolbar);

        currentDarkerSettings = new DarkerSettings();
        DarkerSettings.initializeContext(getApplicationContext());
        checkPermissions();
        darkerNotification = new DarkerNotification(MainActivity.this);
        darkerNotification.updateStatus(isServiceRunning);

        circleSeekBar_brightness = (CircularSeekBar) findViewById(R.id.cp_brightness_circleSeekBar);
        circleSeekBar_alpha = (CircularSeekBar) findViewById(R.id.cp_alpha_circleSeekBar);
        brightness_indicator = (TextView) findViewById(R.id.cp_brightness_indicator);
        alpha_indicator = (TextView) findViewById(R.id.cp_alpha_indicator);
        colorSeekBar = (ColorSeekBar) findViewById(R.id.cp_colorSeekBar);
        aSwitch = (Switch) findViewById(R.id.cp_useColor_switch);
        bSwitch = (Switch) findViewById(R.id.cp_useBrightness_switch);
        sw_auto = (Switch) findViewById(R.id.auto_switch);
        appCompatButton = (AppCompatButton) findViewById(R.id.cm_toggle_button);
        tv_statistic = (TextView) findViewById(R.id.statistic);
        tv_exercise = (TextView) findViewById(R.id.exercise);
        tv_train = (TextView) findViewById(R.id.train);
        et_limit = (EditText) findViewById(R.id.editText1);
        bt_ensure = (Button) findViewById(R.id.ensure);
        bt_cancel = (Button) findViewById(R.id.cancel);
        sp = getSharedPreferences("actm", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        et_limit.setText(""+sp.getLong("limit",0L));
        alerm = new Intent(MainActivity.this, AlermService.class);

        bt_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int limit=Integer.parseInt(et_limit.getText().toString());
                editor.putLong("limit",limit);
                editor.commit();
                et_limit.setFocusable(false);
                et_limit.setFocusableInTouchMode(false);
                et_limit.setClickable(false);
                startService(alerm);
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_limit.setFocusableInTouchMode(true);
                et_limit.setFocusable(true);
                et_limit.requestFocus();
                et_limit.setClickable(true);
                int limit=0;
                et_limit.setText("0");
                editor.putLong("limit",limit);
                editor.commit();
                stopService(alerm);
            }
        });

        restoreLatestSettings();

        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isServiceRunning) {
                    collectCurrentDarkerSettings();
                    setButtonState(true);
                    isServiceRunning = true;
                } else {
                    ScreenFilterService.removeScreenFilter();
                    setButtonState(false);
                    isServiceRunning = false;
                }
                darkerNotification.updateStatus(isServiceRunning);
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DarkerNotification.PRESS_BUTTON.equals(intent.getAction())) {
                    if (!isServiceRunning) {
                        collectCurrentDarkerSettings();
                        setButtonState(true);
                        isServiceRunning = true;
                    } else {
                        ScreenFilterService.removeScreenFilter();
                        setButtonState(false);
                        isServiceRunning = false;
                    }
                    darkerNotification.updateStatus(isServiceRunning);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DarkerNotification.PRESS_BUTTON);
        registerReceiver(broadcastReceiver, intentFilter);


        isAuto = sp.getBoolean("isAuto", false);
        Log.d(TAG, "share中的isAuto "+isAuto);
        initViews();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlphaAnimation alphaAnimation_0 = new AlphaAnimation(0, 1);
                    alphaAnimation_0.setDuration(300);
                    colorSeekBar.startAnimation(alphaAnimation_0);
                    colorSeekBar.setVisibility(View.VISIBLE);
                    currentDarkerSettings.setUseColor(true);
                } else {
                    AlphaAnimation alphaAnimation_1 = new AlphaAnimation(1, 0);
                    alphaAnimation_1.setDuration(300);
                    colorSeekBar.startAnimation(alphaAnimation_1);
                    colorSeekBar.setVisibility(View.INVISIBLE);
                    currentDarkerSettings.setUseColor(false);
                }
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });

        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlphaAnimation alphaAnimation_0 = new AlphaAnimation(0, 1);
                    alphaAnimation_0.setDuration(300);
                    circleSeekBar_brightness.startAnimation(alphaAnimation_0);
                    circleSeekBar_brightness.setVisibility(View.VISIBLE);
                    brightness_indicator.setText(String.valueOf((int) circleSeekBar_brightness.getProgress()));
                    currentDarkerSettings.setUseBrightness(true);
                } else {
                    AlphaAnimation alphaAnimation_1 = new AlphaAnimation(1, 0);
                    alphaAnimation_1.setDuration(300);
                    circleSeekBar_brightness.startAnimation(alphaAnimation_1);
                    circleSeekBar_brightness.setVisibility(View.INVISIBLE);
                    brightness_indicator.setText(R.string.auto_brightness);
                    currentDarkerSettings.setUseBrightness(true);
                }
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });

        sw_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    isAuto = true;
                } else {
                    isAuto = false;
                }
                editor.putBoolean("isAuto", isAuto);
                editor.commit();
                Log.d(TAG, "执行修改isAuto操作 "+isAuto);
                if(isAuto && isTime){
                    collectCurrentDarkerSettings();
                    setButtonState(true);
                    isServiceRunning = true;
                    darkerNotification.updateStatus(isServiceRunning);
                }
            }
        });

        circleSeekBar_brightness.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                brightness_indicator.setText(String.valueOf((int) progress));
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        circleSeekBar_alpha.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                alpha_indicator.setText(String.valueOf((int) progress));
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int i2) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });
        tv_statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatisticActivity.class);
                startActivity(intent);
            }
        });
        tv_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                startActivity(intent);
            }
        });
        tv_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        sw_auto.setChecked(isAuto);
        if (isAuto && isTime) {
            Intent intent = new Intent();
            intent.setAction(DarkerNotification.PRESS_BUTTON);
            sendBroadcast(intent);
        } else {
            setButtonState(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about)
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        else if (id == R.id.action_restoreDefaultSettings) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("重置配置面板")
                    .setMessage("将覆盖您的偏好配置并恢复为推荐配置")
                    .setPositiveButton("确认重置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentDarkerSettings = DarkerSettings.getDefaultSettings();
                            if (isServiceRunning) {
                                doRestore();
                                collectCurrentDarkerSettings();
                            } else
                                doRestore();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setButtonState(boolean isChecked) {
        if (isChecked) {
            final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    ContextCompat.getColor(this, R.color.toggle_button_off),
                    ContextCompat.getColor(this, R.color.toggle_button_on));
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int trans_color = (int) valueAnimator.getAnimatedValue();
                    appCompatButton.setSupportBackgroundTintList(
                            ColorStateList.valueOf(trans_color));
                }
            });
            valueAnimator.start();
            appCompatButton.setText(getResources().getString(R.string.is_on));
        } else {
            final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    ContextCompat.getColor(this, R.color.toggle_button_on),
                    ContextCompat.getColor(this, R.color.toggle_button_off));
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int trans_color = (int) valueAnimator.getAnimatedValue();
                    appCompatButton.setSupportBackgroundTintList(
                            ColorStateList.valueOf(trans_color));
                }
            });
            valueAnimator.start();
            appCompatButton.setText(getResources().getString(R.string.is_off));
        }
    }

    private void doRestore() {
        {
            boolean isCurrentServiceRunning = false;
            if (isServiceRunning) {
                isCurrentServiceRunning = true;
                isServiceRunning = false;
            }
            circleSeekBar_brightness.setProgress(currentDarkerSettings.getBrightness() * 100);
            circleSeekBar_alpha.setProgress(currentDarkerSettings.getAlpha() * 100);
            if (isCurrentServiceRunning)
                isServiceRunning = true;
        }

        if (aSwitch.isChecked()) {
            aSwitch.setChecked(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            colorSeekBar.startAnimation(alphaAnimation);
            colorSeekBar.setVisibility(View.INVISIBLE);
        }

        if (bSwitch.isChecked()) {
            bSwitch.setChecked(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            circleSeekBar_brightness.startAnimation(alphaAnimation);
            circleSeekBar_brightness.setVisibility(View.INVISIBLE);
            brightness_indicator.setText(R.string.auto_brightness);
        }
        invalidateOptionsMenu();
    }

    private void collectCurrentDarkerSettings() {
        currentDarkerSettings.setBrightness(circleSeekBar_brightness.getProgress() / 100);
        currentDarkerSettings.setAlpha(circleSeekBar_alpha.getProgress() / 100);
        currentDarkerSettings.setUseColor(aSwitch.isChecked());
        currentDarkerSettings.setUseBrightness(bSwitch.isChecked());
        currentDarkerSettings.setColorBarPosition(colorSeekBar.getColorPosition());
        currentDarkerSettings.setColor(colorSeekBar.getColor());
        currentDarkerSettings.saveCurrentSettings();
        if (isServiceRunning)
            ScreenFilterService.updateScreenFilter(currentDarkerSettings);
        else {
            try {
                ScreenFilterService.activateScreenFilter(currentDarkerSettings);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    private void restoreLatestSettings() {
        currentDarkerSettings = DarkerSettings.getCurrentSettings();
        circleSeekBar_brightness.setProgress(currentDarkerSettings.getBrightness() * 100);
        circleSeekBar_alpha.setProgress(currentDarkerSettings.getAlpha() * 100);
        brightness_indicator.setText(String.valueOf((int) circleSeekBar_brightness.getProgress()));
        alpha_indicator.setText(String.valueOf((int) circleSeekBar_alpha.getProgress()));
        if (currentDarkerSettings.isUseColor()) {
            aSwitch.setChecked(true);
            colorSeekBar.setVisibility(View.VISIBLE);
        } else {
            aSwitch.setChecked(false);
            colorSeekBar.setVisibility(View.INVISIBLE);
        }
        if (currentDarkerSettings.isUseBrightness()) {
            bSwitch.setChecked(true);
            circleSeekBar_brightness.setVisibility(View.VISIBLE);
        } else {
            bSwitch.setChecked(false);
            circleSeekBar_brightness.setVisibility(View.INVISIBLE);
            brightness_indicator.setText(R.string.auto_brightness);
        }
        colorSeekBar.setColorBarValue((int) currentDarkerSettings.getColorBarPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this))
                    prepareForService();
                else {
                    doCleanBeforeExit();
                    finish();
                    System.exit(0);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            moveTaskToBack(true);
        return super.onKeyDown(keyCode, event);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            } else
                prepareForService();
        } else
            prepareForService();
    }

    private void prepareForService() {
        intent = new Intent(getApplicationContext(), ScreenFilterService.class);
        startService(intent);
    }

    private void doCleanBeforeExit() {
        try {
            darkerNotification.removeNotification();
            unregisterReceiver(broadcastReceiver);
            stopService(intent);
        } catch (Exception ignored) {
        }
    }
    public class getProcessThread implements Runnable{

        @Override
        public void run() {
            while(true){
                myApp.listApp = AndroidProcesses.getRunningAppProcesses();
            }
        }
    }
}

