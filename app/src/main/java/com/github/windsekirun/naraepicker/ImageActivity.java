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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

/**
 * NaraePicker
 * class: ImageActivity
 * Created by WindSekirun on 2015. 7. 17..
 */
public class ImageActivity extends AppCompatActivity {
    Toolbar toolbar;
    EmptyRecyclerView list;
    ProgressBarCircularIndeterminate progressBar;
    GridLayoutManager mLayoutManager;
    FloatingActionButton fab;

    String albumName;
    int limit;
    ArrayList<Image> itemSet = new ArrayList<>();
    ImageGridAdapter adapter;

    ArrayList<String> fileList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        list = (EmptyRecyclerView) findViewById(R.id.list);
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);

        list.setEmptyView(progressBar);
        list.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(mLayoutManager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(list);
        fab.setColorNormal(Material.getMaterialBlueColor(500));
        fab.setColorPressed(Material.getMaterialBlueColor(700));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fileList.isEmpty()) {
                    if (!(fileList.size() > limit)) {
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("images", fileList);
                        Toast.makeText(ImageActivity.this, "정상 작동합니다. 로그캣 참조하세요.", Toast.LENGTH_SHORT).show();
                        Log.d("NaraePicker", "Selected Image Count: " + fileList.size());
                        for (String file : fileList)
                            Log.d("NaraePicker", "Selected Image: " + file);
                    } else {
                        Toast.makeText(ImageActivity.this, "최대 사진 첨부 갯수를 초과하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageActivity.this, "보낼 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        limit = getIntent().getIntExtra("limit", 4);
        albumName = getIntent().getStringExtra("folderName");
        toolbarInflate();

        adapter = new ImageGridAdapter(itemSet);
        list.setAdapter(adapter);

        new LoadGalleryList().execute();
    }

    @SuppressWarnings("ConstantConditions")
    public void toolbarInflate() {
        toolbar.setBackgroundColor(Material.getMaterialBlueColor(500));
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitle(albumName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent imageIntent = new Intent(ImageActivity.this, AlbumActivity.class);
        imageIntent.putExtra("limit", limit);
        startActivity(imageIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoadGalleryList extends AsyncTask<Void, Void, Void> {
        ArrayList<Image> tempSet = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            File file;
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{albumName}, MediaStore.Images.Media.DATE_ADDED);
            if (cursor.moveToLast()) {
                do {
                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    file = new File(image);
                    if (file.exists())
                        tempSet.add(new Image(image));
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

            toolbar.setTitle(albumName + " (" + itemSet.size() + ") ");
        }
    }

    public class ItemClickListener implements View.OnClickListener {
        int position;
        ImageView check;

        public ItemClickListener(int i, ImageView c) {
            position = i;
            check = c;
        }

        @Override
        public void onClick(View v) {
            String filePath = itemSet.get(position).imagePath;
            Log.d("NaraePicker", "select image: " + filePath);
            if (fileList.contains(filePath)) {
                check.setImageResource(R.drawable.check_blank);
                fileList.remove(filePath);
                fileList.trimToSize();
            } else {
                check.setImageResource(R.drawable.check_ok);
                fileList.add(filePath);
            }
        }
    }

    public class ImageGridAdapter extends RecyclerView.Adapter<ImageItem> {
        ArrayList<Image> dataSet;
        Context c;

        public ImageGridAdapter(ArrayList<Image> itemSet) {
            dataSet = itemSet;
        }

        @Override
        public ImageItem onCreateViewHolder(ViewGroup parent, int viewType) {
            c = parent.getContext();
            View v = LayoutInflater.from(c).inflate(R.layout.row_image, parent, false);
            return new ImageItem(v);
        }

        @Override
        public void onBindViewHolder(ImageItem holder, int position) {
            holder.itemView.setOnClickListener(new ItemClickListener(position, holder.check));
            Image data = dataSet.get(position);
            File file = new File(data.imagePath);
            Glide.with(ImageActivity.this).load(file).thumbnail(0.5f).into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    public class Image implements Parcelable {
        public String imagePath;
        public boolean isSelected;

        public Image(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(imagePath);
            dest.writeByte((byte) (isSelected ? 1 : 0));
        }

        public final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel source) {
                return new Image(source);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };

        private Image(Parcel in) {
            imagePath = in.readString();
            isSelected = in.readByte() != 0;
        }
    }

    public class ImageItem extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public ImageView check;

        public ImageItem(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            check = (ImageView) itemView.findViewById(R.id.img_check);
        }
    }
}
