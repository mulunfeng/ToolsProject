package com.nk.ticket;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TicketPageUtil {

	private static final String URI = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2016-05-06&from_station=BJP&to_station=ZZF";
	public static void main(String[] args) throws IllegalStateException, IOException {
		String newString = new String(getMethod(URI));
		System.out.println(newString);
	}
	
	public static String convertStreamToString(InputStream is) {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
        StringBuilder sb = new StringBuilder();      
       
        String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");      
            }      
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               e.printStackTrace();      
            }      
        }      
        return sb.toString();      
    }  

	/**
	 * 
	* @Description: 访问https请求
	* @Title: getMethod 
	* @param urlString
	* @return String
	* @throws
	* @date 2016年1月29日 下午12:43:52
	 */
	public static String getMethod(String urlString) {
		String repString= null;
        try {


            URL url = new URL(urlString);


            /*
             * use ignore host name verifier
             */
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();


            // Prepare SSL Context
            TrustManager[] tm = { ignoreCertificationTrustManger };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());


            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(ssf);
            
            InputStream reader = connection.getInputStream();

            
            // result.setResponseData(bytes);
            repString = convertStreamToString(reader);
            System.out.println(repString);
            reader.close();
            
            connection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        return repString;
    }
	 /**
     * 忽视证书HostName
     */
    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslsession) {
            System.out.println("WARNING: Hostname is not matched for cert.");
            return true;
        }
    };
    
    /**
     * Ignore Certification
     */
    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {


        private X509Certificate[] certificates;

        @Override
        public void checkClientTrusted(X509Certificate certificates[],
                String authType) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
                System.out.println("init at checkClientTrusted");
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] ax509certificate,
                String s) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
                System.out.println("init at checkServerTrusted");
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    };
}
