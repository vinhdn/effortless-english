package effortlessenglish.estorm.vn.effortlessenglish.Database;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import effortlessenglish.estorm.vn.effortlessenglish.Models.Models;


public class LocalStorage {
	
	private static final String TAG = LocalStorage.class.getName();
	private static final long FIFTY_MB = 1024L * 1024L * 50L;
	
	private Context context;
	private final LocalStorageDB lsDb;
	
	private LocalStorage(Context context){
		this.context = context;
		lsDb = new LocalStorageDB(context);
		lsDb.open();
	}

    public ArrayList<Models> getListsModelsOfParent(Models parent, int type){
        return lsDb.getListModels(parent,type);
    }

    public void insertModels(Models models){
        lsDb.insertModels(models);
    }

    public Models getModels(int id){
        return lsDb.getModels(id);
    }

	public void removeAllData() throws StorageUnavailableException {
		Log.d(TAG, "removeAllData()");
		checkLocalStorageState();
		lsDb.removeAllData();
	}

	private void checkLocalStorageState() throws StorageUnavailableException {
		// String externalStorageState = Environment.getExternalStorageState();
		// if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
		// throw new
		// StorageUnavailableException("Local storage is not available to the app.",
		// externalStorageState);
		// }
	}

	private void checkIfLocalStorageIsFull() throws StorageUnavailableException {
		long freeSpaceLeft = getFreeSpace(context);
		if (freeSpaceLeft < FIFTY_MB) {
			throw new OutOfStorageSpaceException(
					"There is less than 50MB of storage space left on external storage device. So the app considers it as being full.");
		}
	}

	/**
	 * Singleton created by the application.
	 * 
	 * @throws StorageUnavailableException
	 */
	public static LocalStorage create(Context app) throws StorageUnavailableException {
		if (app.getExternalCacheDir() == null) {
			throw new StorageUnavailableException("Local storage is not available to the app.", Environment.getExternalStorageState());
		} else {
			return new LocalStorage(app);
		}
	}

	public static long getFreeSpace(Context context) throws StorageUnavailableException {
		File externalCacheDir = context.getExternalCacheDir();
		if (externalCacheDir == null) {
			throw new StorageUnavailableException("Local storage is not available to the app.", Environment.getExternalStorageState());
		} else {
			StatFs stat = new StatFs(externalCacheDir.getPath());
			long spaceAvailSize = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
			// One binary gigabyte equals 1,073,741,824 bytes.
			// long gigaAvailable = spaceAvailSize / 1073741824;
			return spaceAvailSize;
		}
	}
}
