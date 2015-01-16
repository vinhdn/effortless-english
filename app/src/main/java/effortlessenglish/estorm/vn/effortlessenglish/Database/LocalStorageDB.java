package effortlessenglish.estorm.vn.effortlessenglish.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import effortlessenglish.estorm.vn.effortlessenglish.Models.Models;

/**
 * @author Vinh
 *
 */
public class LocalStorageDB {
	
	public static final String TAG = LocalStorageDB.class.getName(); 
	
	private Context mContext;
	
	private static final String DATABASE_NAME = "effortless.db";
	private static final int DATABASE_VERSION = 1;
	private DBOpenHelper dbOpenHelper;
	private SQLiteDatabase database;
	
	/*
	 * Table Name
	 */
	private static final String TABLE_MODEL = "model";
    private static final String TABLE_USER = "user";

    /**
	 * The followings are the available columns in table 'user':
	 * @var integer $id
	 * @var integer $fb_id
	 */

	private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DES = "des";
    private static final String COLUMN_ID_PAR = "id_par";
    private static final String COLUMN_LIKES = "likes";
    private static final String COLUMN_LINK = "link";

    /**
     * @param context
     * 				of application
     */
    /**
     * @param context
     */
    public LocalStorageDB(Context context){
        this.mContext = context;
        this.dbOpenHelper = new DBOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public Models getModelFromCursor(Cursor cursor){
        Models models = new Models();
        models.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        models.setType(Models.TYPE_MODEL.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE))]);
        models.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        models.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DES)));
        models.setIdParMenu(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PAR)));
        models.setLikes(cursor.getInt(cursor.getColumnIndex(COLUMN_LIKES)));
        models.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_LINK)));
        return models;
    }

    public Models getModels(int id){
        Cursor cursor = null;
        try{
            cursor = database.query(TABLE_MODEL,null,
                    COLUMN_ID + " = " + id,null,null,null,null);
            cursor.moveToFirst();
            if(cursor != null){
                Models models = getModelFromCursor(cursor);
                cursor.close();
                return models;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new Models();
    }

    public ArrayList<Models> getListModels(Models parent, int type){
        ArrayList<Models> listData = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = database.query(TABLE_MODEL,null,
                    COLUMN_ID_PAR + " = " + parent.getId() +
                    " AND " + COLUMN_TYPE + " = " + type,null,null,null,null);
            cursor.moveToFirst();
            if(cursor != null){
                do{
                    Models models = getModelFromCursor(cursor);
                    models.setParent(parent);
                    listData.add(models);
                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            listData = new ArrayList<>();
            ex.printStackTrace();
        }
        return listData;
    }

	public void insertModels(Models data){
		try{
			synchronized (this) {
				database.beginTransaction();
				try{
					database.insertWithOnConflict(TABLE_MODEL, null, getCVFromModel(data),SQLiteDatabase.CONFLICT_REPLACE);
					database.setTransactionSuccessful();
				}finally{
					database.endTransaction();
				}
			}
		}catch(SQLException e){
			Log.e("SQLException", e.getMessage());
		}
	}

    private ContentValues getCVFromModel(Models models){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, models.getId());
        values.put(COLUMN_TYPE, models.getType().getValue());
        values.put(COLUMN_NAME, models.getName());
        values.put(COLUMN_DES, models.getDescription());
        values.put(COLUMN_ID_PAR,models.getIdParMenu());
        values.put(COLUMN_LIKES,models.getLikes());
        values.put(COLUMN_LINK,models.getLink());
        return values;
    }

	public void removeTableData(String table) {
		try {
			synchronized (this) {
				database.beginTransaction();
				try {
					database.delete(table, null, null);
					database.setTransactionSuccessful();
				} finally {
					database.endTransaction();
				}
			}
		} catch (SQLException e) {
			handleSqlException(e);
		}
	}
	
	public void removeAllData() {
		try {
			synchronized (this) {
				database.beginTransaction();
				try {
					database.delete(TABLE_MODEL, null, null);
					database.setTransactionSuccessful();
				} finally {
					database.endTransaction();
				}
			}
		} catch (SQLException e) {
			handleSqlException(e);
		}
	}

	/**
	 * Open a writable instance of the database.
	 * 
	 * @throws android.database.sqlite.SQLiteException
	 */
	protected void open() throws SQLiteException {
		database = dbOpenHelper.getWritableDatabase();
		System.out.println("open database = " + database);
	}

	/**
	 * Close the database.
	 */
	protected void close() {
		database.close();
		database = null;
		System.out.println("close database = " + database);
	}

	private void closeAndOpenWritable() {
		try {
			synchronized (this) {
				close();
				open();
			}
		} catch (SQLException e) {
			Log.d(getClass().getName(), "Fatal error, unable to open db!");
			e.printStackTrace();
		}
	}

	private final void handleSqlException(SQLException e) {
		Log.e(LocalStorageDB.class.getName(), "handleSqlException() :: SQL Exception detected:");
		Log.e(LocalStorageDB.class.getName(), "handleSqlException() :: Cache dir: " + mContext.getExternalCacheDir());
		Log.e(LocalStorageDB.class.getName(), "handleSqlException() :: External storage state: " + Environment.getExternalStorageState());
		try {
			Log.e(LocalStorageDB.class.getName(), "handleSqlException() :: Free space: " + LocalStorage.getFreeSpace(mContext));
		} catch (StorageUnavailableException e1) {
			e1.printStackTrace();
		}
		Log.e(LocalStorageDB.class.getName(), "handleSqlException() :: printing exception ...");
		e.printStackTrace(System.err);
		closeAndOpenWritable();
	}
	
	private class DBOpenHelper extends SQLiteOpenHelper {

		// SQL Statements to create new tables:

        private static final String CREATE_TABLE_MODEL = "create table "
                + TABLE_MODEL + " ("
                + COLUMN_ID + " integer primary key,"
                + COLUMN_TYPE + " integer not null,"
                + COLUMN_NAME + " text,"
                + COLUMN_DES + " text,"
                + COLUMN_ID_PAR + " integer,"
                + COLUMN_LIKES + " integer,"
                + COLUMN_LINK + " text);";

		/**
		 * Default constructor.
		 * 
		 * @param context
		 *            to use to open or create the database
		 * @param name
		 *            of the database file, or null for an in-memory database
		 * @param factory
		 *            to use for creating cursor objects, or null for the
		 *            default
		 * @param version
		 *            database version number
		 */
		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		/**
		 * Called when the database is first created.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_MODEL);
		}

		/**
		 * Called when database needs an update.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODEL);
			SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
			mPref.edit().putBoolean("isFirst", true);
			mPref.edit().commit();
			onCreate(db);
		}
	}
}
