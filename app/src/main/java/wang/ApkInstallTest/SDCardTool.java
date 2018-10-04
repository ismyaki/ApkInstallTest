package wang.ApkInstallTest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SDCardTool {
	/** 
	 * 拍照資料夾 <br>
	 * 檔案 : imageLoad 暫存資料
	 * */
	public static String FOLDER_PICTURES = "Pictures";
	/** 
	 * 下載資料夾 <br>
	 * 檔案 : 輪播,小圖,圖框
	 * */
	public static String FOLDER_DOWNLOADS = "Download";
	/** 
	 * 快取資料夾 <br>
	 * 檔案 : imageLoad 暫存資料
	 * */
	public static String FOLDER_CACHE = "Cache";
	/** 
	 * 離線播放資料夾 <br>
	 * */
	public static String FOLDER_PLAY = "play";
	/** 
	 * 設定圖片資料夾 <br>
	 * */
	public static String FOLDER_SETTING = "setting";
	/** 資料夾名稱列表*/
	public static String[] FOLDER_ALL = {
		FOLDER_PICTURES,
		FOLDER_DOWNLOADS,
		FOLDER_CACHE,
		FOLDER_PLAY,
		FOLDER_SETTING
	};
	
	/**
	 * 尋找指定資料夾內是否有指定檔案 <br>有則回傳路徑無則回傳null
	 * @param context
	 * @param folderName 資料夾名稱
	 * @param fileName 檔案名稱
	 * @return
	 * @author Wang
	 * @date 2016/1/20 下午8:54:26
	 * @version 
	 */
	public static String getFilePath(Context context , String folderName , String fileName){
		String path = getPath(context, folderName);
		String uri = path + fileName;
		if (checkFile(uri) == true) {
			return uri;
		}else{
			return null;
		}
	}
	
	/**
	 * 尋找指定資料夾內是否有指定檔案 <br>有則回傳路徑無則回傳null
	 * @param context
	 * @param url 檔案名稱
	 * @return
	 * @author Wang
	 * @date 2016/1/20 下午8:54:26
	 * @version 
	 */
	public static String getDownloadFilePath(Context context , String url){
		String path = getPath(context, FOLDER_DOWNLOADS);
		String uri = path + formetURLname(url);
		if (checkFile(uri) == true) {
			return uri;
		}else{
			return null;
		}
	}
	
	/**
	 * 離線播放路徑
	 * @param context
	 * @param url
	 * @return
	 * @author Wang
	 * @date 2016/7/5 下午7:08:41
	 * @version 
	 */
	public static String getOfflinePlayFilePath(Context context){
		String path = getSDCard0Path(context , FOLDER_PLAY);;
		
		return path;
	}
	
	/**
	 * 設定圖片資料夾路徑
	 * @param context
	 * @return
	 * @author Wang
	 * @date 2016/8/30 下午2:01:42
	 * @version 
	 */
	public static String getSettingFilePath(Context context){
		String path = getSDCardPath(context , FOLDER_SETTING);;
		
		return path;
	}
	
	/**
	 * 有SDCard會回應SDCard路徑 ex : file:///data/data/pro.realtouchapp.cspread/files/<br>
	 * 否則則回應 sdcard0 ex : /storage/emulated/0/cspread/<br>
	 * 都沒有則回應 內存路徑
	 */
	public static String getPath(Context context){
		return getPath(context, null);
	}
	
	/**
	 * 有SDCard會回應SDCard路徑 ex : file:///data/data/pro.realtouchapp.cspread/files/filderName/<br>
	 * 否則則回應 sdcard0 ex : /storage/emulated/0/cspread/filderName/<br>
	 * 都沒有則回應 內存路徑
	 * @param context
	 * @param folderName 資料夾名稱
	 */
	public static String getPath(Context context , String folderName){
		String path = null;
		
		if (ExistSDCard() == true) {
			path = getSDCardPath(context , folderName);
		}else{
			path = getSDCard0Path(context , folderName);
		}
		
		if (path == null) {
			path = getInteralPath(context);
		}
		return path;
	}
	
	/**
	 * 內存路徑 ex : file:///data/data/pro.realtouchapp.cspread/files/<br>
	 * 建議使用 getPath(Context context)
	 */
	public static String getInteralPath(Context context){
		String path = context.getFilesDir().getAbsolutePath();
		return path;
	}
	
	/**
	 * sdcard0 路徑 ex : /storage/emulated/0/cspread/<br>
	 * 建議使用 getPath(Context context)
	 */
	public static String getSDCard0Path(Context context){
		return getSDCard0Path(context , null);
	}
	
	/**
	 * sdcard0 路徑 ex : /storage/emulated/0/cspread/<br>
	 * 建議使用 getPath(Context context)
	 * @param context
	 * @param folderName 資料夾名稱
	 */
	public static String getSDCard0Path(Context context , String folderName){
		String path = null;
		File file = Environment.getExternalStorageDirectory();
		if (file != null) {
			path = file.getAbsolutePath() + "/" + context.getString(R.string.system) + "/";
			if (folderName != null) {
				path += folderName + "/";
			}
			if (checkFolder(path) == false) {
				File folder = new File(path);
				folder.mkdir();
			}
		}
		return path;
	}
	
	/**
	 * SDCard 路徑 ex : /storage/MicroSD/cspread/<br>
	 * 建議使用 getPath(Context context)
	 */
	public static String getSDCardPath(Context context){
		return getSDCardPath(context, null);
	}
	
	/**
	 * SDCard 路徑 ex : /storage/MicroSD/cspread/<br>
	 * @param context
	 * @param folderName 資料夾名稱
	 */
	@SuppressLint("NewApi")
	public static String getSDCardPath(Context context , String folderName){
		String path = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			File fileArray[] = context.getExternalFilesDirs(folderName);
			if (fileArray != null && fileArray.length > 0) {
				File file = null;
				if (fileArray != null && fileArray.length > 0) {
					for(int i = fileArray.length - 1 ; i > -1 ; i--){
						file = fileArray[i];
						if (file != null && file.canRead() == true && file.canWrite() == true) {
							//需可讀可寫才可用
							break;
						}
					}
				}
				
				if (file != null) {
					path = file.getAbsolutePath() + "/";
				}else{
					path = getSDCard0Path(context, folderName);
				}
			}else{
				path = getSDCard0Path(context, folderName);
			}
		}else{
			path = getSDCard0Path(context, folderName);
		}
		
		return path;
	}
	
	/**
	 * 刪除圖片資料夾 
	 */
	public static void delectAllPictures(Context context){
		for(int i = 0 ; i < FOLDER_ALL.length ; i++){
			delectAllPictures(context, FOLDER_ALL[i]);
		}
	}
	
	/**
	 * 刪除圖片資料夾 
	 */
	public static void delectAllPictures(Context context , String folderName){
		File file = new File(getPath(context , folderName));
		delectAllPictures(file);
	}
	
	/**
	 * 刪除指定資料夾下所有檔案及子資料夾
	 */
	public static void delectAllPictures(File file){
		if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
        	file.delete();
            return;
        }
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
        	delectAllPictures(fileList[i]);
        }
        file.delete();
	}
	
	/**
	 * SDCard 是否存在
	 */
	public static boolean ExistSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/**
	 * SDCard 剩余空间
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static long getSDFreeSize(Context context) {
		// 取得SD卡文件路径
		File path = new File(getPath(context));
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			blockSize = sf.getBlockSizeLong();
		}else{
			blockSize = sf.getBlockSize();
		}
		// 空闲的数据块的数量
		long freeBlocks = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			freeBlocks = sf.getAvailableBlocksLong();
		}else{
			freeBlocks = sf.getAvailableBlocks();
		}
		// 返回SD卡空闲大小
		return freeBlocks * blockSize; //单位Byte
//		return (freeBlocks * blockSize)/1024; //单位KB
//		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	/**
	 * SDCard 总容量
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static long getSDAllSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			blockSize = sf.getBlockSizeLong();
		}else{
			blockSize = sf.getBlockSize();
		}
		// 获取所有数据块数
		long allBlocks = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			allBlocks = sf.getBlockCountLong();
		}else{
			allBlocks = sf.getBlockCount();
		}
		// 返回SD卡大小
		return allBlocks * blockSize; //单位Byte
//		return (allBlocks * blockSize)/1024; //单位KB
//		return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
	}
	
	/**
	 * 確認檔案是否存在
	 */
	public static boolean checkFile(String uri){
		File file = new File(uri);
		if (file != null) {
			return file.exists();
		}
		return false;
	}
	
	/**
	 * 確認資料夾是否存在
	 */
	public static boolean checkFolder(String uri){
		File folder = new File(uri);
		if (folder.exists() && folder.isDirectory()){
			return true;
		}
		return false;
	}
	
	/**
	 * 格式化url取得最後檔名
	 */
	public static String formetURLname(String url) {
		if (url.contains("/")) {
			String[] s = url.split("/");
			url = s[s.length - 1];
			if (s.length > 3) {
				url=s[s.length - 2] + "_" + s[s.length - 1];
			}
		}
		return url;
	}
	
	/**
     * 以檔案日期排序
     * @param fliePath fliePath 檔案路徑
     * @param asc true:由新到舊，false:由舊到新
     */
    public static ArrayList<File> sortByDate(String fliePath, final boolean asc) {
        ArrayList<File> list = new ArrayList<File>();
        File file = new File(fliePath);
        File[] fs = file.listFiles();
        if(fs != null){
        	Arrays.sort(fs,new Comparator<File>(){
                public int compare(File f1, File f2) {
                	
                	
                    long diffDate = f1.lastModified() - f2.lastModified(); 
                    if(asc){
                        if (diffDate > 0) { 
                            return 1;  
                        }else if (diffDate == 0){  
                            return 0;  
                        }else{  
                            return -1;
                        }
                    }else{
                        if (diffDate > 0){  
                            return -1;  
                        }else if (diffDate == 0){  
                            return 0;  
                        }else{  
                            return 1; 
                        }
                    }
                }  
                public boolean equals(Object obj) {
                    return true;  
                }  

            });  
        }
        for (int i = fs.length-1; i >-1; i--) {
            list.add(fs[i]);
        }  
        return list;
    }
}
