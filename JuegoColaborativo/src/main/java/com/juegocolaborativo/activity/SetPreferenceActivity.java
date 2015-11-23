package com.juegocolaborativo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.juegocolaborativo.fragment.PrefsFragment;

public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}