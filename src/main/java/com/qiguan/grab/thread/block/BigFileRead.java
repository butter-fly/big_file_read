package com.qiguan.grab.thread.block;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;


/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 上午11:35:52  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class BigFileRead  {
	
	/************** 参数 ************************/
	private File cfFile = null;
	private RandomAccessFile raf = null;
	private FileChannel fc = null;

	/**
	 * @param cfFile
	 * @param charset
	 * @throws IOException
	 */
	public BigFileRead(File cfFile,String charset) throws IOException {
		super();
		this.cfFile = cfFile;
		raf = new RandomAccessFile(this.cfFile,"r");
		init();
	}

	/**
	 * @throws IOException
	 */
	private void init() throws IOException {
		fc = raf.getChannel();
		fc.read(fbb);
		fbb.flip();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.cfFile = null;
		super.finalize();
	}

	/**
	 * 每行缓存的字节
	 */
	ByteBuffer bb = ByteBuffer.allocate(400);
	/**
	 * 一次读取文件，读取的字节缓存数
	 */
	ByteBuffer fbb=ByteBuffer.allocate(1024*5);
	boolean EOF=false;
	
	/**
	 * @return
	 * @throws IOException
	 */
	public boolean hasNext() throws IOException {
		/*
		 * 通过一个环的形式，处理数据
		 * bb 做为一行数据的缓存区   
		 * fbb  是读取出来的 缓存区的内容
		 * 判断是否包含数据，如果数据为空，则 启动线程获取一段数据
		 */
		if(EOF)return false;
		if(fbb.position()==fbb.limit()){
			if(readByte()==0)  return false;
		}
		while(true){
			if(fbb.position()==fbb.limit()){
				if(readByte()==0)  break;
			}
			byte a=fbb.get();
			if(a==13){
				if(fbb.position()==fbb.limit()){
					if(readByte()==0)  break;
				}
				return true;
			}else{
				if (bb.position() < bb.limit()) {
					bb.put(a);
				}else {
					if(readByte()==0)  break;
				}
			}
		}
		return true;
	}


	
	/**
	 * @return
	 * @throws IOException
	 */
	private int readByte() throws IOException{
		//使缓冲区做好了重新读取已包含的数据的准备：它使限制保持不变，并将位置设置为零。 
		fbb.rewind();
		//使缓冲区做好了新序列信道读取或相对 get 操作的准备：它将限制设置为当前位置，然后将该位置设置为零。 
		fbb.clear();
		if(this.fc.read(fbb)==-1){ 
			EOF=true;
			return 0;
		}else{
			fbb.flip();
			return fbb.position();
		}
	}

	/**
	 * @return
	 */
	public byte[] next(){
		bb.flip();
		byte tm[] = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
		bb.clear();
		return tm;
	}
}
