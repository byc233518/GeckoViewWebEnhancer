package com.gecko.webview.tv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SPUtil {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * 保存boolean类型配置信息
     *
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        getSharedPreferences(context).edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * 获取boolean类型配置信息
     *
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreferences(context).getBoolean(key, defValue);
    }

    /**
     * 保存int类型配置信息
     *
     * @param key
     * @param value
     */
    public static void saveInt(Context context, String key, int value) {
        getSharedPreferences(context).edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * 获取int类型配置信息
     *
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    /**
     * 保存long类型配置信息
     *
     * @param key
     * @param value
     */
    public static void saveLong(Context context, String key, long value) {
        getSharedPreferences(context).edit()
                .putLong(key, value)
                .apply();
    }

    /**
     * 获取long类型配置信息
     *
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(Context context, String key, Long defValue) {
        return getSharedPreferences(context).getLong(key, defValue);
    }

    /**
     * 保存String类型配置信息
     *
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        getSharedPreferences(context).edit()
                .putString(key, value)
                .apply();
    }

    /**
     * 获取String类型配置信息
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getSharedPreferences(context).getString(key, defValue);
    }

    // 保存新的输入记录到 List 中
    public static void saveInputHistory(Context context,String newInput) {
        // 获取现有的记录
        List<String> currentList = getHistoryStringList(context);
        // 如果列表中已经包含新输入，先移除它（避免重复）
        currentList.remove(newInput);
        // 添加新输入到列表开头
        currentList.add(0, newInput);
        // 如果超过最大记录条数，移除最旧的
        if (currentList.size() > 5) {
            currentList.remove(currentList.size() - 1);
        }
        saveHistoryStringList(context,currentList);
    }

    private static void saveHistoryStringList(Context context, List<String> stringList) {
        StringBuilder csvList = new StringBuilder();
        for (String item : stringList) {
            csvList.append(item).append(",");
        }
        // 去除最后一个逗号
        if (csvList.length() > 0) {
            csvList.setLength(csvList.length() - 1);
        }
        SPUtil.saveString(context,"input_history",csvList.toString());
    }

    public static List<String> getHistoryStringList(Context context) {
        String csvList = SPUtil.getString(context,"input_history","");
        if (!csvList.isEmpty()) {
            return new ArrayList<>(Arrays.asList(csvList.split(",")));
        }
        return new ArrayList<>();
    }

    public static void removeHistoryString(Context context,String inputToRemove) {
        List<String> currentList = getHistoryStringList(context);
        if (currentList.remove(inputToRemove)) {
            saveHistoryStringList(context,currentList);
        }
    }

}
