package com.example.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<MyItem> itemList;
    private Context context;

    public MyAdapter(Context context, List<MyItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    // 添加一个用于更新TextView文本的方法
    public void updateTextViewText(int position, String newText) {
        if (position >= 0 && position < itemList.size()) {
            MyItem item = itemList.get(position);
            item.setText(newText);

            // 通知RecyclerView更新对应位置的数据
            notifyItemChanged(position);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyItem item = itemList.get(position);
        holder.textView.setText(item.getText());

        // 添加按钮点击事件监听器
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用holder.getAdapterPosition()获取位置
                int itemPosition = holder.getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    // 点击按钮删除项目
                    itemList.remove(itemPosition);
                    notifyItemRemoved(itemPosition);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            button = itemView.findViewById(R.id.button);
        }

        // 添加一个方法来更新TextView的文本
        public void updateTextViewText(String text) {
            textView.setText(text);
        }
    }
}
