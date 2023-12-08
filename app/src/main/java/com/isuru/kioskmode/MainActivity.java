package com.isuru.kioskmode;

import android.app.ActionBar;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.isuru.kioskmode.Service.DeviceAdminService;
import com.isuru.kioskmode.Service.KIOSKManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DevicePolicyManager devicePolicyManager = null; // devicePolicyManager used to activate device admin
    ComponentName adminCompName = null;             // adminCompName


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

//        Button btnActivate = findViewById(R.id.btnActivate);
//        Button btnDeactivate = findViewById(R.id.btnDeactivate);
//        btnActivate.setOnClickListener(this);
//        btnDeactivate.setOnClickListener(this);

//        initLogger();
        initDeviceAdmin();

    }

    /**
     * configuration for logger
     */
//    private void initLogger() {
//        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                .tag("KIOSKDemo")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
//                .build();
//
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
//            @Override
//            public boolean isLoggable(int priority, String tag) {
//                return BuildConfig.DEBUG;
//            }
//        });
//    }

//    private void setDefaultCosuPolicies(boolean active){
//
//        // Set user restrictions
//        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
//        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
//        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
//        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
//        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);
//
//        // Disable keyguard and status bar
//        devicePolicyManager.setKeyguardDisabled(adminCompName, active);
//        devicePolicyManager.setStatusBarDisabled(adminCompName, active);
//
//        // Enable STAY_ON_WHILE_PLUGGED_IN
//        enableStayOnWhilePluggedIn(active);
//
//        // Set system update policy
//        if (active){
//            devicePolicyManager.setSystemUpdatePolicy(adminCompName, SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
//        } else {
//            devicePolicyManager.setSystemUpdatePolicy(adminCompName,null);
//        }
//
//        // set this Activity as a lock task package
//        devicePolicyManager.setLockTaskPackages(adminCompName,active ? new String[]{getPackageName()} : new String[]{});
//
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
//        intentFilter.addCategory(Intent.CATEGORY_HOME);
//        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
//
//        if (active) {
//            // set Cosu activity as home intent receiver so that it is started
//            // on reboot
//            devicePolicyManager.addPersistentPreferredActivity(adminCompName, intentFilter, new ComponentName(getPackageName(), MainActivity.class.getName()));
//        } else {
//            devicePolicyManager.clearPackagePersistentPreferredActivities(adminCompName, getPackageName());
//        }
//    }
//
//    private void setUserRestriction(String restriction, boolean disallow){
//        if (disallow) {
//            devicePolicyManager.addUserRestriction(adminCompName,restriction);
//        } else {
//            devicePolicyManager.clearUserRestriction(adminCompName,restriction);
//        }
//    }
//
//    private void enableStayOnWhilePluggedIn(boolean enabled){
//        if (enabled) {
//            devicePolicyManager.setGlobalSetting(adminCompName, Settings.Global.STAY_ON_WHILE_PLUGGED_IN,Integer.toString(BatteryManager.BATTERY_PLUGGED_AC| BatteryManager.BATTERY_PLUGGED_USB| BatteryManager.BATTERY_PLUGGED_WIRELESS));
//        } else {
//            devicePolicyManager.setGlobalSetting(adminCompName,Settings.Global.STAY_ON_WHILE_PLUGGED_IN,"0");
//        }
//    }

    /**
     * Activates KIOSK mode by calling enableKioskMode() of KIOSKManager class
     *
     * @param status
     */
    private void activateKIOSK(boolean status){
        System.out.println("dpm is adm = " + devicePolicyManager.isAdminActive(adminCompName));

        if(devicePolicyManager.isAdminActive(adminCompName)) {
//            setDefaultCosuPolicies(status);
//            KIOSKManager km = new KIOSKManager(this);
//            km.enableKioskMode(status);

            if(status){
                this.startLockTask();
            }
            else{
                this.stopLockTask();
            }

            devicePolicyManager.setKeyguardDisabled(adminCompName, status);
            devicePolicyManager.setStatusBarDisabled(adminCompName, status);

        }
    }

    /**
     * Initialize device admin privileges
     */
    private void initDeviceAdmin(){
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminCompName = new ComponentName(this, DeviceAdminService.class);              // Initializing the component;



        try {
            System.out.println("----------");
            Process p = Runtime.getRuntime().exec("dpm set-device-owner com.isuru.kioskmode/com.isuru.kioskmode.Service.DeviceAdminService");
//            p = Runtime.getRuntime().exec("pm disable-user --user 0 com.android.systemui");
            //pm enable --user 0 com.android.systemui
            InputStream inputStream = p.getErrorStream();
            String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            System.out.println("result = " + result);
        } catch (IOException e) {
            System.out.println("erro");
            e.printStackTrace();
        }

        System.out.println("dpm is owner = " + devicePolicyManager.isDeviceOwnerApp(getPackageName()));
        System.out.println("dpm is admin = " + devicePolicyManager.isAdminActive(adminCompName));

        if (devicePolicyManager.isDeviceOwnerApp(getPackageName())) {
            devicePolicyManager.setLockTaskPackages(adminCompName, new String[]{getPackageName()});
        }


        if(devicePolicyManager.isAdminActive(adminCompName)) {
            this.startLockTask();
            devicePolicyManager.setKeyguardDisabled(adminCompName, true);
            devicePolicyManager.setStatusBarDisabled(adminCompName, true);
        }

    }

    @Override
    public void onClick(View v) {
//
//        switch (v.getId()){
//            case R.id.btnActivate: { activateKIOSK(true); break;}
//            case R.id.btnDeactivate: { activateKIOSK(false); break;}
//        }
    }

    /**
     * Called when the window containing this view gains or loses focus and Hide system UIs
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
