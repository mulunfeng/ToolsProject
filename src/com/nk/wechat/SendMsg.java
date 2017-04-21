package com.nk.wechat;

import com.nk.security.ThreeDesUtil;
import com.nk.ticket.util.HttpsUtil;
import com.nk.ticket.util.JsonUtils;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by zhangyuyang1 on 2017/1/17.
 */
public class SendMsg {
    public static void main(String[] args) throws IOException {
        Message message = new Message();
        message.setSafe(0);
        message.setAgentid(3);
        message.setMsgtype("text");
        MessageInfo info = new MessageInfo("NONONO1");
        message.setText(info);
        message.setTouser("zyy");
        String msg = JsonUtils.objToString(message);
        msg = URLEncoder.encode(msg,"utf-8");
        String access = HttpsUtil.sendPost("https://qyapi.weixin.qq.com/cgi-bin/gettoken?"+ ThreeDesUtil.decrypt4HexByte("6b99226f0e2a091c30b7e0f55273a283110e9484e7a726dd145920c8bc3e7bd67955bf62123a23f64eb5502dac900938236b68a6e9bbc9f85ff9d5d1b1f1b87438126929ef0c41d9810c5effb42ae6dbbcac9ec785cdc677052a2e9b8d04af6f206dc914180dcfb43ac42536177af9f9"),null);
        AccessToken accessToken = JsonUtils.stringToObj(access, AccessToken.class);
        String result = HttpsUtil.sendPost("http://qydev.weixin.qq.com/cgi-bin/apiagent?tid=21&access_token="+accessToken.getAccess_token()+"&body="+msg,null);
        System.out.println(result);
    }

    public static String getHttpMethod(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        InputStream reader = connection.getInputStream();

        // result.setResponseData(bytes);
        String repString = HttpsUtil.convertStreamToString(reader);
        reader.close();

        connection.disconnect();
        return repString;
    }
}
class AccessToken {
    private String access_token;
    private String expires_in;

    public AccessToken() {
    }

    public AccessToken(String access_token, String expires_in) {
        this.access_token = access_token;
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}