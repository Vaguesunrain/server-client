package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class Information extends AppCompatActivity {
    String resultData = null ;
    private String Ip="123.249.88.236";
    boolean isConnected = false;
    SharedPreferences sharedPreferences;
    String index="null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        EditText editText =  findViewById(R.id.editTextTextPersonName);
        Button button =  findViewById(R.id.confirm_button);
        ImageView imageView =  findViewById(R.id.head_image);
        EditText account =  findViewById(R.id.account);
        EditText password =  findViewById(R.id.your_password);
        Switch func_change = (Switch) findViewById(R.id.func_change);
        Button submit =  findViewById(R.id.submit);
        Client sign_Client=new Client();
        Client log_in_Client= new Client();
        /*UI 初始化，分两步*/
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);
        String information = sharedPreferences.getString("information","");

        if (information.isEmpty()) {
            // 如果数据为空，do nothing
        }
        else {//设置参数
            editText.setText(information);
            resultData = information;
        }
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);
        String valid = sharedPreferences.getString("valid","");
        if (valid.isEmpty()||valid.equals("0")) {
            // 如果数据为空，do nothing
        }
        else {//删除组件
            account.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            func_change.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        }
        /*UI 初始化完成*/

        /*handle 处理*/
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {  //这个是发送过来的消息
                // 处理从子线程发送过来的消息
                if (msg.what != 0 && msg.what != -1) {
                    account.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    func_change.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                    write_data("1", "valid");//写入有效位1
                    write_data(int_to_string(msg.what), "index");//写入在数据库的索引
                    //Toast.makeText(Information.this, "修改成功", Toast.LENGTH_SHORT).show();
                    //System.out.println(int_to_string(msg.what));
                }
            };
        };


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断switch状态
                if (func_change.isChecked()) {
                    //创建一个子线程，并运行
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean success = log_in_Client.client_connect(Ip,2021);
                            if (success) {
                            }
                            else {
                                return;
                            }
                            log_in_Client.client_send((account.getText().toString()+","+password.getText().toString()));
                             index = log_in_Client.client_read();
                            System.out.println(index);
                            if (index.equals("-1")||index.equals("0")) {
                                log_in_Client.client_close();
                                return;//失败退掉连接
                            }
                            else {
                                Message msg = Message.obtain(handler, Integer.parseInt(index));
                                msg.sendToTarget();
                            }
                            log_in_Client.client_close();
                            System.out.println("okokorqwrwkok");
                        }
                    });
                    thread.start();
                }
                 else{
                    //创建一个子线程，并运行
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean success = sign_Client.client_connect(Ip,2020);
                            if (success) {
                            }
                            else {
                                return;
                            }
                            sign_Client.client_send((account.getText().toString()+","+password.getText().toString()));
                            String result = sign_Client.client_read();
                            if (result.equals("Register ok")) {
                            }
                            else{
                                sign_Client.client_close();
                                return;//注册失败关闭注册链接
                            }
                            sign_Client.client_close();
                        }
                    });
                    thread.start();
                }


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultData = editText.getText().toString();
                //toast 打印
                Toast.makeText(Information.this, "set successfully", Toast.LENGTH_SHORT).show();
                write_data(resultData, "information");
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Information.this, "have not set function", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //重写返回函数
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("resultKey", resultData); // 如果有需要，传递一些结果数据
            setResult(Information.RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String read_data(String text) {
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);
        return sharedPreferences.getString(text,"");


    }
    private void write_data(String data, String text){
        sharedPreferences.edit().putString(text, data).apply();
    }
    //将一个int数据变成三位string，例如1变001
    private String int_to_string(int i) {
        return String.format("%03d", i);
    }
}
