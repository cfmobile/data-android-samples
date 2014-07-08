/* Copyright (c) 2013 Pivotal Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.android.data.sample.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import io.pivotal.android.data.sample.R;
import io.pivotal.android.data.sample.util.Preferences;

public class PreferencesActivity extends PreferenceActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private Map<String, Preference> preferenceMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        // NOTE - many of the method calls in this class show up as deprecated.  However, I still want my
        // app to run on old Android versions, so I'm going to leave them in here.
        addPreferencesFromResource(getPreferencesXmlResourceId());
        addPreferences(getPreferenceNames());
        preferenceChangeListener = getPreferenceChangeListener();
    }

    protected String[] getPreferenceNames() {
        return Preferences.PREFERENCE_NAMES;
    }

    protected int getPreferencesXmlResourceId() {
        return R.xml.preferences;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            final ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void addPreferences(String[] preferenceNames) {
        preferenceMap = new HashMap<String, Preference>();
        for (final String preferenceName: preferenceNames) {
            addPreference(preferenceName);
        }
    }

    private void addPreference(String preferenceName) {
        final Preference item = getPreferenceScreen().findPreference(preferenceName);
        preferenceMap.put(preferenceName, item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener getPreferenceChangeListener() {
        return new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                showCurrentPreferences();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentPreferences();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void showCurrentPreferences() {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        for (final String preferenceName : preferenceMap.keySet()) {
            final Preference item = preferenceMap.get(preferenceName);
            if (item instanceof EditTextPreference) {
                setupEditTextPreferenceField((EditTextPreference) item, prefs.getString(preferenceName, null));
            } else if (item instanceof CheckBoxPreference) {
                setupCheckBoxPreference((CheckBoxPreference) item, prefs.getBoolean(preferenceName, false));
            }
        }
    }

    protected void setupEditTextPreferenceField(EditTextPreference preference, String value) {
        preference.setText(value);
        preference.setSummary(value);
    }

    private void setupCheckBoxPreference(CheckBoxPreference preference, boolean value) {
        preference.setChecked(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.action_reset_preferences) {
            resetPreferencesToDefault();
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetPreferencesToDefault() {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.edit().clear().commit();
        PreferenceManager.setDefaultValues(this, getPreferencesXmlResourceId(), true);
        showCurrentPreferences();
    }
}
