/*
 * Copyright 2015 Bruno Romeu Nunes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pawel.cardviewsswipes;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static CardViewAdapter mAdapter;
    private DatabaseHelper db;
    private ArrayList<String> mItems;
    private ArrayList<String> mImages;
    private ArrayList<Integer> mIds;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        final Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setExitTransition(slide);

        mItems = new ArrayList<>();
        mImages = new ArrayList<>();
        mIds = new ArrayList<>();

        db = new DatabaseHelper(this);
        Cursor cursor = db.getAllData();
        if(cursor.getCount() == 0) Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
        while(cursor.moveToNext()){
            mIds.add(Integer.parseInt(cursor.getString(0)));
            mItems.add(cursor.getString(1));
            mImages.add(cursor.getString(3));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mItems.add(String.format("Card number %02d", mItems.size()));
                //mImages.add(R.drawable.b);
                //mAdapter.notifyItemInserted(mItems.size() - 1);
                Intent intent = new Intent(getApplicationContext(), CastleEditActivity.class);
                startActivity(intent);
            }
        });

        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                //Toast.makeText(getApplicationContext(), "Tapped " + mItems.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CastleActivity.class);
                intent.putExtra("Id", mIds.get(position));
                startActivity(intent);
            }

            @Override
            public void onCardViewLongClick(View view, int position) {
                //Toast.makeText(getApplicationContext(), "Long pressed " + mItems.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CastleEditActivity.class);
                intent.putExtra("Id", mIds.get(position));
                startActivity(intent);
            }

        };

        mAdapter = new CardViewAdapter(mIds, mItems, mImages, itemTouchListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Integer result = db.delete(String.valueOf(mIds.get(position)));
                                    if(result != 0) {
                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    else{ Toast.makeText(getApplicationContext(), "NOT deleted", Toast.LENGTH_SHORT).show();}
                                    mIds.remove(position);
                                    mItems.remove(position);
                                    mImages.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Integer result = db.delete(String.valueOf(mIds.get(position)));
                                    if(result != 0) {
                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    else{ Toast.makeText(getApplicationContext(), "NOT deleted", Toast.LENGTH_SHORT).show();}
                                    mIds.remove(position);
                                    mItems.remove(position);
                                    mImages.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    /**
     * Interface for the touch events in each item
     */
    public interface OnItemTouchListener {
        /**
         * Callback invoked when the user Taps one of the RecyclerView items
         *
         * @param view     the CardView touched
         * @param position the index of the item touched in the RecyclerView
         */
        void onCardViewTap(View view, int position);

        void onCardViewLongClick(View view, int position);

    }

    /**
     * A simple adapter that loads a CardView layout with one TextView and two Buttons, and
     * listens to clicks on the Buttons or on the CardView
     */
    public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
        private List<String> cardsText;
        private List<String> cardsImages;
        private List<Integer> cardsIDs;
        private OnItemTouchListener onItemTouchListener;

        public CardViewAdapter(List<Integer> cardsIDs, List<String> cardsText, List<String> cardsImages, OnItemTouchListener onItemTouchListener) {
            this.cardsText = cardsText;
            this.cardsImages = cardsImages;
            this.cardsIDs = cardsIDs;
            this.onItemTouchListener = onItemTouchListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_layout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.title.setText(cardsText.get(i));
            new DownloadImageTask(viewHolder.image).execute(cardsImages.get(i));
            viewHolder.ID = cardsIDs.get(i);
            //viewHolder.image.setImageResource(R.drawable.a);
        }

        @Override
        public int getItemCount() {
            return cardsText == null ? 0 : cardsText.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            private ImageView image;
            private int ID;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.card_view_title);
                image = (ImageView) itemView.findViewById(R.id.imageView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemTouchListener.onCardViewLongClick(v, getLayoutPosition());
                        return true;
                    }
                });

            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}

