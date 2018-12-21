package com.example.dineshkumar.diary;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dineshkumar.diary.Model.Diary;

import java.util.ArrayList;

/**
 * Created by Dinesh Kumar on 2/22/2018.
 */

public class CustomHomeRecyclerAdapter extends RecyclerView.Adapter<CustomHomeRecyclerAdapter.MyViewHolders> {

    Context context;
    ArrayList<Diary> diaryArrayList;
    private OnCardClickListener _cardListener;

    CustomHomeRecyclerAdapter(Context context, ArrayList<Diary> diaryArrayList, OnCardClickListener cardListener) {
        _cardListener = cardListener;
        this.diaryArrayList = diaryArrayList;
        this.context = context;
    }


    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_layout, parent, false);

        return new MyViewHolders(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolders holder, int position) {

        final Diary diary = diaryArrayList.get(position);
        holder.titleTv.setText(diary.getTitle());
        holder.dateTv.setText(diary.getCreatedDate());
        holder.categoryTv.setText(diary.getCategory());

        holder.homeCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _cardListener.onClick(diary);
               /* Intent viewDiaryIntent = new Intent(context, ViewDiary.class);
                viewDiaryIntent.putExtra("title", diary.getTitle());
                context.startActivity(viewDiaryIntent);*/
            }
        });

        holder.homeCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                _cardListener.onLongClick(diary);
                /*ShowAlertDialog("Delete", "Are you sure to delete it ?");*/
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryArrayList.size();
    }

    public void addData(ArrayList<Diary> diariesList) {
        diaryArrayList = diariesList;
    }

    class MyViewHolders extends RecyclerView.ViewHolder {
        Context context;

        TextView titleTv;
        TextView categoryTv;
        TextView dateTv;
        CardView homeCardView;

        public MyViewHolders(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.homeDiaryTitle);
            dateTv = (TextView) itemView.findViewById(R.id.homeDiaryDate);
            categoryTv = itemView.findViewById(R.id.txtViewCategory);
            context = itemView.getContext();
            homeCardView = itemView.findViewById(R.id.homeCardView);
        }



    }

    public interface OnCardClickListener {
        void onClick(Diary diary);

        void onLongClick(Diary diary);
    }

}
