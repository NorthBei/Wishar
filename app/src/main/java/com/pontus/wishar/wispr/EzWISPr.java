package com.pontus.wishar.wispr;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NorthBei on 2017/8/30.
 */

public class EzWISPr extends TimerTask {

    private ArrayList<String> logResult = new ArrayList<>();

    static final String LOGIN_SUCCESSED = "50";

    final Pattern m_pLoginURL = Pattern.compile(
            "<WISPAccessGatewayParam.*<Redirect.*<LoginURL>(.*)</LoginURL>.*</Redirect>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    final Pattern m_pResponseCode = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<ResponseCode>(.*)</ResponseCode>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    final Pattern m_pReplyMessage = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<ReplyMessage>(.*)</ReplyMessage>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    final Pattern m_pLogoffURL = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<LogoffURL>(.*)</LogoffURL>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

    // about ncsi
    // https://dotblogs.com.tw/swater111/2014/01/09/139420
    //
    String m_CHECK_URL = "http://www.msftncsi.com/ncsi.txt";
    String m_CHECK_KEYWORD = "NCSI";

    String m_sCookie = null;
    String m_sReferer = null;
    String m_sCharset = "UTF-8";

    String m_sUserName = null;
    String m_sPassword = null;

    Date m_dDate = new Date();

    boolean m_bDebug = false;

    int nLoop = 0;
    int m_nPeriod = 0;

    long m_lTime = System.currentTimeMillis();

    private final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    EzWISPr setUserName(String sUserName) {
        m_sUserName = sUserName;
        return this;
    }

    EzWISPr setPassword(String sPassword) {
        m_sPassword = sPassword;
        return this;
    }

    EzWISPr setPeriod(int nMins) {
        m_nPeriod = nMins;
        return this;
    }

    String getUserName() {
        return m_sUserName;
    }

    String getPassword() {
        return m_sPassword;
    }

    int getPeriod() {
        return m_nPeriod;
    }

    public synchronized void login() {
        log("login: account:"+getUserName()+" password:"+getPassword());

        m_lTime = System.currentTimeMillis();
        nLoop = 0;
        String sURL = null;
        try {
            String sTmp = null;
            sTmp = removeNewLine(doGet(m_CHECK_URL));
            if (sTmp == null) {
                outPrintln("Please check network.");
            } else if (sTmp.contains(m_CHECK_KEYWORD)) {
                outPrintln("Already connected to Internet.");
            } else {
                sURL = getXMLValue(sTmp, m_pLoginURL);
                if (sURL != null) {
                    log("LoginURL=" + sURL);
                    sTmp = "UserName=" + URLEncoder.encode(m_sUserName, m_sCharset) + "&Password="
                            + URLEncoder.encode(m_sPassword, m_sCharset);
                    if (sURL.toLowerCase().startsWith("https://")) {
                        sTmp = this.doPost(sURL, sTmp);
                        String sResponseCode = getXMLValue(sTmp, m_pResponseCode);
                        String sReplyMessage = getXMLValue(sTmp, m_pReplyMessage);
                        String spLogoffURL = getXMLValue(sTmp, m_pLogoffURL);
                        if (sResponseCode != null && LOGIN_SUCCESSED.equals(sResponseCode)) {
                            outPrintln("Login success(" + (System.currentTimeMillis() - m_lTime) + "ms): LogoffURL="
                                    + spLogoffURL);
                        } else {
                            outPrintln("Login failed(" + (System.currentTimeMillis() - m_lTime) + "ms): ResponseCode="
                                    + sResponseCode + ", ReplyMessage=" + sReplyMessage);
                        }
                    } else {
                        outPrintln("[Warning]LoginURL is not https connection. LoginURL=" + sURL);
                    }
                } else if (sTmp.startsWith("[Warning]")) {
                    outPrintln(sTmp);
                } else {
                    outPrintln("No supported WISPr.");
                }
            }
        } catch (Exception e) {
            outPrintln("Login failed, Err=" + e.getMessage() + ", URL=" + sURL);
            e.printStackTrace(System.err);
        }
    }

    String removeNewLine(String sVal) {
        return sVal != null ? sVal.replaceAll("\\r*\\n", "") : null;
    }

    void setDebug(boolean bool) {
        m_bDebug = bool;
    }

    void log(String msg) {
        if (m_bDebug)
            outPrintln((System.currentTimeMillis() - m_lTime) + "ms\t" + msg);
    }

    void outPrintln(final String msg) {
        m_dDate.setTime(System.currentTimeMillis());
        System.out.println(m_sdf.format(m_dDate) + "\t" + msg);

        logResult.add(m_sdf.format(m_dDate) + "\t" + msg);
    }

    String getXMLValue(String sVal, Pattern pPattern) {
        String sRet = null;
        Matcher mm = pPattern.matcher(sVal);
        if (mm.find() && (sRet = mm.group(1).trim()).length() == 0)
            sRet = null;
        return sRet;
    }

    public String doPost(String sURL, String sVal) {
        log("doPost begin");
        String sRet = null;
        try {
            URL url = new URL(sURL);
            HttpURLConnection URLConn = (HttpURLConnection) url.openConnection();
            URLConn.setRequestMethod("POST");
            URLConn.setDoInput(true);
            URLConn.setDoOutput(true);
            URLConn.setInstanceFollowRedirects(false);
            URLConn.setRequestProperty("User-Agent", "EzWISPr");
            URLConn.setRequestProperty("Accept-Charset", m_sCharset);
            log("doPost setRequestProperty()");
            URLConn.connect();
            log("doPost connect()");
            if (sVal != null) {
                log("doPost output begin");
                OutputStream out = URLConn.getOutputStream();
                out.write(sVal.getBytes(m_sCharset));
                out.flush();
                out.close();
                log("doPost output end");
            }
            log("doPost input begin");
            sRet = readConn(URLConn);
            log("doPost input end");
            URLConn.disconnect();
            log("doPost disconnect()");
        } catch (java.net.UnknownHostException e) {
            sRet = null;
        } catch (javax.net.ssl.SSLHandshakeException e) {
            log("doPost SSLHandshakeException:" + sURL);
            sRet = "[Warning]Untrust certification:" + sURL;
        } catch (IOException e) {
            log("doPost IOException:" + sURL);
            e.printStackTrace(System.err);
            sRet = "[Warning]IOException:" + e.getMessage();
        }
        log("doPost end");
        return sRet;
    }

    void showField(HttpURLConnection URLConn) {
        System.out.println("------ URL --------");
        System.out.println(URLConn.getURL());
        System.out.println("------ Header -----");
        System.out.println(URLConn.getHeaderFields());
        System.out.println("-------------------");
    }

    String readConn(HttpURLConnection URLConn) throws UnsupportedEncodingException, IOException {
        if (m_bDebug)
            showField(URLConn);
        ByteArrayOutputStream out = new ByteArrayOutputStream(102400);
        int nRead;
        byte[] buff = new byte[10240];
        BufferedInputStream in = new BufferedInputStream(URLConn.getInputStream());
        while ((nRead = in.read(buff)) > -1)
            out.write(buff, 0, nRead);
        buff = null;
        log("readConn:"+out.toString(m_sCharset));
        return out.toString(m_sCharset);
    }

    public String   doGet(String sURL) {
        log("doGet begin");
        String sRet = null;
        try {
            URL url = new URL(sURL);
            HttpURLConnection URLConn = (HttpURLConnection) url.openConnection();
            URLConn.setRequestMethod("GET");
            URLConn.setDoInput(true);
            URLConn.setDoOutput(false);
            URLConn.setInstanceFollowRedirects(false);
            URLConn.setRequestProperty("User-Agent", "EzWISPr");
            URLConn.setRequestProperty("Accept-Charset", m_sCharset);

//          httpUrlConnection.setDoOutput(true);以后就可以使用conn.getOutputStream().write()
//			httpUrlConnection.setDoInput(true);以后就可以使用conn.getInputStream().read();
//
//			get请求用不到conn.getOutputStream()，因为参数直接追加在地址后面，因此默认是false。
//			post请求（比如：文件上传）需要往服务区传输大量的数据，这些数据是放在http的body里面的，因此需要在建立连接以后，往服务端写数据。
//
//			因为总是使用conn.getInputStream()获取服务端的响应，因此默认值是true。

            log("doGet setRequestProperty()");
            URLConn.connect();
            log("doGet connect()");
            if (URLConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                // HttpURLConnection.HTTP_MOVED_TEMP is 302: 要求的資源暫時存於不同的 URI 底下(Temporarily Moved)
                if (++nLoop < 20) {
                    String sLocation = URLConn.getHeaderField("Location");
                    String sTarget = new URL(URLConn.getURL(), sLocation).toString();
                    log("doGet ResponseCode=" + URLConn.getResponseCode() + ",Location=" + sLocation
                            + (sTarget.equals(sLocation) ? "" : ",Target=" + sTarget));
                    return doGet(sTarget);
                } else {
                    log("doGet Server redirected too many times");
                }
            }
            log("doGet input begin");
            sRet = readConn(URLConn);
            //log("doGet packet body:"+sRet);
            log("doGet input end");
            URLConn.disconnect();
            log("doGet disconnect()");
        } catch (java.net.UnknownHostException e) {
            log("doGet UnknownHostException:" + sURL);
            sRet = null;
        } catch (javax.net.ssl.SSLHandshakeException e) {
            log("doGet SSLHandshakeException:" + sURL);
            sRet = "[Warning]Untrust certification:" + sURL;
        } catch (IOException e) {
            log("doGet IOException:" + sURL);
            e.printStackTrace(System.err);
            sRet = "[Warning]IOException:" + e.getMessage();
        }
        log("doGet end");
        return sRet;
    }


    public void connect(String url){
        System.out.println("--- DEBUG MODE ---");
        //開啟debug模式
        setDebug(true);
        //不週期性檢查是否可登入wifi
        setPeriod(0);

        if (url.toLowerCase().startsWith("http")) {
            System.out.println(doGet(url));
        }
    }

    public ArrayList<String> loginWithLog(String userName,String password){
        System.out.println("--- DEBUG MODE ---");
        //開啟debug模式
        setDebug(true);
        //不週期性檢查是否可登入wifi
        setPeriod(0);
        setUserName(userName);
        setPassword(password);

        //if period is 0 ez.login equal to next line code
        //new Timer("EzWISPr_" + ez.getUserName()).schedule(ez, 0L, ez.getPeriod() * 60000L);

        login();
        return logResult;
    }

    public static void main(String[] args) {

        try {
            EzWISPr ez = new EzWISPr();
            System.out.println("--- DEBUG MODE ---");
            //開啟debug模式
            ez.setDebug(true);
            //不週期性檢查是否可登入wifi
            ez.setPeriod(0);

            if (args.length > 1) {
                Vector<String> vArgs = new Vector<String>(args.length);
                for (int i = 0; i < args.length; i++) {
                    if ("-debug".equals(args[i])) {
                        System.out.println("--- DEBUG MODE ---");
                        ez.setDebug(true);
                    } else if ("-period".equals(args[i])) {
                        try {
                            if (i + 1 < args.length) {
                                int nPeriod = Integer.parseInt(args[++i]);
                                if (nPeriod > 0) {
                                    ez.setPeriod(nPeriod);
                                    System.out.println("--- AUTO LOGIN MODE(Period:" + nPeriod + " mins)---");
                                }
                            }
                        } catch (java.lang.NumberFormatException e) {
                            --i;
                        }
                    } else {
                        vArgs.add(args[i]);
                    }
                }
                args = vArgs.toArray(new String[vArgs.size()]);
                vArgs = null;
            }

            if (args.length == 2) {
                ez.setUserName(args[0]).setPassword(args[1]);
                if (ez.getPeriod() == 0) {
                    ez.login();
                } else {
                    new Timer("EzWISPr_" + ez.getUserName()).schedule(ez, 0L, ez.getPeriod() * 60000L);
                }
            } else {
                if (args.length == 1 && args[0].toLowerCase().startsWith("http")) {
                    System.out.println(ez.doGet(args[0]));
                } else {
                    System.out.println("java -jar ezwispr.jar UserName Password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        login();
    }
}
