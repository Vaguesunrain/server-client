package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Client_Act extends AppCompatActivity {
    Client client;
    int requestCode;//用于标识此请求
    private CircleImage circleImage;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<MyItem> itemList;
    boolean connected_state=false;
    private TextView textView_id;
    String getdata;//get data from server
    private SharedPreferences sharedPreferences;//用于读写固定数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        client = new Client();
        //为layout里的circleimage设置图片
        textView_id = findViewById(R.id.idname);
        circleImage = findViewById(R.id.imageView);
        circleImage.setImageResource(R.drawable.photo2);
        TextView remote = findViewById(R.id.remote);
        TextView local =  findViewById(R.id.local);
        recyclerView =findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        // 初始化RecyclerView的适配器
        adapter = new MyAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 添加初始数据
        addItem("项目 1");
        addItem("项目 2");
        addItem("项目 3");
        Drawable targetBackground = getResources().getDrawable(R.drawable.custom_background);
        // 获取当前 Drawable 对象的 ConstantState
        Drawable.ConstantState targetBackgroundState = targetBackground.getConstantState();
        Drawable liteBackground = getResources().getDrawable(R.drawable.lite_background);
        // 获取当前 Drawable 对象的 ConstantState
        //Drawable.ConstantState liteBackgroundState = targetBackground.getConstantState();

        //查看有没有注册过，注册过会有valid="1"

        String valid = read_data("valid");
        if (valid.isEmpty()) {
            // 如果数据为空，创建数据
            write_data("0","valid");
            //toast提示
            Toast.makeText(Client_Act.this, "保存成功", Toast.LENGTH_SHORT).show();
        }

        //查看有没有设置过信息数据

        String information = read_data("information");
        if (information.isEmpty()) {
            // 如果数据为空，do nothing
        }
        else {//设置参数
            textView_id.setText(information);
        }

        //以上为了处理初始化问题

        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去下一个activity
                Intent intent = new Intent(Client_Act.this,Information.class);
                startActivityForResult(intent,requestCode);
            }
        });

        remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //如果背景不是lite_background，或者为空，切换成 lite_background ，否则不操作、
                Drawable currentBackground = null;
                Drawable.ConstantState currentBackgroundState= null;
                try {
                     currentBackground = v.getBackground();
                     currentBackgroundState = currentBackground.getConstantState();}
                catch (Exception e){
                    //
                }
                // 检查两个 Drawable 对象的 ConstantState 是否相同
                if (currentBackgroundState!= null  &&
                        currentBackgroundState.equals(targetBackgroundState)) {
                    // 如果背景是 lite_background，什么都不做
                } else {
                    // 否则，切换成 lite_background 背景
                    remote.setBackground(targetBackground);
                    remote.setTextColor(Color.BLACK);
                    local.setBackground(liteBackground);
                    //灰色
                    local.setTextColor(Color.GRAY);
                    System.out.println("wwww");
                }


            }
        });

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable currentBackground = null;
                Drawable.ConstantState currentBackgroundState= null;
                try {
                     currentBackground = v.getBackground();
                     currentBackgroundState = currentBackground.getConstantState();}
                catch (Exception e){
                    //
                }
                // 检查两个 Drawable 对象的 ConstantState 是否相同
                if (currentBackgroundState != null &&
                        currentBackgroundState.equals(targetBackgroundState)) {
                    // 如果背景是 lite_background，什么都不做
                } else {
                    // 否则，切换成 lite_background 背景
                    local.setBackground(targetBackground);
                    //设置字体为黑色
                    local.setTextColor(Color.BLACK);
                    remote.setBackground(liteBackground);
                    remote.setTextColor(Color.GRAY);
                    System.out.println("wwww");
                }
            }
        });

        //线程，每四秒执行一次
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(4000);
                        connect_toser();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //线程,一直循环
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        get_data_from_server();
                        //System.out.println(getdata);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();



    }

    // 在第一个Activity中处理结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCode) { // 检查 requestCode，确保是你期望的请求
            if (resultCode == Information.RESULT_OK) {
                // 处理成功的结果，可以从 data 中获取传递的数据
                String resultData = data.getStringExtra("resultKey");

                // 执行你的函数
                write_data(resultData,"information");
                textView_id.setText(resultData);
                //yourFunction(resultData);
            }
        }
    }



    private void connect_toser() {//连接服务器
        SharedPreferences sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);
        String valid = sharedPreferences.getString("valid","");
        if (valid.equals("1") && connected_state==false) {
            client.client_connect("123.249.88.236", 2019);
            client.client_send("xxx"+read_data("index"));
            connected_state=true;
            return;
        }
        else {
            System.out.println("server online");
            return ;
        }
    }

    private void get_data_from_server() {
        if(connected_state==false) {
            return;
        }
        getdata = client.client_read();
    }

    // 添加新项目到RecyclerView
    private void addItem(String text) {
        MyItem newItem = new MyItem(text);
        itemList.add(newItem);
        adapter.notifyItemInserted(itemList.size() - 1);
    }

    private String read_data(String text) {
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);
        return sharedPreferences.getString(text,"");


    }
    private void write_data(String data, String text){
        sharedPreferences.edit().putString(text, data).apply();
    }
}