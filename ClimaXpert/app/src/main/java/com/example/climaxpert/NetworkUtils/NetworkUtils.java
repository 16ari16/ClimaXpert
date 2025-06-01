package com.example.climaxpert.NetworkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static void showNoInternetToast(Context context) {
        Toast.makeText(
                context,
                "Отсутствует подключение к интернету",
                Toast.LENGTH_LONG
        ).show();
    }

    public static boolean checkInternetConnectionWithToast(Context context) {
        boolean isConnected = isNetworkAvailable(context);
        if (!isConnected) {
            showNoInternetToast(context);
        }
        return isConnected;
    }
}