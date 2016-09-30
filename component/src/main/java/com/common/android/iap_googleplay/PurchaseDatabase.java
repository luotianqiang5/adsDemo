/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.android.iap_googleplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class PurchaseDatabase {
    private static final String TAG = "PurchaseDatabase";
    private static final String DATABASE_NAME = "purchase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String PURCHASED_ITEMS_TABLE_NAME = "purchased";

    // These are the column names for the purchase history table. We need a
    // column named "_id" if we want to use a CursorAdapter. The primary key is
    // the orderId so that we can be robust against getting multiple messages
    // from the server for the same purchase.
    static final String HISTORY_ORDER_ID_COL = "_id";
    static final String HISTORY_STATE_COL = "state";
    static final String HISTORY_PRODUCT_ID_COL = "productId";
    static final String HISTORY_PURCHASE_TIME_COL = "purchaseTime";
    static final String HISTORY_DEVELOPER_PAYLOAD_COL = "developerPayload";

    private static final String[] HISTORY_COLUMNS = {
        HISTORY_ORDER_ID_COL, HISTORY_PRODUCT_ID_COL, HISTORY_STATE_COL,
        HISTORY_PURCHASE_TIME_COL, HISTORY_DEVELOPER_PAYLOAD_COL
    };

    // These are the column names for the "purchased items" table.
    static final String PURCHASED_PRODUCT_ID_COL = "_id";
    static final String PURCHASED_QUANTITY_COL = "quantity";

    private static final String[] PURCHASED_COLUMNS = {
        PURCHASED_PRODUCT_ID_COL, PURCHASED_QUANTITY_COL
    };

    private SQLiteDatabase mDb;
    private DatabaseHelper mDatabaseHelper;

    public PurchaseDatabase(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mDb = mDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        mDatabaseHelper.close();
    }

    

    /**
     * 更新数据库中已有的产品信息,如果原始数据库中没有该产品,则新增一条
     * @param productId
     * @param quantity  产品数量，如果为0，会从数据库中删除该产品
     */
    public void updatePurchasedItem(String productId, int quantity) {
        if (quantity == 0) {
            mDb.delete(PURCHASED_ITEMS_TABLE_NAME, PURCHASED_PRODUCT_ID_COL + "=?",
                    new String[] { productId });
            return;
        }
        ContentValues values = new ContentValues();
        values.put(PURCHASED_PRODUCT_ID_COL, productId);
        values.put(PURCHASED_QUANTITY_COL, quantity);
        mDb.replace(PURCHASED_ITEMS_TABLE_NAME, null /* nullColumnHack */, values);
    }


    /**
     * 查询数据库中所有产品信息
     * @return
     */
    public Cursor queryAllPurchasedItems() {
        return mDb.query(PURCHASED_ITEMS_TABLE_NAME, PURCHASED_COLUMNS, null,
                null, null, null, null);
    }

    /**
     * This is a standard helper class for constructing the database.
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createPurchaseTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Production-quality upgrade code should modify the tables when
            // the database version changes instead of dropping the tables and
            // re-creating them.
            if (newVersion != DATABASE_VERSION) {
                Log.w(TAG, "Database upgrade from old: " + oldVersion + " to: " +
                    newVersion);
                db.execSQL("DROP TABLE IF EXISTS " + PURCHASED_ITEMS_TABLE_NAME);
                createPurchaseTable(db);
                return;
            }
        }

        private void createPurchaseTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PURCHASED_ITEMS_TABLE_NAME + "(" +
                    PURCHASED_PRODUCT_ID_COL + " TEXT PRIMARY KEY, " +
                    PURCHASED_QUANTITY_COL + " INTEGER)");
        }
    }
}
