package com.gecko.webview.tv.pop;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gecko.webview.tv.OnInputConfirmListener;
import com.gecko.webview.tv.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.impl.FullScreenPopupView;

public class InputPop extends FullScreenPopupView {
    private String address;
    private Bitmap qrBitmap;
    private ImageView imgQr;
    private EditText edtAddress;
    private OnInputConfirmListener listener;
    private TextView tvAddress;

    public InputPop(@NonNull Context context, String address, Bitmap qrBitmap, OnInputConfirmListener listener) {
        super(context);
        this.address = address;
        this.qrBitmap = qrBitmap;
        this.listener = listener;
    }

    public void setInput(String input) {
        if (edtAddress != null) {
            edtAddress.setText(input);
        }
    }

    public void setAddress(String address){
        if (tvAddress != null) {
            tvAddress.setText(address);
        }
    }

    public void setQrBitmap(Bitmap bitmap){
        if (imgQr != null){
            imgQr.setImageBitmap(bitmap);
        }
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_input;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        imgQr = findViewById(R.id.img_qr);
        tvAddress = findViewById(R.id.tv_address);
        edtAddress = findViewById(R.id.edt_address);
        Button btn_history = findViewById(R.id.btn_history);
        Button btn_confirm = findViewById(R.id.btn_confirm);

        if (qrBitmap != null) {
            imgQr.setImageBitmap(qrBitmap);
        }
        if (address != null) {
            tvAddress.setText(address);
        }
        btn_confirm.setOnClickListener(v -> {
            String input = edtAddress.getText().toString();
            if (TextUtils.isEmpty(input)){
                Toast.makeText(getContext(), "请输入地址", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onConfirm(input);
            dismiss();
        });
        btn_history.setOnClickListener(v -> {
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .asCustom(new HistoryPop(getContext(), new OnInputConfirmListener() {
                        @Override
                        public void onConfirm(String input) {
                            setInput(input);
                        }
                    }))
                    .show();
        });
    }
}
