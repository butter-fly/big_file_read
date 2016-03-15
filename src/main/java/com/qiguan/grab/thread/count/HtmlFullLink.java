package com.qiguan.grab.thread.count;


public class HtmlFullLink {
	
//	volatile int total = 0;
//	ExecutorService poolFetchLink = Executors.newFixedThreadPool(150);
//	ExecutorService poolContent = Executors.newFixedThreadPool(20);
//
//	PrintStream ps = null;
//	String basePath = "D://brightmart/testt/";
//
//	public static String getText(String f) {
//		StringBuffer sb = new StringBuffer();
//		try {
//			Parser parser = new Parser(f);
//			parser.setEncoding("UTF-8");
//			NodeFilter filter = new NodeClassFilter(LinkTag.class);
//			NodeList links = new NodeList();
//			for (NodeIterator e = parser.elements(); e.hasMoreNodes();) {
//				e.nextNode().collectInto(links, filter);
//			}
//			for (int i = 0; i < links.size(); i++) {
//				LinkTag linktag = (LinkTag) links.elementAt(i);
//				sb.append(linktag.getLink() + "$^");
//
//			}
//		} catch (ParserException e) {
//			e.printStackTrace();
//		}
//		return sb.toString();
//	}
//
//
//	public Map<String, LinkTag> getText(Object key, int times) {
//		BufferedOutputStream bos = null;
//		Map<String, LinkTag> map = new HashMap<String, LinkTag>();
//		try {
//			Parser parser = new Parser(key.toString());
//			parser.setEncoding("UTF-8");
//			NodeFilter filter = new NodeClassFilter(LinkTag.class);
//			NodeList links = new NodeList();
//			for (NodeIterator e = parser.elements(); e.hasMoreNodes();) {
//				e.nextNode().collectInto(links, filter);
//			}
//			bos = new BufferedOutputStream(new FileOutputStream(new File(basePath + times + "_link_" + key.hashCode() + ".txt")));
//			for (int i = 0; i < links.size(); i++) {
//				LinkTag linktag = (LinkTag) links.elementAt(i);
//				if (linktag.getLink().indexOf("jobs.zhaopin.com") != -1) {
//					bos.write(linktag.getLink().getBytes());
//					bos.write("\n".getBytes());
//					if (times == 1) {
//						map.put(linktag.getLink(), linktag);
//					}
//				}
//				total++;
//				if (total % 1000 == 0) {
//					// System.out.println("total:" + total);
//				}
//				if (total == 100000) {
//					break;
//				}
//
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		} finally {
//			try {
//				bos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if (times == 1) {
//			return map;
//		}
//		return null;
//	}
//
//	public void invoke() {
//		try {
//			Map<String, LinkTag> map = getText("http://jobs.zhaopin.com", 1);
//			try {
//				map.forEach((key, value) -> poolFetchLink.execute((new Runnable() {
//					/**
//					 * 
//					 */
//					@Override
//					public void run() {
//						try {
//							getText(key, 2);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				})));
//				Thread.sleep(10000);
//				System.out.println("started..........................................");
//				File rootDir = new File(basePath);
//				File[] fileLists = rootDir.listFiles();
//				Arrays.asList(fileLists).forEach(f -> poolContent.execute(new Runnable() {
//					public void run() {
//						try {
//							List<String> stringList = Files.readAllLines(f.toPath());
//							System.out.println("invoke.stringList:" + stringList);
//							for (int j = 0; j < stringList.size(); j++) {
//								readUrlStringWriteToFile(stringList.get(j).toString());
//							}
//						} catch (Exception ex) {
//
//						}
//					}
//				}));
//
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//		} catch (Exception e1)
//
//		{
//			System.out.println(e1.getMessage());
//		}
//
//	}
//
//	// read url, get content, write content to file system.
//	public static void readUrlStringWriteToFile(String urlString) throws Exception {
//		System.out.println("readUrlStringWriteToFile. started. urlString:" + urlString);
//		BufferedInputStream bis = null;
//		BufferedOutputStream bos = null;
//		try {
//			URL url = new URL(urlString);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.connect();
//			InputStream is = conn.getInputStream();
//
//			byte[] b = new byte[10240];
//			int hasRead = 0;
//			String temp = null;
//			bis = new BufferedInputStream(is);
//			bos = new BufferedOutputStream(new FileOutputStream(new File("D://brightmart//testtcontent/" + urlString.hashCode() + ".txt")));
//			while ((hasRead = bis.read(b)) != -1) {
//				temp = new String(b, "utf-8");
//				System.out.println(temp);
//				bos.write(b, 0, hasRead);
//				bos.flush();
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		} finally {
//			bis.close();
//			bos.close();
//		}
//	}
//	
//	public static void main(String[] args) throws Exception {
//		HtmlFullLink extracthtmlfulllink = new HtmlFullLink();
//		extracthtmlfulllink.invoke();
//	}

}
