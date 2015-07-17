package com.github.windsekirun.naraepicker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * NaraePicker
 * class: AlbumFragment
 * Created by WindSekirun on 2015. 7. 17..
 */
public class AlbumActivity extends AppCompatActivity {
    Toolbar toolbar;

    AlbumGridAdapter adapter;
    ArrayList<Album> itemSet = new ArrayList<>();

    EmptyRecyclerView list;
    ProgressBarCircularIndeterminate progressBar;
    GridLayoutManager mLayoutManager;
    int limit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        list = (EmptyRecyclerView) findViewById(R.id.list);
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);

        list.setEmptyView(progressBar);
        list.setHasFixedSize(true);

        adapter = new AlbumGridAdapter(itemSet);
        mLayoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(mLayoutManager);
        list.setAdapter(adapter);
        new LoadGalleryList().execute();

        toolbarInflate();

        limit = getIntent().getIntExtra("limit", 4);
    }

    public void toolbarInflate() {
        toolbar.setBackgroundColor(Material.getMaterialBlueColor(500));
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitle("사진을 선택해주세요.");
        setSupportActionBar(toolbar);
    }

    public class LoadGalleryList extends AsyncTask<Void, Void, Void> {
        ArrayList<Album> tempSet = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            HashSet<String> albumSet = new HashSet<>();
            File file;

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA}, null, null, MediaStore.Images.Media.DATE_ADDED);

            if (cursor.moveToLast()) {
                do {
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    file = new File(image);

                    if (file.exists() && !albumSet.contains(album)) {
                        tempSet.add(new Album(album, image));
                        albumSet.add(album);
                    }
                } while (cursor.moveToPrevious());
            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemSet.addAll(0, tempSet);
            adapter.notifyDataSetChanged();
            tempSet.clear();
            tempSet.trimToSize();
        }
    }

    public class ItemClickListener implements View.OnClickListener {
        int position;

        public ItemClickListener(int i) {
            position = i;
        }

        @Override
        public void onClick(View v) {
            Album album = itemSet.get(position);
            Intent imageIntent = new Intent(AlbumActivity.this, ImageActivity.class);
            imageIntent.putExtra("folderName", album.name);
            imageIntent.putExtra("limit", limit);
            startActivity(imageIntent);
            finish();
        }
    }

    public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumItem> {
        ArrayList<Album> dataSet;
        Context c;

        public AlbumGridAdapter(ArrayList<Album> itemSet) {
            dataSet = itemSet;
        }

        @Override
        public AlbumItem onCreateViewHolder(ViewGroup parent, int viewType) {
            c = parent.getContext();
            View v = LayoutInflater.from(c).inflate(R.layout.row_folder, parent, false);
            return new AlbumItem(v);
        }

        @Override
        public void onBindViewHolder(AlbumItem holder, int position) {
            holder.itemView.setOnClickListener(new ItemClickListener(position));
            Album data = dataSet.get(position);
            holder.name.setText(data.name);
            Glide.with(c).load(data.imagePath).thumbnail(0.5f).into(holder.thumbnail);
            holder.name.setTextColor(0xffffffff);
            holder.nameRoot.setBackgroundColor(Material.getMaterialBlueColor(700));
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    public class Album implements Parcelable {
        public String name;
        public String imagePath;

        public Album(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(imagePath);
        }

        public final Creator<Album> CREATOR = new Creator<Album>() {
            @Override
            public Album createFromParcel(Parcel source) {
                return new Album(source);
            }

            @Override
            public Album[] newArray(int size) {
                return new Album[size];
            }
        };

        private Album(Parcel in) {
            name = in.readString();
            imagePath = in.readString();
        }
    }

    public class AlbumItem extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView name;
        public RelativeLayout nameRoot;

        public AlbumItem(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            name = (TextView) itemView.findViewById(R.id.img_text);
            nameRoot = (RelativeLayout) itemView.findViewById(R.id.img_text_root);
        }
    }
}
