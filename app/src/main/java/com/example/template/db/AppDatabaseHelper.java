package com.example.template.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.template.model.Post;

import java.util.ArrayList;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_POSTS = "posts";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_LINK = "link";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_POSTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_BODY + " TEXT, " +
                COLUMN_LINK + " TEXT" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

    public boolean insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, post.getId());
        values.put(COLUMN_USER_ID, post.getUserId());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_BODY, post.getBody());
        values.put(COLUMN_LINK, post.getLink());

        long result = db.insert(TABLE_POSTS, null, values);

        return result != -1;
    }

    public ArrayList<Post> getAllPosts() {
        ArrayList<Post> posts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_POSTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY));
                String link = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK));

                posts.add(new Post(userId, title, link, body, id));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return posts;
    }

    public Post getFirstPost() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_POSTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " ASC",
                "1"
        );

        Post post = null;

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String body = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY));
            String link = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK));

            post = new Post(userId, title, link, body, id);
        }

        cursor.close();

        return post;
    }

    public boolean deleteFirstPost() {
        Post firstPost = getFirstPost();

        if (firstPost == null) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(
                TABLE_POSTS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(firstPost.getId())}
        );

        return rows > 0;
    }

    public int getPostCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_POSTS,
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public boolean isEmpty() {
        return getPostCount() == 0;
    }

    public void deleteAllPosts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, null, null);
    }
}