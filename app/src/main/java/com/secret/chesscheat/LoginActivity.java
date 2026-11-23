package com.secret.chesscheat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);

        EditText passInput = new EditText(this);
        passInput.setHint("Введите пароль");
        passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button loginBtn = new Button(this);
        loginBtn.setText("ВОЙТИ");

        layout.addView(passInput);
        layout.addView(loginBtn);
        setContentView(layout);

        // ТУТ ТВОЙ ПАРОЛЬ (можешь поменять на свой)
        String secretPassword = "CMEXD"; 

        loginBtn.setOnClickListener(v -> {
            if (passInput.getText().toString().equals(secretPassword)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
