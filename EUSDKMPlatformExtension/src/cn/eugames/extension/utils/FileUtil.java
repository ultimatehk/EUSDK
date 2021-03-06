/**
 * 文件操作类
 * 
 * @author huangke
 *
 */
package cn.eugames.extension.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtil {

	private static final String FOLDERNAME = "EuGames_TEST/";
	private static String fileDir;
	private static FileUtil fileUtil;
	private String SDPATH;

	private FileUtil() {

		SDPATH = Environment.getExternalStorageDirectory() + "/";
		fileDir = SDPATH + FOLDERNAME;
		createDIR(fileDir);
	}

	public static FileUtil getInstanse() {
		if (fileUtil == null)
			fileUtil = new FileUtil();
		return fileUtil;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filepath
	 * @return
	 */
	public boolean checkFileExists(String filepath) {
		File file = new File(SDPATH + filepath);
		return file.exists();
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirpath
	 * @return
	 */
	public File createDIR(String dirpath) {
		File dir = new File(dirpath);
		dir.mkdir();
		return dir;
	}

	/**
	 * 在SD卡上创建文件；
	 */
	public File createFile(String filepath) {
		File file = new File(fileDir + filepath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	public void appendContext(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileDir + fileName, true);
			writer.write(content + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将一个InputStream中的数据写入至SD卡中
	 */
	public File writeStreamToSDCard(String dirpath, String filename,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			// 创建目录；
			createDIR(dirpath);
			// 在创建 的目录上创建文件；
			file = createFile(dirpath + filename);
			output = new FileOutputStream(file);
			byte[] bt = new byte[4 * 1024];
			while (input.read(bt) != -1) {
				output.write(bt);
			}
			// 刷新缓存，
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return file;

	}

}
