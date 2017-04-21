package com.nk.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nk.ticket.util.HttpsUtil;

/**
 * Created by zhangyuyang1 on 2017/1/17.
 */
public class GetToken {
    public static void main(String[] args) {
        String str = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wxae9faf6d2730f7b3&corpsecret=S8RkSGz2dT0MVN-efC6Ee21hnqJ9-QCTppmFGYiclUesQvMU9-guJ-U34WD2NVbN";
        String result = HttpsUtil.sendPost(str,null);
        JSONObject obj = JSON.parseObject(result);
        System.out.println(obj.get("access_token"));

    }

}
