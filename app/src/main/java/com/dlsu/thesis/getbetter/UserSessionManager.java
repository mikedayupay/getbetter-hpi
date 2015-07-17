package com.dlsu.thesis.getbetter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * GetBetter. Created by Mike Dayupay on 7/17/15.
 */
public class UserSessionManager {

    SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "GetBetterPref";

    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String KEY_HEALTH_CENTER = "healthcenter";

    public static final String KEY_EMAIL = "email";

    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);

        editor = pref.edit();
    }

    public void createUserLoginSession (String email, String healthcenter) {

        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_HEALTH_CENTER, healthcenter);
        editor.commit();
    }

    public boolean checkLogin() {

        if(!this.isUserLoggedIn()) {

            Intent i = new Intent(_context, MainActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);

            return true;
        }

        return false;
    }

    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KEY_HEALTH_CENTER, pref.getString(KEY_HEALTH_CENTER, null));

        return user;
    }

    public void logoutUser() {

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, MainActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    public boolean isUserLoggedIn () {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

}
