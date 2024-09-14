package com.gecko.webview.tv.pop;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gecko.webview.tv.OnInputConfirmListener;
import com.gecko.webview.tv.R;
import com.gecko.webview.tv.utils.SPUtil;
import com.lxj.xpopup.core.CenterPopupView;

public class HistoryPop extends CenterPopupView {
    private HistoryAdapter historyAdapter;
    private OnInputConfirmListener listener;

    public HistoryPop(@NonNull Context context, OnInputConfirmListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_history;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        RecyclerView rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
        String currentAddress = SPUtil.getString(getContext(), "url", "");
        historyAdapter = new HistoryAdapter(getContext(), SPUtil.getHistoryStringList(getContext()), currentAddress, new OnInputConfirmListener() {
            @Override
            public void onConfirm(String address) {
                dismiss();
                listener.onConfirm(address);
            }
        });
        rv_list.setAdapter(historyAdapter);
    }

}
