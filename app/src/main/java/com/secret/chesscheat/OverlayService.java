package com.secret.chesscheat;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlayView;
    private WebView engineWebView;
    private TextView resultText;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 1. Создаем невидимый WebView для шахматного движка Stockfish
        engineWebView = new WebView(this);
        engineWebView.getSettings().setJavaScriptEnabled(true);
        engineWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void showMove(final String move) {
                // Когда движок нашел ход, показываем его в окошке
                ((android.app.Activity) getApplicationContext()).runOnUiThread(() -> {
                    if (resultText != null) resultText.setText("Ход: " + move);
                });
            }
        }, "AndroidBridge");

        // Загружаем движок из интернета
        String html = "<html><body><script src='https://cdnjs.cloudflare.com/ajax/libs/stockfish.js/10.0.2/stockfish.js'></script>" +
                "<script>var engine = new Worker('https://cdnjs.cloudflare.com/ajax/libs/stockfish.js/10.0.2/stockfish.js');" +
                "function findBestMove(fen) { engine.postMessage('position fen ' + fen); engine.postMessage('go depth 10'); }" +
                "engine.onmessage = function(e) { if(e.data.includes('bestmove')) { window.AndroidBridge.showMove(e.data.split(' ')[1]); } };" +
                "</script></body></html>";
        engineWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        // 2. Создаем плавающее окно
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xCC000000); // Черный полупрозрачный
        layout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("ШАХМАТНЫЙ ЧИТ");
        title.setTextColor(0xFF00FF00);
        title.setTextSize(18f);
        title.setGravity(Gravity.CENTER);

        EditText fenInput = new EditText(this);
        fenInput.setHint("Вставь FEN позиции...");
        fenInput.setTextColor(0xFFFFFFFF);
        fenInput.setTextSize(12f);

        Button analyzeBtn = new Button(this);
        analyzeBtn.setText("ЛУЧШИЙ ХОД");

        resultText = new TextView(this);
        resultText.setTextColor(0xFFFFFF00);
        resultText.setTextSize(20f);
        resultText.setGravity(Gravity.CENTER);
        resultText.setText("Ожидание...");

        layout.addView(title);
        layout.addView(fenInput);
        layout.addView(analyzeBtn);
        layout.addView(resultText);

        int layoutFlag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 100;

        overlayView = layout;
        windowManager.addView(overlayView, params);

        // 3. Кнопка "Лучший ход"
        analyzeBtn.setOnClickListener(v -> {
            String fen = fenInput.getText().toString().trim();
            if (!fen.isEmpty()) {
                resultText.setText("Думаю...");
                // Отправляем позицию движку
                engineWebView.evaluateJavascript("findBestMove('" + fen + "');", null);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) windowManager.removeView(overlayView);
        if (engineWebView != null) engineWebView.destroy();
    }
}
