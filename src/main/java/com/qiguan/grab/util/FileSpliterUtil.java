package com.qiguan.grab.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**  
 * <pre>
 * Description	文件分割工具类
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午4:07:22  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class FileSpliterUtil {
	
		// 将大数据文件切分到另外的十个小文件中
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static void sqlitFileData(String filepath, String fileName, String ext, String sqlitPath, int CountFile) throws IOException {
			FileWriter fs = null;
			BufferedWriter fw = null;
			FileReader fr = new FileReader(filepath + "/" + fileName + ext);
			BufferedReader br = new BufferedReader(fr); // 读取获取整行数据
			int i = 1;
			LinkedList WriterLists = new LinkedList(); // 初始化文件流对象集合
			LinkedList fwLists = new LinkedList();
			for (int j = 1; j <= CountFile; j++) {
				// 声明对象
				fs = new FileWriter(sqlitPath + "\\" + fileName + "_" + j + ext, false);
				fw = new BufferedWriter(fs);
				// 将对象装入集合
				WriterLists.add(fs);
				fwLists.add(fw);
			}
			// 判断是文件流中是否还有数据返回
			while (br.ready()) {
				int count = 1;// 初始化第一文件流
				for (Iterator iterator = fwLists.iterator(); iterator.hasNext();) {
					BufferedWriter type = (BufferedWriter) iterator.next();
					if (i == count)// 判断轮到第几个文件流写入数据了
					{
						// 写入数据，跳出，进行下一个文件流，下一个数据的写入
						type.write(br.readLine() + "\r\n");
						break;
					}
					count++;
				}
				// 判断是否到了最后一个文件流了
				if (i >= CountFile) {
					i = 1;
				} else
					i++;
			}
			br.close();
			fr.close();
			for (Iterator iterator = fwLists.iterator(); iterator.hasNext();) {
				BufferedWriter object = (BufferedWriter) iterator.next();
				object.close();
			}
			// 遍历关闭所有子文件流
			for (Iterator iterator = WriterLists.iterator(); iterator.hasNext();) {
				FileWriter object = (FileWriter) iterator.next();
				object.close();
			}
		}
		
	/**
	 * 使用IO流切分指定文件
	 */
	public List<File> splitByStream(String file, int piece, String outputDirectiry) throws IOException {
		List<File> result = new ArrayList<File>();
		List<Point> list = blocking(new File(file), piece);
		for (int i = 0; i < list.size(); i++) {
			File outputFile = new File(outputDirectiry + i + "_byStream.txt");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
			in.skip(list.get(i).getSkipSize());
			int index = 0;
			while (index < list.get(i).getLength()) {
				out.write(in.read());
				index++;
			}
			out.flush();
			out.close();
			in.close();
			result.add(outputFile);
		}
		return result;
	}

	/**
	 * 使用內存映射文件切分指定文件
	 */
	@SuppressWarnings("resource")
	public List<File> splitByMappedByteBuffer(String file, int piece, String outputDirectiry) throws IOException {
		List<File> result = new ArrayList<File>();
		List<Point> list = blocking(new File(file), piece);
		for (int i = 0; i < list.size(); i++) {
			File outputFile = new File(outputDirectiry + i + "_byMappedByteBuffer.txt");
			FileChannel in = new RandomAccessFile(file, "r").getChannel();
			FileChannel out = new RandomAccessFile(outputFile, "rw").getChannel();
			MappedByteBuffer outBuffer = out.map(MapMode.READ_WRITE, 0, list.get(i).length);
			MappedByteBuffer inBuffer = in.map(MapMode.READ_ONLY, list.get(i).getSkipSize(), list.get(i).getLength());
			outBuffer.put(inBuffer);
			outBuffer.force();
			in.close();
			out.close();
			result.add(outputFile);
		}
		return result;
	}

	/**
	 * 使用通道切分指定文件
	 */
	@SuppressWarnings("resource")
	public List<File> splitByChannel(String file, int piece, String outputDirectiry) throws IOException {
		List<File> result = new ArrayList<File>();
		List<Point> list = blocking(new File(file), piece);
		for (int i = 0; i < list.size(); i++) {
			File outputFile = new File(outputDirectiry + i + "_byChannel.txt");
			FileChannel in = new FileInputStream(file).getChannel();
			FileChannel out = new FileOutputStream(outputFile).getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(list.get(i).getLength());
			in.read(buffer, list.get(i).getSkipSize());
			buffer.flip();
			out.write(buffer);
			in.close();
			out.close();
			result.add(outputFile);
		}
		return result;
	}

	/**
	 * 对文件进行切分 1.先根据指定的参数分片,每个分片以\n结束 2。根据分片的情况,计算切点
	 */
	public List<Point> blocking(File file, int piece) throws IOException {
		List<Point> result = new ArrayList<Point>();
		List<Long> list = new ArrayList<Long>();
		list.add(-1L);
		long length = file.length();
		long step = length / piece;
		long index = 0;
		for (int i = 0; i < piece; i++) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			if (index + step < length) {
				index = index + step;
				in.skip(index);

				while (in.read() != 10) {
					index = index + 1;
				}
				list.add(index);
				index++;
			}
			in.close();
		}
		list.add(length - 1);
		System.out.println(list);
		for (int i = 0; i < list.size() - 1; i++) {
			long skipSize = list.get(i) + 1;
			long l = list.get(i + 1) - list.get(i);
			result.add(new Point(skipSize, l));

		}
		System.out.println(result);
		return result;
	}

	/**
	 * 切分文件的切点 skipSize指的是从流跳过的size length指的是从流读出的长度
	 */
	public class Point {
		public Point(long skipSize, long length) {
			if (length > Integer.MAX_VALUE) {
				throw new RuntimeException("长度溢出");
			}
			this.skipSize = skipSize;
			this.length = (int) length;
		}

		@Override
		public String toString() {
			return "Point [skipSize=" + skipSize + ", length=" + length + "]\n";
		}

		private long skipSize;
		private int length;

		public long getSkipSize() {
			return skipSize;
		}

		public int getLength() {
			return length;
		}

	}
		
	public static void main(String[] args) {
		try {
			sqlitFileData("G://url_lineout.txt/", "url_lineout", ".txt", "g://url_lineout.txt/split",  20);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}