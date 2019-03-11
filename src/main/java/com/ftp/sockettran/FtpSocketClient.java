package com.ftp.sockettran;

import org.ftp.transmission.config.Config;
import org.ftp.transmission.tools.Tools;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * 基于Socket的FTP数据传输测试
 * Created by Q-WHai on 2015/11/11.
 */
public class FtpSocketClient {

    Socket mFtpClient = null;
    BufferedReader mReader = null;
    BufferedWriter mWriter = null;

    /**
     * 连接FTP服务器
     */
    public void connectFtp() {
        try {
            mFtpClient = new Socket(Config.FTP.HOST_IP, Config.FTP.HOST_PORT);
            mReader = new BufferedReader(new InputStreamReader(mFtpClient.getInputStream()));
            mWriter = new BufferedWriter(new OutputStreamWriter(mFtpClient.getOutputStream()));

            sendCommand("USER " + Config.FTP.FTP_USERNAME);
            sendCommand("PASS " + Config.FTP.FTP_PASSWD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭FTP的Socket连接
     */
    public void disconnectFtp() {
        if (mFtpClient == null) {
            return;
        }

        if (!mFtpClient.isConnected()) {
            return;
        }

        try {
            mFtpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得Socket的连接状态
     * @param socket
     *          待检查的socket
     * @return
     */
    private boolean socketStatus(Socket socket) {
        if (socket == null || !socket.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 向FTP服务器发送命令
     * @param command
     *          FTP命令
     * @throws IOException
     */
    private void sendCommand(String command) throws IOException {
        if (Tools.StringTools.isEmpty(command)) {
            return;
        }

        if (mFtpClient == null) {
            return;
        }

        mWriter.write(command + "\r\n");
        mWriter.flush();
    }

    /**
     * 下载文件
     * @param localPath
     *          本地文件路径
     * @param ftpPath
     *          服务器文件路径
     * @throws IOException
     */
    public void downloadFile(String localPath, String ftpPath) throws IOException {
        // 进入被动模式
        sendCommand("PASV");

        // 获得ip和端口
        String response = readNewMessage();
        String[] ipPort = getIPPort(response);
        String ip = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        // 建立数据端口的连接
        Socket dataSocket = new Socket(ip, port);
        sendCommand("RETR " + ftpPath);

        // 下载文件前的准备
        File localFile = new File(localPath);
        InputStream inputStream = dataSocket.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(localFile);

        // 下载文件
        int offset;
        byte[] bytes = new byte[1024];
        while ((offset = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, offset);
        }
        System.out.println("download success!!");

        // 下载文件后的善后工作
        inputStream.close();
        fileOutputStream.close();
        dataSocket.close();
    }

    /**
     * 上传文件
     * @param localPath
     *          本地文件路径
     * @param ftpPath
     *          服务器文件路径
     * @throws IOException
     */
    public void uploadFile(String localPath, String ftpPath) throws IOException {
        // 进入被动模式
        sendCommand("PASV");

        // 获得ip和端口
        String response = readNewMessage();
        String[] ipPort = getIPPort(response);
        String ip = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        // 建立数据端口的连接
        Socket dataSocket = new Socket(ip, port);
        sendCommand("STOR " + ftpPath);

        // 上传文件前的准备
        File localFile = new File(localPath);
        OutputStream outputStream = dataSocket.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(localFile);

        // 上传文件
        int offset;
        byte[] bytes = new byte[1024];
        while ((offset = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, offset);
        }
        System.out.println("upload success!!");

        // 上传文件后的善后工作
        outputStream.close();
        fileInputStream.close();
        dataSocket.close();
    }

    /**
     * 从服务器返回的值中计算出ip和端口号
     * @param response
     *          返回信息
     * @return
     * @throws IOException
     */
    private String[] getIPPort(String response) throws IOException {

        String[] ipPort = new String[2];
        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);

        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256 + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: " + response);
            }
        }

        ipPort[0] = ip;
        ipPort[1] = String.valueOf(port);

        return ipPort;
    }

    /**
     * 从FTP服务器读入一些返回信息
     * @return
     * @throws IOException
     */
    private String readNewMessage() throws IOException {
        String response = null;
        while (true) {
            response = mReader.readLine();
            if (response == null || response.length() == 0) {
                return null;
            }
            if (isLegalMessage(response)) {
                break;
            }
        }

        return response;
    }

    /**
     * 判断是否是合法的信息，合法信息的格式如下：
     * 227 Entering Passive Mode (xxx,xxx,xxx,xxx,226,132).
     * @param msg
     *          信息
     * @return
     */
    private boolean isLegalMessage(String msg) {
        String rexp = "Entering Passive Mode";
        if (msg.contains(rexp)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        FtpSocketClient client = new FtpSocketClient();
        client.connectFtp();
        System.out.println("FTP connect status: " + client.socketStatus(client.mFtpClient));

        try {
            client.downloadFile("F:/Temp/python.msi", "/tmp/python-2.7.10.amd64.msi");
            client.uploadFile("F:/Temp/python.msi", "/tmp/python-2.7.10.amd64-2.msi");
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.disconnectFtp();
    }
}
