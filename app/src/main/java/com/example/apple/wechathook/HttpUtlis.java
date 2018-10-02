package com.example.apple.wechathook;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtlis {
    /**
     *get请求封装
     */
    public static void getRequest(String url, Map<String,String> params, String encode, OnResponseListner listner) {
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        if (params!=null && !params.isEmpty()){
            for (Map.Entry<String,String> entry:params.entrySet()) {    //增强for遍历循环添加拼接请求内容
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            if (listner!=null) {
                try {
                    URL path = new URL(sb.toString());
                    if (path!=null) {
                        HttpURLConnection con = (HttpURLConnection) path.openConnection();
                        con.setRequestMethod("GET");    //设置请求方式
                        con.setConnectTimeout(3000);    //链接超时3秒
                        con.setDoOutput(true);
                        con.setDoInput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(sb.toString().getBytes(encode));
                        os.close();
                        if (con.getResponseCode() == 200) {    //应答码200表示请求成功
                            onSucessResopond(encode, listner, con);
                        }
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    onError(listner, error);
                }
            }
        }
    }

    /**
     * POST请求
     */
    public static void postRequest(String url,Map<String,String> params,String encode,OnResponseListner listner){
        StringBuffer sb = new StringBuffer();
        if (params!=null && !params.isEmpty()){
            for (Map.Entry<String,String> entry: params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        if (listner!=null) {
            try {
                URL path = new URL(url);
                if (path!=null){
                    HttpURLConnection con = (HttpURLConnection) path.openConnection();
                    con.setRequestMethod("POST");   //设置请求方法POST
                    con.setConnectTimeout(3000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    byte[] bytes = sb.toString().getBytes();
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.close();
                    if (con.getResponseCode()==200){
                        onSucessResopond(encode, listner,  con);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(listner, e);
            }
        }
    }


    public static void postJsonRequest(String url, String json,String encode,OnResponseListner listner){
        if (listner!=null) {
            try {
                URL path = new URL(url);
                if (path!=null){
                    HttpURLConnection con = (HttpURLConnection) path.openConnection();
                    con.setRequestMethod("POST");   //设置请求方法POST
                    con.setConnectTimeout(3000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    // 设置文件类型:
                    con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    // 设置接收类型否则返回415错误
                    //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
                    con.setRequestProperty("accept","application/json");
                    byte[] bytes = json.getBytes();
                    byte[] writebytes = json.getBytes();
                    // 设置文件长度
                    con.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.close();
                    if (con.getResponseCode()==200){
                        onSucessResopond(encode, listner,  con);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(listner, e);
            }
        }
    }


    public static void postFileRequest(String url, Map<String,String> params, String filePath, String fileType, OnResponseListner listner){
        String END = "\r\n";
        String TWOHYPHENS = "--";
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        if (listner!=null) {
            try {
                URL path = new URL(url);
                if (path!=null){
                    HttpURLConnection con = (HttpURLConnection) path.openConnection();
                    // 设置是否从httpUrlConnection读入，默认情况下是true;
                    con.setDoInput(true);
                    // 设置是否向httpUrlConnection输出
                    con.setDoOutput(true);
                    // Post 请求不能使用缓存
                    con.setUseCaches(false);
                    // 设定请求的方法，默认是GET
                    con.setRequestMethod("POST");
                    // 设置字符编码连接参数
                    con.setRequestProperty("Connection", "Keep-Alive");
                    // 设置字符编码
                    con.setRequestProperty("Charset", "utf8");
                    // 设置请求内容类型
                    con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                    OutputStream out = new DataOutputStream(con.getOutputStream());

                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append(END);

                    File file = new File(filePath);
                    String filename = file.getName();
                    strBuf.append(TWOHYPHENS).append(BOUNDARY).append(END);
                    strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\""+END);
                    strBuf.append("Content-Type: "+fileType+END);
                    strBuf.append(END);

                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    out.write(END.getBytes());

                    strBuf = new StringBuffer();
                    if (params!=null && !params.isEmpty()){
                        for (Map.Entry<String,String> entry: params.entrySet()) {
                            strBuf.append(TWOHYPHENS).append(BOUNDARY).append(END);
                            strBuf.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\""+END+END);
                            strBuf.append(entry.getValue());
                            strBuf.append(END);
                        }
                    }
                    out.write(strBuf.toString().getBytes());
                    out.write((TWOHYPHENS + BOUNDARY + TWOHYPHENS + END).getBytes());

                    /* close streams */
                    out.flush();
                    in.close();

                    if (con.getResponseCode()==200){
                        onSucessResopond("utf8", listner,  con);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(listner, e);
            }
        }
    }

    private static void onError(OnResponseListner listner,Exception onError) {
        listner.onError(onError.toString());
    }

    private static void onSucessResopond(String encode, OnResponseListner listner, HttpURLConnection con) throws IOException {
        InputStream inputStream = con.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//创建内存输出流
        int len = 0;
        byte[] bytes = new byte[1024];
        if (inputStream != null) {
            while ((len = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            String str = new String(baos.toByteArray(), encode);
            listner.onSucess(str);
        }
    }

}