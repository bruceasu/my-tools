package asu.tool.util;

/**
 * 获取源代码行号
 *
 * @author Victor Su&lt;victor.su@gwtsz.net&gt;
 * @version 1.0.0
 * @date 2016/3/7 14:20
 */

public class LineNo {
    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[3].getLineNumber();
    }

    public static String getFileName() {
//        for ( StackTraceElement e : Thread.currentThread().getStackTrace())
//            System.out.println("e = " + e);
        return Thread.currentThread().getStackTrace()[3].getFileName();
    }

    public static void logFileLineNo() {
        System.out.println("run to ["+getFileName()+"："+ getLineNumber()+"]");
    }
    public static void main(String args[]) {
        System.out.println("["+getFileName()+"："+ getLineNumber()+"]"+"Hello World!");
    }
}