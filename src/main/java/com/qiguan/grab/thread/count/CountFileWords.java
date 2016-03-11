package com.qiguan.grab.thread.count;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.qiguan.grab.util.FileSpliterUtil;
import com.qiguan.grab.util.FileSpliterUtil.Point;

public class CountFileWords {
	public static void main(String[] args) throws Exception {
		File wf = new File("d://SensitiveWord.txt");
		// 文件分片
		List<Point> pointList = new FileSpliterUtil().blocking(wf, 1);
		// 线程池
		ExecutorService executor = Executors.newCachedThreadPool();
		List<CountWords> threadList = new ArrayList<CountWords>(pointList.size());
		for (int i = 0; i < pointList.size(); i++) {
			threadList.add(new CountWords(wf, pointList.get(i).getSkipSize(), pointList.get(i).getLength()));
			executor.execute(threadList.get(i));
		}
		
		Thread t = new Thread() {
			public void run() {
				while (true) {
					System.out.println(executor.isTerminated());
					// 两个线程均运行结束
					if (executor.isTerminated()) {
						// 使用TreeMap保证结果有序
						TreeMap<String, Integer> tMap = new TreeMap<String, Integer>();
						// 对不同线程处理的结果进行整合
						for (CountWords cw : threadList) {
							tMap.putAll(cw.getResult());
						}
						// 打印输出，查看结果
						for (Map.Entry<String, Integer> entry : tMap.entrySet()) {
							String key = entry.getKey();
							int value = entry.getValue();
							System.out.println(key + ":\t" + value);
						}
						// 将结果保存到文件中
						mapToFile(tMap, new File("d://result.txt"));
					}
					return;
				}
			}
		};
		t.start();
	}

	// 将结果按照 "单词：次数" 格式存在文件中
	@SuppressWarnings("resource")
	private static void mapToFile(Map<String, Integer> src, File dst) {
		try {
			// 对将要写入的文件建立通道
			FileChannel fcout = new FileOutputStream(dst).getChannel();
			// 使用entrySet对结果集进行遍历
			for (Map.Entry<String, Integer> entry : src.entrySet()) {
				String key = entry.getKey();
				int value = entry.getValue();
				// 将结果按照指定格式放到缓冲区中
				ByteBuffer bBuf = ByteBuffer.wrap((key + ":\t" + value).getBytes());
				fcout.write(bBuf);
				bBuf.clear();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class CountWords implements Runnable {
	private FileChannel fc;
	private FileLock fl;
	private MappedByteBuffer mbBuf;
	private HashMap<String, Integer> hm;
	
	public CountWords(File src, long start, long end) {
		try {
			//得到当前文件的通道
			fc = new RandomAccessFile(src, "rw").getChannel();
			//锁定当前文件的部分
			fl = fc.lock(start, end, false);
			//对当前文件片段建立内存映射，如果文件过大需要切割成多个片段
			mbBuf = fc.map(FileChannel.MapMode.READ_ONLY, start, end);
			//创建HashMap实例存放处理结果
			hm = new HashMap<String,Integer>();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		String str = Charset.forName("UTF-8").decode(mbBuf).toString();
		//使用StringTokenizer分析单词
		StringTokenizer token = new StringTokenizer(str);
		String word;
		while(token.hasMoreTokens()) {
			//将处理结果放到一个HashMap中，考虑到存储速度
			word = token.nextToken();
			if(null != hm.get(word)) {
				hm.put(word, hm.get(word)+1);
			} else {
				hm.put(word, 1);
			}
		}
		try {
			//释放文件锁
			fl.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	//获取当前线程的执行结果
	public HashMap<String, Integer> getResult() {
		return hm;
	}
}
