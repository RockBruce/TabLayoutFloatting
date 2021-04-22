package cn.edsmall.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import cn.edsmall.router_annotation.Route;

@Route(path = "/router/login")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView textView = findViewById(R.id.tv_library);
        textView.setText("这是一个library包。。。。。。。。。。。。。。。。。。。。");
        //组件模块下不能用路由
        if (!BuildConfig.isModule) {

        } else {
            Toast.makeText(this, "当前处于组件模式，无法使用此功能", Toast.LENGTH_SHORT).show();
        }
        onClick();
    }

    private void onClick() {
    }

}