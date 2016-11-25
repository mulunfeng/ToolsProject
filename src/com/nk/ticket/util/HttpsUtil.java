package com.nk.ticket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsUtil {

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
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 * @throws KeyManagementException 
	* @throws
	* @date 2016年1月29日 下午12:43:52
	 */
	public static String getMethod(String urlString) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, KeyManagementException {
		String repString= null;
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
        reader.close();
        
        connection.disconnect();
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

        public void checkClientTrusted(X509Certificate certificates[],
                String authType) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
                System.out.println("init at checkClientTrusted");
            }
        }

        public void checkServerTrusted(X509Certificate[] ax509certificate,
                String s) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
                System.out.println("init at checkServerTrusted");
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    };
}
