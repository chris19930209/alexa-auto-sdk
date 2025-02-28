package com.amazon.alexa.auto.lwa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Store for User Identity in Encrypted Preferences.
 */
public class UserIdentityStore {
    private static final String USER_IDENTITY_KEY = "com.amazon.alexa.auth.userIdentity.key";

    /**
     * Fetch user identity if available.
     *
     * @param context Android Context.
     * @return User identity if available, null otherwise.
     */
    static String getUserIdentity(@NonNull Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(USER_IDENTITY_KEY, masterKeyAlias,
                context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        return sharedPreferences.getString(USER_IDENTITY_KEY, null);
    }

    static void saveUserIdentity(@NonNull Context context, @NonNull String userIdentity)
            throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(USER_IDENTITY_KEY, masterKeyAlias,
                context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_IDENTITY_KEY, userIdentity);
        editor.apply();
    }

    static void resetUserIdentity(@NonNull Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(USER_IDENTITY_KEY, masterKeyAlias,
                context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_IDENTITY_KEY);
        editor.apply();
    }
}
