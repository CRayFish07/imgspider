package com.tupian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 
 * @author Admin
 *抓取图片并保存到本地
 */

public class jpg_spider {
	public static Thread thread;
	public static void main(String[] args) {
		try {
			//抓取第一个页面
			String root_url="http://www.88langke.com/se/yazhousetu/";
			craw(root_url);
			
			//抓取第二到第十个页面
			for(int i=2;i<=10;i++){
				String url="http://www.88langke.com/se/yazhousetu/index_"+i+".html";
				craw(url);
			}
		} catch (Exception e) {
			System.out.println("抓取失败！");
		}
	}
	//抓取主页面中每个例子的url和标题，然后启动一个线程去抓取并下载图片
	public static void craw(String root_url) throws IOException{
		Document document=Jsoup.connect(root_url).get();
		Element content=document.getElementsByClass("textList").first();
		Elements elements=content.getElementsByTag("a");
		//为每一个链接都单独开启一个线程
		for (Element element : elements) {
			thread=new Thread(new spider("http://www.88langke.com"+element.attr("href"), element.text()));
			thread.start();
		}
	}
}

//抓取每个页面的img图片并下载到本地
class spider implements Runnable{
	private String root_url,title;
	public File file,file2;
	public InputStream is;
	public OutputStream fos;
	public URL url2;
	//构造方法，初始化变量
	public spider(String url,String title) {
		this.root_url=url;
		this.title=title;
	}
	//抓取线程
	synchronized public void run() {
		try {
			Document document=Jsoup.connect(root_url).userAgent("Mozilla").get();
			Element content=document.getElementsByTag("td").first();
			Elements elements=content.getElementsByTag("img");
			int i=0,f=0;
			for (Element element : elements) {
				if (f>=1) {//第一个链接是广告外联，跳过它
					url2=new URL(element.attr("src"));
					System.out.println(element.attr("src"));
					file=new File("D:\\爬虫爬取的内容\\2\\"+title);
					if (!file.exists()) {
						file.mkdir();//创建目录
					}
					file2=new File("D:\\爬虫爬取的内容\\2\\"+title+"\\"+i+".jpg");
					fos=new FileOutputStream(file2);
					is=url2.openStream();
					byte[] b=new byte[1024];
					int len;
					while((len=is.read(b))!=-1){
						fos.write(b,0,len);
					}
					fos.flush();
					is.close();
					fos.close();
					i++;
				}
				f++;
			}
			System.out.println("done!");
		} catch (Exception e) {
			System.out.println("下载失败");
		}
	}

}
