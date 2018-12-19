package com.example.dineshkumar.diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dineshkumar.diary.DB.DiaryDatabase;
import com.example.dineshkumar.diary.Model.Diary;

import java.util.ArrayList;

/**
 * Created by Dinesh Kumar on 2/22/2018.
 */

public class CustomHomeRecyclerAdapter extends RecyclerView.Adapter<CustomHomeRecyclerAdapter.MyViewHolders> {

    Context context;
    ArrayList<Diary> diaryArrayList;

    CustomHomeRecyclerAdapter( ArrayList<Diary> diaryArrayList,Context context)
    {
        this.diaryArrayList = diaryArrayList;
        this.context = context;
    }

    class MyViewHolders extends RecyclerView.ViewHolder
    {
        Context context;

        TextView titleTv;
        TextView categoryTv;
        TextView dateTv;

        public MyViewHolders(View itemView) {
            super(itemView);
            titleTv = (TextView)itemView.findViewById(R.id.homeDiaryTitle);
            dateTv = (TextView)itemView.findViewById(R.id.homeDiaryDate);
            categoryTv = itemView.findViewById(R.id.txtViewCategory);
            context = itemView.getContext();

            CardView homeCardView = (CardView)itemView.findViewById(R.id.homeCardView);


            homeCardView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent viewDiaryIntent = new Intent(context, ViewDiary.class);
                    viewDiaryIntent.putExtra("title",titleTv.getText().toString());
                    context.startActivity(viewDiaryIntent);
                }
            });

            homeCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ShowAlertDialog("Delete","Are you sure to delete it ?");
                    return true;
                }
            });
        }
        void ShowAlertDialog(String title,String msg)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteNote();
                            refreshHomeRecycler(MainActivity.orderBy);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        void deleteNote()
        {

            new DiaryDatabase(context).delete("title",titleTv.getText().toString());
            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_layout,parent,false);

        return new MyViewHolders(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolders holder, int position) {

        Diary diary = diaryArrayList.get(position);
        holder.titleTv.setText(diary.getTitle());
        holder.dateTv.setText(diary.getCreatedDate());
        holder.categoryTv.setText(diary.getCategory());
    }

    @Override
    public int getItemCount() {
        return diaryArrayList.size();
    }

    public void addData(ArrayList<Diary> diariesList)
    {
        diaryArrayList = diariesList;
    }

    public void refreshHomeRecycler(String orderBy)
    {
        Log.i("refresh","Data refreshed");
        ArrayList<Diary> diaryArrayList = new DiaryDatabase(context).getData(orderBy);
        this.addData(diaryArrayList);
        this.notifyDataSetChanged();
    }
}
