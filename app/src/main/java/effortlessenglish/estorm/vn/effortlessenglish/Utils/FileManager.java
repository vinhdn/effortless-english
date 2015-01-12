package effortlessenglish.estorm.vn.effortlessenglish.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * The class provides some method used to create file or folder in SDCard
 * 
 */
public class FileManager {

	public static final File EXTERNAL_FOLDER = Environment
			.getExternalStorageDirectory();
	public static final File ROOT_FOLDER = Environment.getRootDirectory();
	public static final File DATA_FOLDER = Environment.getDataDirectory();
	public static final File CACHE_DOWNLOAD_FOLDER = Environment
			.getDownloadCacheDirectory();
	public static final String EXTERNAL_PATH = EXTERNAL_FOLDER.getPath();
	public static final String ASSETS_PATH = "file:///android_asset/";

	public static final int FILE_NOT_FOUND = 1;
	public static final int IO_EXCEPTION = 2;
	public static final int OK = 3;

	/**
	 * Delete file or directory
	 */
	public static void removeFolder(File fileOrDirectory) {
		try {
			if (fileOrDirectory.isDirectory()) {
				for (File file : fileOrDirectory.listFiles()) {
					removeFolder(file);
				}
			}

			fileOrDirectory.delete();
		} catch (Exception e) {
			Log.e(e.toString());
		}
	}

	/**
	 * Created a new folder in SD card
	 * 
	 * @param nameOfFolder
	 *            name of folder is created
	 * @return Return true if create folder successfully, otherwise return false
	 */
	public static boolean createNewFolder(String nameOfFolder) {
		File newfolder = new File(EXTERNAL_FOLDER, nameOfFolder);
		return newfolder.mkdirs();
	}

	/**
	 * Created a new folder in SD card
	 * 
	 * @param parentPath
	 *            the path of folder parent contains its
	 * @param nameOfFolder
	 *            name of folder is created
	 * @return Return true if create folder successfully, otherwise return false
	 */
	public static boolean createNewFolder(String parentPath, String nameOfFolder) {
		File newfolder = new File(parentPath, nameOfFolder);
		return newfolder.mkdirs();
	}

	/**
	 * Create a new file in SD card
	 * 
	 * @param pathOfFile
	 *            The path of file is created
	 * @return true if the file has been created, false if it already exists.
	 */
	public static boolean createNewFile(String pathOfFile) {
		File file = new File(pathOfFile);
		try {
			return file.createNewFile();
		} catch (IOException e) {
			Log.d(e.toString());
		}
		return false;
	}

	/**
	 * Create a new file using the specified directory and name in SD card
	 * 
	 * @param dir
	 *            : directory of specified folder
	 * @param nameOfFile
	 *            : name of file is created
	 * @return true if the file has been created, false if it already exists.
	 */
	public static boolean createNewFile(File dir, String nameOfFile) {
		File file = new File(dir, nameOfFile);
		try {
			return file.createNewFile();
		} catch (IOException e) {
			Log.d(e.toString());
		}
		return false;
	}

	/**
	 * Open a directory in external storage (such as SD card)
	 * 
	 * @param dirName
	 * @param createIfNotExist
	 * @return
	 */
	public static File openExternalDirectory(String dirName,
			boolean createIfNotExist) {
		File directory = null;
		try {
			Log.d("Open external directory: " + dirName);
			directory = new File(EXTERNAL_PATH + File.separator + dirName);
			if (createIfNotExist) {
				directory.mkdirs();
			}
		} catch (Exception e) {
			Log.d("Can not create directory. Please check your SD card.");
		}
		return directory;
	}

	/**
	 * Open a directory within application data folder. If the directory does
	 * not exist, this method will create it before returning File handle to the
	 * directory. If the directory exists, this method simply return File handle
	 * of the directory.
	 * 
	 * @param dirName
	 * @param packageName
	 * @param createIfNotExist
	 * @return
	 * @throws IOUtilException
	 */
	@SuppressLint("SdCardPath")
	public static File openDataDir(String dirName, String packageName,
			boolean createIfNotExist) {
		File dir = null;
		try {
			String path = "/data/data/" + packageName + "/databases/" + dirName;
			Log.d("Open data directory: " + path);
			dir = new File(path);
			if (createIfNotExist) {
				dir.mkdirs();
			}
		} catch (Exception e) {
		}

		return dir;
	}

