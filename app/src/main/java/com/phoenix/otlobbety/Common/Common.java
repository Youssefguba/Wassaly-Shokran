package com.phoenix.otlobbety.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.Model.Request;
import com.phoenix.otlobbety.Model.User;
import com.phoenix.otlobbety.Remote.APIService;
import com.phoenix.otlobbety.Remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static String topicName = "News";

    public static User currentUser;
    public static Request currentRequest;

    public static String nameOfCategory;
    public static String nameOfSubCategory;
    public static String subCategoryID;
    public static String childItemId;
    public static String indexOfItemInArray;

    public static String imgOfSubCategory;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else
            return "Accepted";
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static final String DELETE = "Delete";

    public static final String USER_KEY = "User";

    public static final String PWD_KEY = "Password";

    public static String PHONE_TEXT = "userPhone";

    public static final String TOTAL_PRICE = "Total Price";
    public static final String DELIVERY_COST = "Delivery Cost";

    public static List<Order> listOfCart = new ArrayList<>();

}
