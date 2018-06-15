/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tbs.tbssms.client;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class NotificationSettingsActivity extends PreferenceActivity {

    private static final String LOGTAG = LogUtil.makeLogTag(NotificationSettingsActivity.class);

    public NotificationSettingsActivity() {
    	
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(createPreferenceHierarchy());
        setPreferenceDependencies();

        CheckBoxPreference notifyPref = (CheckBoxPreference) getPreferenceManager().findPreference(Constants.SETTINGS_NOTIFICATION_ENABLED);
        if (notifyPref.isChecked()) {
            notifyPref.setTitle("通知已激活");
        } else {
            notifyPref.setTitle("通知已取消");
        }
    }

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(Constants.SHARED_PREFERENCE_NAME);
        preferenceManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        PreferenceScreen root = preferenceManager.createPreferenceScreen(this);

        //        PreferenceCategory prefCat = new PreferenceCategory(this);
        //        // inlinePrefCat.setTitle("");
        //        root.addPreference(prefCat);

        CheckBoxPreference notifyPref = new CheckBoxPreference(this);
        notifyPref.setKey(Constants.SETTINGS_NOTIFICATION_ENABLED);
        notifyPref.setTitle("激活通知");
        notifyPref.setSummaryOn("推送新消息");
        notifyPref.setSummaryOff("取消推送新消息");
        notifyPref.setDefaultValue(Boolean.TRUE);
        notifyPref
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference preference,Object newValue) {
                        boolean checked = Boolean.valueOf(newValue.toString());
                        if (checked) {
                            preference.setTitle("通知已激活");
                        } else {
                            preference.setTitle("通知已取消");
                        }
                        return true;
                    }
                });

        CheckBoxPreference soundPref = new CheckBoxPreference(this);
        soundPref.setKey(Constants.SETTINGS_SOUND_ENABLED);
        soundPref.setTitle("声音");
        soundPref.setSummary("设置通知声音");
        soundPref.setDefaultValue(Boolean.TRUE);
        // soundPref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);

        CheckBoxPreference vibratePref = new CheckBoxPreference(this);
        vibratePref.setKey(Constants.SETTINGS_VIBRATE_ENABLED);
        vibratePref.setTitle("震动");
        vibratePref.setSummary("设置通知震动");
        vibratePref.setDefaultValue(Boolean.TRUE);
        // vibratePref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);
        
        root.addPreference(notifyPref);
        root.addPreference(soundPref);
        root.addPreference(vibratePref);

        //        prefCat.addPreference(notifyPref);
        //        prefCat.addPreference(soundPref);
        //        prefCat.addPreference(vibratePref);
        //        root.addPreference(prefCat);

        return root;
    }

    private void setPreferenceDependencies() {
        Preference soundPref = getPreferenceManager().findPreference(Constants.SETTINGS_SOUND_ENABLED);
        if (soundPref != null) {
            soundPref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);
        }
        Preference vibratePref = getPreferenceManager().findPreference(Constants.SETTINGS_VIBRATE_ENABLED);
        if (vibratePref != null) {
            vibratePref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);
        }
    }

}
