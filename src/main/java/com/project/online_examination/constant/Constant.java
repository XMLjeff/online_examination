package com.project.online_examination.constant;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/5 15:44
 * @description：常量
 * @modified By：
 * @version: $
 */
public class Constant {
    /**
     * 允许的文件格式
     */
    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf"};
    /**
     * 允许的excel文件格式
     */
    public static final String[] EXCEL_EXTENSION = {"xls", "xlsx"};
    /**
     * 虚拟路径名
     */
    public static final String PATH = "http://localhost:9100";
    /**
     * 文件存储路径
     */
    public static final String BASE_DIR = "D:/onlineExam";

    public static final String FILE_EXCEPTION = "文件类型异常";
}
