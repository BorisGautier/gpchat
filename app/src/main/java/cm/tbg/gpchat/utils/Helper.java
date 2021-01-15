package cm.tbg.gpchat.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cm.tbg.gpchat.model.realms.Contact;
import cm.tbg.gpchat.model.realms.LogCall;
import cm.tbg.gpchat.model.realms.User;


public class Helper {
    public static boolean DISABLE_SPLASH_HANDLER = false;
    private static final String USER = "USER";
    private static final String USER_MUTE = "USER_MUTE";
    private static final String LOGS_CALLS = "CALL_LOGS";
    public static final String BROADCAST_MY_USERS = "com.opuslabs.yoohoo.MY_USERS";
    public static final String BROADCAST_MY_CONTACTS = "com.opuslabs.yoohoo.MY_CONTACTS";
    public static final String BROADCAST_USER_ME = "com.opuslabs.yoohoo.USER_ME";
    public static final String BROADCAST_LOGOUT = "com.opuslabs.yoohoo.services.LOGOUT";
    public static final String GROUP_CREATE = "group_create";
    public static final String GROUP_PREFIX = "group";
    public static final String USER_MY_CACHE = "usersmycache";
    public static final String CONTACTS_MY_CACHE = "contactsmycache";
    public static final String REF_CHAT = "chats";
    public static final String REF_GROUP = "groups";
    public static final String REF_INBOX = "inbox";
    public static final String REF_USERS = "users";
    public static String CURRENT_CHAT_ID;
    public static boolean CHAT_CAB = false;

    private SharedPreferenceHelper sharedPreferenceHelper;
    private Gson gson;
    private HashSet<String> muteUsersSet;
    private HashMap<String, User> myUsersNameInPhoneMap;

    public Helper(Context context) {
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        gson = new Gson();
    }










    public User getLoggedInUser() {
        String savedUserPref = sharedPreferenceHelper.getStringPreference(USER);
        if (savedUserPref != null)
            return gson.fromJson(savedUserPref, new TypeToken<User>() {
            }.getType());
        return null;
    }

    public void setLoggedInUser(User user) {
        sharedPreferenceHelper.setStringPreference(USER, gson.toJson(user, new TypeToken<User>() {
        }.getType()));
    }

    public void saveCallLog(LogCall logCall) {
        ArrayList<LogCall> previousLogs = getCallLogs();
        previousLogs.add(logCall);
        sharedPreferenceHelper.setStringPreference(LOGS_CALLS, gson.toJson(previousLogs, new TypeToken<ArrayList<LogCall>>() {
        }.getType()));
    }

    public ArrayList<LogCall> getCallLogs() {
        ArrayList<LogCall> toReturn;
        String savedInPrefs = sharedPreferenceHelper.getStringPreference(LOGS_CALLS);
        if (savedInPrefs != null) {
            toReturn = gson.fromJson(savedInPrefs, new TypeToken<ArrayList<LogCall>>() {
            }.getType());
        } else {
            toReturn = new ArrayList<>();
        }
        return toReturn;
    }

    public HashMap<String, Contact> getMyContacts() {
        String savedContactsPref = sharedPreferenceHelper.getStringPreference(CONTACTS_MY_CACHE);
        if (savedContactsPref != null) {
            return gson.fromJson(savedContactsPref, new TypeToken<HashMap<String, Contact>>() {
            }.getType());
        } else {
            return new HashMap<String, Contact>();
        }
    }

    public ArrayList<User> getMyUsers() {
        String savedUserPref = sharedPreferenceHelper.getStringPreference(USER_MY_CACHE);
        if (savedUserPref != null) {
            return gson.fromJson(savedUserPref, new TypeToken<ArrayList<User>>() {
            }.getType());
        } else {
            return new ArrayList<User>();
        }
    }




}