	/**
	 * Get file name in path of file
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileName(String pathOfFile) {
		int slashIndex = pathOfFile.lastIndexOf('/');
		int dotIndex = pathOfFile.length();
		String filenameWithoutExtension;
		if (dotIndex == -1) {
			filenameWithoutExtension = pathOfFile.substring(slashIndex + 1);
		} else {
			filenameWithoutExtension = pathOfFile.substring(slashIndex + 1,
					dotIndex);
		}
		return filenameWithoutExtension;
	}

	/**
	 * write text to file
	 * 
	 * @param file
	 * @param text
	 */
	public static void write(File file, String text) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(text.getBytes());
			outputStream.flush();
			outputStream.close();
			Log.d("write file success");
		} catch (FileNotFoundException e) {
			Log.e(e.toString());
		} catch (IOException e) {
			Log.e(e.toString());
		}
	}

	/**
	 * read content in file
	 * 
	 * @param path
	 * @return
	 */
	public static String read(String path) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(new File(path));
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} catch (FileNotFoundException e) {
			Log.e(e.toString());
		} catch (IOException e) {
			Log.e(e.toString());
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(e.toString());
			}
		}
		return null;
	}

	/**
	 * zip file
	 * 
	 * @param fileToZip
	 * @param fileZip
	 * @return
	 */
	public static int zipFile(File fileToZip, File fileZip) {
		ZipOutputStream zipOutputStream = null;
		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(fileToZip.getPath());

			zipOutputStream = new ZipOutputStream(new FileOutputStream(fileZip));
			zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
			zipOutputStream.putNextEntry(new ZipEntry(fileToZip.getName()));

			copyInputStream(fileInputStream, zipOutputStream);

			zipOutputStream.closeEntry();
			zipOutputStream.close();
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			return FILE_NOT_FOUND;
		} catch (IOException e) {
			return IO_EXCEPTION;
		}
		return OK;
	}

	/**
	 * unzip file
	 * 
	 * @param fileName
	 */
	public static void unzipFile(String fileName) {
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			ZipFile zipFile = new ZipFile(fileName);

			Enumeration<?> e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if (entry.isDirectory()) {
					createNewFile(entry.getName());
					continue;
				}

				fileInputStream = new FileInputStream(entry.getName());
				fileOutputStream = new FileOutputStream(zipFile.getName());
				copyInputStream(fileInputStream, fileOutputStream);
			}
			zipFile.close();
		} catch (IOException e) {
		}
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @throws java.io.IOException
	 */
	public static void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}

	/**
	 * Save bitmap to file
	 * 
	 * @param bmp
	 *            bitmap
	 * @param uriFile
	 *            file is saved
	 * @return
	 */
	public static boolean saveBitmapToFile(Bitmap bmp, String uriFile) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 90, outputStream);

			File file = new File(uriFile);
			if (!file.createNewFile()) {
				return false;
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(outputStream.toByteArray());
			fileOutputStream.close();

			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Get all files same type in folder
	 * 
	 * @param parentDir
	 * @param mime
	 *            MIME of file, get all files if mime empty
	 * @return list files
	 */
	public static List<File> getListFilesWithExt(File parentDir, String mime) {
		ArrayList<File> inFiles = new ArrayList<File>();

		if (parentDir == null) {
			return inFiles;
		}

		File[] files = parentDir.listFiles();
		if (files == null) {
			return inFiles;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListFilesWithExt(file, mime));
			} else {
				if (!TextUtils.isEmpty(mime)) {
					if (file.getName().endsWith("." + mime)) {
						inFiles.add(file);
					}
				} else {
					//add all file
					inFiles.add(file);
				}
			}
		}
		return inFiles;
	}

	public static List<File> getListFilesWithExt(String parentDirPath,
			String mime) {
		File parentDir = new File(parentDirPath);
		return getListFilesWithExt(parentDir, mime);
	}
	
	public static List<File> getListFiles(String parentDirPath) {
		return getListFilesWithExt(parentDirPath, "");
	}

	/**
	 * @param source
	 * @param dest
	 * @return false if can't copy
	 */
	public static boolean copyFile(File source, File dest) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(source));
			bos = new BufferedOutputStream(new FileOutputStream(dest, false));

			byte[] buf = new byte[1024];
			bis.read(buf);

			do {
				bos.write(buf);
			} while (bis.read(buf) != -1);
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}

	public static boolean copyFile(String source, String dest) {
		File fileS = new File(source);
		File fileD = new File(dest);
		return copyFile(fileS, fileD);
	}

	/**
	 * Returns true if the sdcard is mounted rw
	 * 
	 * @return true if the sdcard is mounted rw
	 */
	public static boolean isSDMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
}
