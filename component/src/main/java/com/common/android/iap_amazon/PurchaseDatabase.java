package com.common.android.iap_amazon;

import java.util.ArrayList;
import java.util.List;

import com.common.android.PurchaseBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PurchaseDatabase {
	private static final String DATABASE_NAME = "purchase.db";
	private static final int DATABASE_VERSION = 1;

	private static final String PURCHASED_PRODUCT_ID_COL = "_id";
	private static final String PURCHASED_QUANTITY_COL = "quantity";
	private static final String PURCHASED_ITEMS_TABLE_NAME = "purchased";

	private DatabaseHelper helper;
	private SQLiteDatabase mDb;

	public PurchaseDatabase(Context context) {
		helper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		mDb = helper.getWritableDatabase();

	}

	public void close() {
		helper.close();
	}

	public void insert(List<PurchaseBean> catalogs) {
		for (PurchaseBean catalog : catalogs) {
			insert(catalog);
		}
	}

	public void insert(PurchaseBean catalog) {
		ContentValues values = new ContentValues();
		values.put(PURCHASED_PRODUCT_ID_COL, catalog.getSku());
		values.put(PURCHASED_QUANTITY_COL, 1);// 所有数量默认为1
		mDb.replace(PURCHASED_ITEMS_TABLE_NAME, null /* nullColumnHack */, values);
	}

	public List<PurchaseBean> queryAll() {
		Cursor cursor = mDb.query(PURCHASED_ITEMS_TABLE_NAME, new String[] { PURCHASED_PRODUCT_ID_COL, PURCHASED_QUANTITY_COL }, null, null, null, null, null);
		if (cursor == null)
			return null;
		List<PurchaseBean> catalogs = new ArrayList<PurchaseBean>();
		while (cursor.moveToNext()) {
			PurchaseBean p=new PurchaseBean(cursor.getString(0));
			catalogs.add(p);
		}
		cursor.close();
		return catalogs;
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + PURCHASED_ITEMS_TABLE_NAME + "(" + PURCHASED_PRODUCT_ID_COL + " TEXT PRIMARY KEY, " + PURCHASED_QUANTITY_COL + " INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + PURCHASED_ITEMS_TABLE_NAME);
			onCreate(db);
		}

	}

}
