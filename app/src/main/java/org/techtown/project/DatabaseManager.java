package org.techtown.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
//sqlite 저장 (firebase되면 안쓸듯)
public class DatabaseManager extends SQLiteOpenHelper {

    //ArrayList<UrlList> urls = new ArrayList<UrlList>();
    //singleton
    private static DatabaseManager instance;
    public static synchronized DatabaseManager getInstance(Context context){
        if (instance==null){
            instance = new DatabaseManager(context,"URLLIST",null,1);
        }
        return instance;
    }
    public DatabaseManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE URLLIST(url TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS URLLIST");
        //onCreate(db);
    }

    public void insertData(ArrayList<UrlList> urls){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(UrlList entity : urls){
            values.put("URL",entity.getUrl());

            db.insert("URLLIST",null,values);
        }
        db.close();
    }


    public ArrayList<UrlList> getItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT url FROM URLLIST",null);
        ArrayList<UrlList> list = new ArrayList<>();
        while (cursor.moveToNext()){
            UrlList entity = new UrlList();
            entity.setUrl(cursor.getString(0));

            list.add(entity);
        }
        return list;
    }
}
