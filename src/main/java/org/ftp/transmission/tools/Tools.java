package org.ftp.transmission.tools;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 工具类
 * Created by Q-WHai on 2015/11/11.
 */
public class Tools {
    public static class NetTools {

        public static String hostAddress() {
            String address = "";
            try {
                InetAddress addr =  InetAddress.getLocalHost();
                address = addr.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            return address;
        }
    }

    /**
     * 字符串工具类
     */
    public static class StringTools {
        public static boolean isEmpty(String label) {
            if (label == null || label.length() == 0) {
                return true;
            }
            return false;
        }

        public static String reversal(String label) {
            if (isEmpty(label)) {
                return "";
            }

            return new StringBuffer(label).reverse().toString();
        }

        public static boolean isPalindrome(String label) {
            if (isEmpty(label)) {
                return false;
            }

            return reversal(label).equals(label);
        }
    }

    public static class FileTools {

        /**
         * 文件重命名
         * @param path 文件目录
         * @param oldName  原来的文件名
         * @param newName 新文件名
         */
        public static void renameFile(String path, String oldName, String newName) {

            //新的文件名和以前文件名不同时,才有必要进行重命名
            if(!oldName.equals(newName)){
                File oldFile=new File(path + "/" + oldName);
                File newFile=new File(path + "/" + newName);

                // 重命名文件不存在
                if(!oldFile.exists()){
                    return;
                }

                //若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                if(newFile.exists()) {
                    System.out.println(newName + "已经存在.");
                } else{
                    oldFile.renameTo(newFile);
                }
            }else{
                System.out.println("新文件名和旧文件名相同");
            }
        }
    }
}
