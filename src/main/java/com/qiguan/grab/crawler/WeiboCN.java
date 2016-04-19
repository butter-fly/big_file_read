package com.qiguan.grab.crawler;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WeiboCN {
	public static String getSinaCookie(String username, String password) throws Exception {
		StringBuilder sb = new StringBuilder();
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
		driver.get("http://login.weibo.cn/login/");

		WebElement mobile = driver.findElementByName("mobile");//("input[name=mobile]");
		mobile.sendKeys(new CharSequence[] { username });
		WebElement pass = driver.findElementByName("password");//"input[name^=password]");
		pass.sendKeys(new CharSequence[] { password });
		WebElement rem = driver.findElementByName("remember");//"input[name=remember]");
		rem.click();
		WebElement submit = driver.findElementByName("submit");//"input[name=submit]");
		submit.click();

		Set<Cookie> cookieSet = driver.manage().getCookies();
		driver.close();
		for (Cookie cookie : cookieSet) {
			sb.append(new StringBuilder().append(cookie.getName()).append("=").append(cookie.getValue()).append(";")
					.toString());
		}
		String result = sb.toString();
		if (result.contains("gsid_CTandWM")) {
			return result;
		}
		throw new Exception("weibo login failed");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebDriver driver = new HtmlUnitDriver();
		// 打开百度首页
		driver.get("http://www.baidu.com/");
		// 打印页面标题
		System.out.println("页面标题：" + driver.getTitle());
		// 根据id获取页面元素输入框
		WebElement search = driver.findElement(By.id("kw"));
		// 在id=“kw”的输入框输入“selenium”
		search.sendKeys("selenium");
		// 根据id获取提交按钮
		WebElement submit = driver.findElement(By.id("su"));
		// 点击按钮查询
		submit.click();
		// 打印当前页面标题
		System.out.println("页面标题：" + driver.getTitle());
		// 返回当前页面的url
		System.out.println("页面url：" + driver.getCurrentUrl());
		// 返回当前的浏览器的窗口句柄
		System.out.println("窗口句柄：" + driver.getWindowHandle());
		Set<String> set = driver.getWindowHandles();
		for (String str : set) {
			System.out.println(str);
		}
		System.out.println(driver.getCurrentUrl());
	}
}
