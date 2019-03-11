package org.ftp.transmission;

import org.ftp.transmission.config.Config;
import org.ftp.transmission.tools.FTPUtil;
import org.ftp.transmission.tools.Tools;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.IOException;

public class TestFtp {

    static String ip = "";
    static int port = 0;
    static String username = "";
    static String password = "";
    static String path = "";

    public static void main(String[] args) {
        System.out.println(Tools.NetTools.hostAddress()); // read host ip

        readConfig();

        upload("F:/Temp/python.msi", "/tmp/python-2.7.10.amd64.msi");

        download("F:/Temp/user1@20150925135953.rar", "/tmp/users/user1@20150925135953.rar");
    }

    static void upload(String localPath, String ftpPath) {
        // 连接ftp
        FtpClient ftp = FTPUtil.connectFTP(ip, port, username, password);

        // 切换目录
        FTPUtil.changeDirectory(ftp, path);

        FTPUtil.upload(localPath, ftpPath, ftp);

        FTPUtil.disconnectFTP(ftp);
    }

    static void download(String localPath, String ftpPath) {
        // 连接ftp
        FtpClient ftp = FTPUtil.connectFTP(ip, port, username, password);

        // 切换目录
//        FTPUtil.changeDirectory(ftp, path);

        FTPUtil.download(localPath, ftpPath, ftp);

        FTPUtil.disconnectFTP(ftp);
    }

    static void listFiles(String path) {
        FtpClient ftp = FTPUtil.connectFTP(ip, port, username, password);
        try {
            System.out.println(ftp.listFiles(path));
        } catch (FtpProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void readConfig() {
        ip = Config.FTP.HOST_IP;
        port = Config.FTP.HOST_PORT;
        username = Config.FTP.FTP_USERNAME;
        password = Config.FTP.FTP_PASSWD;
        path = Config.FTP.FTP_PATH;
    }
}