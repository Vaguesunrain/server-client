package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SharedPreferences datawrite_map;//用于读写固定数据
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
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);
        datawrite_map = getSharedPreferences("data_map",MODE_PRIVATE);
        /*handle 处理*/
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {  //这个是发送过来的消息
                // 处理从子线程发送过来的消息
                if (msg.what == 1000 ) {
                }
                else {

                   chatUI_update(msg.what,getdata.substring(3));
                }
            };
        };


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
                        if (getdata!=null) {
                        Message msg = Message.obtain(handler, format_translation(getdata));
                        msg.sendToTarget();}
                        System.out.println(getdata);
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

        String valid = sharedPreferences.getString("valid","");
        if (valid.equals("1") && connected_state==false) {
            client.client_connect("123.249.88.236", 2019);
            client.client_send("xxx"+read_data("index"));
            connected_state=true;
            return;
        }
        else {
            //System.out.println("server online");
            return ;
        }
    }

    private void get_data_from_server() {//从服务器拿数据
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
        return sharedPreferences.getString(text,"");
    }
    private void write_data(String data, String text){
        sharedPreferences.edit().putString(text, data).apply();
    }
    private String read_map(String text) {
        return datawrite_map.getString(text,"");
    }
    private void write_map(String data){
        String[] pairs = data.split("\\.");
        Map<String, String> keyValuePairs = new HashMap<>();
        for (String pair : pairs) {
            // 按照逗号再次拆分键值对
            String[] parts = pair.split(",");
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                keyValuePairs.put(key, value);
            }
        }
        // 将解析后的键值对存储到SharedPreferences中
        for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            datawrite_map.edit().putString(key, value).apply();
        }
    }
    private int format_translation(String msg){
        //获取字符串前三字符
        String str = msg.substring(0, 3);
        String person = msg.substring(3);
        //判断字符串前三字符
        if(str.equals("ooo")){
            //获取删掉前三字符串的部分
            return (person.length()/3);
        }
        else if(str.equals("OOO")){
            write_map(person);
            return 1000;
        }
        else {return 1000;}
    }

    private void chatUI_update(int num,String user_index){
        MyItem itemAtIndex1;
        int get_ui_num=itemList.size();
        if(num<itemList.size()){
            for (int i=get_ui_num-1; i>num-1; i--){
                itemList.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
        else if(num==get_ui_num){}
        else {
            for (int i=0; i<num-get_ui_num; i++){
                addItem("test");
            }
        }
        for (int i = 0; i<num ;i++){
            adapter.updateTextViewText(i,user_index.substring(0,3));//updata ui text
            user_index = user_index.substring(3);
        }
    }
}