package com.secret.chesscheat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);

        TextView text = new TextView(this);
        text.setText("1. Нажми кнопку ниже и разреши показ поверх других приложений.\n2. Вернись и запусти чит.");

        Button btnPerm = new Button(this);
        btnPerm.setText("1. ДАТЬ РАЗРЕШЕНИЕ");
        btnPerm.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });

        Button btnStart = new Button(this);
        btnStart.setText("2. ЗАПУСТИТЬ ЧИТ");
        btnStart.setOnClickListener(v -> {
            startService(new Intent(this, OverlayService.class));
            finish();
        });

        layout.addView(text);
        layout.addView(btnPerm);
        layout.addView(btnStart);
        setContentView(layout);
    }
}
