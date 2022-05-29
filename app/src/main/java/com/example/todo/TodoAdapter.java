package com.example.todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.CustomViewHolder> {
    List<ToDoModel> toDoModels;
    Context context;
    private onItemClickListener mListener;

    public TodoAdapter(List<ToDoModel> toDoModels, Context context) {
        this.toDoModels = toDoModels;
        this.context = context;
    }

    public void setOnItemClickListener(onItemClickListener listener) {//item click listener initialization
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.todo_item;
    }

    @Override
    public int getItemCount() {
        return toDoModels.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.textViewDate.setText(toDoModels.get(position).getDate());
        holder.textViewTime.setText(toDoModels.get(position).getTime());
        holder.textViewName.setText(toDoModels.get(position).getTodoName());
        holder.textViewDescription.setText(toDoModels.get(position).getTodoDescription());
        if (toDoModels.get(position).getIsFavourite().equals("ja")) {
            holder.imageViewStar.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewStar.setVisibility(View.GONE);
        }
        if (toDoModels.get(position).getTodoStatus().equals("erledigt")) {
            holder.textViewName.setTextColor(context.getResources().getColor(R.color.beige));
            holder.textViewDate.setTextColor(context.getResources().getColor(R.color.beige));
            holder.textViewTime.setTextColor(context.getResources().getColor(R.color.beige));
            holder.textViewDescription.setTextColor(context.getResources().getColor(R.color.beige));
            holder.imageViewStar.setColorFilter(context.getResources().getColor(R.color.beige));
            holder.imageViewOption.setColorFilter(context.getResources().getColor(R.color.beige));
        }


        int pos = holder.getAdapterPosition();
        holder.imageViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.imageViewOption);
                popup.inflate(R.menu.menu_option);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                mListener.operation("Ändern", pos);
                                break;
                            case R.id.delete:
                                mListener.operation("Löschen", pos);
                                break;
                            case R.id.complete:
                                mListener.operation("Erledigen", pos);
                                break;
                            case R.id.favorite:
                                mListener.operation("Favorit", pos);
                                break;
                            case R.id.share:
                                mListener.operation("Teilen", pos);
                                break;
                            case R.id.Save:
                                mListener.operation("Speichern", pos);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

    }

    public interface onItemClickListener {
        void operation(String operationName, int position);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewOption, imageViewStar;
        TextView textViewDate, textViewName, textViewDescription, textViewTime;

        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            imageViewOption = itemView.findViewById(R.id.imageViewOption);
            imageViewStar = itemView.findViewById(R.id.imageViewStar);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}