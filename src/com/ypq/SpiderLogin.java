package com.ypq;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.TesseractException;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;


import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class SpiderLogin {
	private OkHttpClient client;
	private String location = null, jsessionid = null, valid = null, code = null;	//������Ҫ�ĵ�½����,location�����ҵ���ҳ,JSESSIONID����header�ı�ʶ,valid�����˺������½����,code������֤��
	private String uid = null;		//��½���û���
	private String pass = null;		//��½����
	private String pinCode = null;	//PIN��
	private String r1 = null, r2 = null;	//PIN������Ҫ��r1,r2
	public final String codePath = "code.jpg";
	SpiderLogin(OkHttpClient client) {
		this.client = client;
		uid = "vgyu";
		pass = "mkop*2323";
		pinCode = "11989";
	}
	
	/**
	 * ��½1.��ȡJSESSIONID
	 * @param button
	 * @param contentPane
	 * @throws Exception
	 */
	public void getJession() {
		Request request = new Request.Builder().url("http://www.ctb988.net/").build();
		
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			public void onResponse(Response response) throws IOException {
				String body = response.body().string();
				//����cookie�ҵ�JSESSIONID,��Ϊ�ͻ��˵ı�ʶ
				String setCookie = response.header("Set-Cookie");
				if(setCookie == null) {
					return;
				}
				Matcher m1 = Pattern.compile("(?<=JSESSIONID=).+(?=;)").matcher(setCookie);
				if(!m1.find()) {
					return;
				}
				System.out.println("m1.group=" + m1.group());
				jsessionid = m1.group();
				location = body.substring(body.indexOf('?') + 1, body.indexOf('\'', body.indexOf('?') + 1));
				System.out.println("location= " + location);
				
				getHome(location);
			}
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("requestInit fail!!!");
			}

		});
		
	}
	
	/**
	 * ��½2.��ȡlocation
	 * @param location
	 */
	public void getHome(String location) {
		String url = "http://www.ctb988.net/?" + location;
		System.out.println(url);
		Request request = new Request.Builder().url(url).addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
		Call callCode = client.newCall(request);
		callCode.enqueue(new Callback() {
			public void onResponse(Response response) throws IOException {
				System.out.println("getHome success!!!");
				getCode();
			}
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("getHome fail!!!");
			}
		});
	}
	
	/**
	 * ��½3.��ȡ��֤��
	 */
	public void getCode() {
		Request request = new Request.Builder().url("http://www.ctb988.net/img.jpg?0.16911522029540038").addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
		Call callCode = client.newCall(request);
		callCode.enqueue(new Callback() {
			public void onResponse(Response response) throws IOException{
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
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("getCode fail!!!");
			}
		});
	}
	/**
	 * ��½4.��ȡ�û��������½�����valid
	 */
	
	public void getValid() {
		Request request = new Request.Builder().url("http://www.ctb988.net/login.jsp?e=3&s=true").addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
		Call callCode = client.newCall(request);
		callCode.enqueue(new Callback() {
			public void onResponse(Response response) throws IOException{
				String body = response.body().string();
				Matcher matcher = Pattern.compile("(?<=id=\"valid\" value=\").+(?=\"/>)").matcher(body);
				if(!matcher.find())
					return;
				valid = matcher.group();
				System.out.println("valid =" + valid);
				System.out.println("getValid success!");	
				
				firstLogin();
			}
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("getValid fail!!!");
			}

		});
	}
	
	/**
	 * ��½5.���ݻ�ȡ��valid����,��ʼ��һ�ε�½
	 */
	public void firstLogin() {
		pass = SHA1(valid + code + SHA1("voodoo_people_" + uid + SHA1(pass)));		//��ȡ���ܺ��pass
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
			public void onResponse(Response response) throws IOException{
				System.out.println(response.body().string());
				System.out.println("firstLogin success!!!");
				getPin();
			}
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("firstLogin fail!!!");
			}
		});
	}
	
	/**
	 * ��½6.��ȡPIN��Ҫ��r1,r2
	 */
	public void getPin() {
		
		Request request = new Request.Builder().url("https://secure.ctb988.net/validate_pin.jsp").addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
		Call callCode = client.newCall(request);
		callCode.enqueue(new Callback() {
			public void onResponse(Response response) throws IOException{
				String body = response.body().string();
				Matcher matcher = Pattern.compile("(?<=var r\\d=').+(?=';)").matcher(body);		//�ֱ���ȡr1,r2��������ʽ
				if(!matcher.find())
					return;
				r1 = matcher.group();
				if(!matcher.find())
					return;
				r2 = matcher.group();
				System.out.println("r1=" + r1);
				System.out.println("r2=" + r2);
				System.out.println("getPin success!!!");
				secondLogin();
			}
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("getPin fail!!!");
			}
		});
		
	}
	
	/**
	 * ��½7.���PIN�ĵ�½
	 */
	public void secondLogin() {
		pinCode = SHA1(r1 + r2 + SHA1("pin_" + uid + pinCode));
		FormEncodingBuilder feBuilder = new FormEncodingBuilder();
		feBuilder.add("code", pinCode);
		Request request = new Request.Builder().url("https://secure.ctb988.net/verifypin").post(feBuilder.build()).addHeader("Cookie", "JSESSIONID=" + jsessionid).build();
		Call callCode = client.newCall(request);
		callCode.enqueue(new Callback() {
			public void onResponse(Response response) throws IOException{
				System.out.println(response.body().string());
				System.out.println("secondLogin success!!!");
			}
			
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("secondLogin fail!!!");
			}
		});
	}
	
	/**
	 * JAVA���Դ���SHA1����
	 * @param decript
	 * @return
	 */
	private String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // �ֽ�����ת��Ϊ ʮ������ ��
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
	
	public void testCode()
	{
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
