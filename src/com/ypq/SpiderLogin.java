package com.ypq;

import com.squareup.okhttp.*;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderLogin {
    private OkHttpClient client;
    private String location = null, jsessionid = null, valid = null, code = null;    //几个重要的登陆变量,location用于找到主页,JSESSIONID用于header的标识,valid用于账号密码登陆加密,code用于验证码
    private String uid = null;        //登陆的用户名
    private String pass = null;        //登陆密码
    private String pinCode = null;    //PIN码
    private String r1 = null, r2 = null;    //PIN步骤需要的r1,r2
    public final String codePath = "code.jpg";

    SpiderLogin(OkHttpClient client) {
        this.client = client;
        uid = "vgyu";
        pass = "mkop*2323";
        pinCode = "11989";
    }

    /**
     * 登陆1.获取JSESSIONID
     *
     * @throws Exception
     */
    public void getJession() {
        Request request = new Request.Builder().url("http://www.ctb988.net/").build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                //根据cookie找到JSESSIONID,作为客户端的标识
                String setCookie = response.header("Set-Cookie");
                if (setCookie == null) {
                    return;
                }
                Matcher m1 = Pattern.compile("(?<=JSESSIONID=).+(?=;)").matcher(setCookie);
                if (!m1.find()) {
                    return;
                }
                System.out.println("m1.group=" + m1.group());
                jsessionid = m1.group();
                location = body.substring(body.indexOf('?') + 1, body.indexOf('\'', body.indexOf('?') + 1));
                System.out.println("location= " + location);

                getHome(location);
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("requestInit fail!!!");
            }

        });

    }

    /**
     * 登陆2.获取location
     *
     * @param location
     */
    public void getHome(String location) {
        String url = "http://www.ctb988.net/?" + location;
        System.out.println(url);
        Request request = new Request.Builder().url(url).addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
        Call callCode = client.newCall(request);
        callCode.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                System.out.println("getHome success!!!");
                getCode();
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("getHome fail!!!");
            }
        });
    }

    /**
     * 登陆3.获取验证码
     */
    public void getCode() {
        Request request = new Request.Builder().url("http://www.ctb988.net/img.jpg?0.16911522029540038").addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
        Call callCode = client.newCall(request);
        callCode.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                System.out.println(response.body().contentLength());
                BufferedImage bi = ImageIO.read(response.body().byteStream());
//				ImageIO.write(bi, "jpg", new File(codePath));
                try {
                    code = Recognition.recognize(codePath);
                } catch (TesseractException e) {
                    e.printStackTrace();
                }
                System.out.println(codePath + " code = " + code);

                System.out.println("getCode success!");

                getValid();
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("getCode fail!!!");
            }
        });
    }

    /**
     * 登陆4.获取用户名密码登陆所需的valid
     */

    public void getValid() {
        Request request = new Request.Builder().url("http://www.ctb988.net/login.jsp?e=3&s=true").addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
        Call callCode = client.newCall(request);
        callCode.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Matcher matcher = Pattern.compile("(?<=id=\"valid\" value=\").+(?=\"/>)").matcher(body);
				if (!matcher.find()) {
					return;
				}
                valid = matcher.group();
                System.out.println("valid =" + valid);
                System.out.println("getValid success!");

                firstLogin();
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("getValid fail!!!");
            }

        });
    }

    /**
     * 登陆5.根据获取的valid加密,开始第一次登陆
     */
    public void firstLogin() {
        pass = sha1(valid + code + sha1("voodoo_people_" + uid + sha1(pass)));        //获取加密后的pass
        FormEncodingBuilder feBuilder = new FormEncodingBuilder();
        feBuilder.add("code", code);
        feBuilder.add("lang", "EN");
        feBuilder.add("pass", pass);
        feBuilder.add("ssl", "http:");
        feBuilder.add("uid", uid);
        feBuilder.add("valid", valid);
        Request request = new Request.Builder().url("https://secure.ctb988.net/login").post(feBuilder.build()).addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
        Call callCode = client.newCall(request);
        callCode.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                System.out.println(response.body().string());
                System.out.println("firstLogin success!!!");
                getPin();
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("firstLogin fail!!!");
            }
        });
    }

    /**
     * 登陆6.获取PIN需要的r1,r2
     */
    public void getPin() {

        Request request = new Request.Builder().url("https://secure.ctb988.net/validate_pin.jsp").addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
        Call callCode = client.newCall(request);
        callCode.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Matcher matcher = Pattern.compile("(?<=var r\\d=').+(?=';)").matcher(body);        //分别提取r1,r2的正则表达式
				if (!matcher.find()) {
					return;
				}
                r1 = matcher.group();
				if (!matcher.find()) {
					return;
				}
                r2 = matcher.group();
                System.out.println("r1=" + r1);
                System.out.println("r2=" + r2);
                System.out.println("getPin success!!!");
                secondLogin();
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("getPin fail!!!");
            }
        });

    }

    /**
     * 登陆7.最后PIN的登陆
     */
    public void secondLogin() {
        pinCode = sha1(r1 + r2 + sha1("pin_" + uid + pinCode));
        FormEncodingBuilder feBuilder = new FormEncodingBuilder();
        feBuilder.add("code", pinCode);
        Request request = new Request.Builder().url("https://secure.ctb988.net/verifypin").post(feBuilder.build()).addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
        Call callCode = client.newCall(request);
        callCode.enqueue(new Callback() {
            @Override
			public void onResponse(Response response) throws IOException {
                System.out.println(response.body().string());
                System.out.println("secondLogin success!!!");
            }

            @Override
			public void onFailure(Request arg0, IOException arg1) {
                System.out.println("secondLogin fail!!!");
            }
        });
    }

    /**
     * JAVA库自带的SHA1加密
     *
     * @param decript
     * @return
     */
    private String sha1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void testCode() {
        try {
            code = Recognition.recognize(codePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TesseractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("code=" + code);
    }
}
