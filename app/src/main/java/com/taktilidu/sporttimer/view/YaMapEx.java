package com.taktilidu.sporttimer.view;

import android.content.Context;
import android.util.AttributeSet;

import ru.yandex.yandexmapkit.MapView;

public class YaMapEx extends MapView {

    private static final String RESOURCE_PREFIX = "D0IG66eKaTqOZrw6WdmX0sZiJSc72cRt~69zkn8V7Yj2immPgUaHqR-~krn~JHbzJnqkiJa2oRaCcqBEZbdLNvmYVbPo5O~jjmbMk-9MaDA=";

    public YaMapEx(Context context, String s) {
        super(context, s);
    }

    public YaMapEx(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    @Override
    public String getApiKey() {
        String apiKey = super.getApiKey();
        if (apiKey != null && apiKey.startsWith(RESOURCE_PREFIX)) {
            String strResName = apiKey.substring(RESOURCE_PREFIX.length());
            try {
                int apiKeyResourceId = Integer.parseInt(strResName);
                if (apiKeyResourceId > 0) {
                    String apiKeyFromResorce = getContext().getResources().getString(apiKeyResourceId);
                    return apiKeyFromResorce;
                }
            } catch (Exception ex) {
                //return api key from super
            }
        }
        return apiKey;
    }
}
