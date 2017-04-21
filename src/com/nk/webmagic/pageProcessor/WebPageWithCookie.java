package com.nk.webmagic.pageProcessor;

import com.nk.excel.util.DateUtils;
import com.nk.ticket.util.HttpsUtil;
import org.junit.Test;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WebPageWithCookie {

	public static void main(String[] args) throws IllegalStateException, IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String url = "http://www.baidu.com";
		String pin = "jd_5f39b31316ab4";
		String newString = new String(getHttpMethod(url));
		System.out.println(newString);

		System.out.println(DateUtils.getCurrentDate(DateUtils.Format_DateTime));
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable, "System_Clock");
				thread.setDaemon(true);
				return thread;
			}
		});
//		scheduler.scheduleAtFixedRate(new Timer(), 5, 20, TimeUnit.MINUTES);
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void time(){
		System.out.println(DateUtils.getCurrentDate(DateUtils.Format_DateTime));
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable, "System_Clock");
				thread.setDaemon(true);
				return thread;
			}
		});
		scheduler.scheduleAtFixedRate(new Timer(), 1000, 13 * 60 * 60 * 10000, TimeUnit.MILLISECONDS);
		try {
			Thread.sleep(2000+100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getHttpMethod(String urlString) throws IOException {
		URL url = new URL(urlString);

        /*
         * use ignore host name verifier
         */
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("Cookie","userinfo=a00639553b3d6ea9a11ffa98c3cf9942");

		InputStream reader = connection.getInputStream();

		// result.setResponseData(bytes);
		String repString = HttpsUtil.convertStreamToString(reader);
		reader.close();

		connection.disconnect();
		return repString;
	}
	int i = 0;
	class Timer implements Runnable {
		// 注入进来，避免访问SystemClock.now占用很多CPU

		public Timer() {
		}

		@Override
		public void run() {
			System.out.println("---");
			if (i > 0)
				return;
			i ++ ;
			String url = "http://kq.baidu.com/";
			try {
				String newString = new String(HttpsUtil.getHttpMethod(url));
				System.out.println(newString);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(DateUtils.getCurrentDate(DateUtils.Format_DateTime));
		}
	}

}


