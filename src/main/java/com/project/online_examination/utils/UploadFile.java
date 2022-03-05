package com.project.online_examination.utils;

import com.project.online_examination.constant.Constant;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.vo.ResultVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/5 15:42
 * @description：上传文件
 * @modified By：
 * @version: $
 */
public class UploadFile {

    /**
     * 上传文件
     *
     * @param baseDir
     * @param file
     * @param allowedExtension
     * @return
     * @throws IOException
     */
    public static Map<String, String> upload(String baseDir, MultipartFile file, String[] allowedExtension) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Map<String, String> map = new HashMap<>();
        if (!Arrays.asList(allowedExtension).contains(extension)) {
            map.put("msg", Constant.FILE_EXCEPTION);
            return map;
        }
        //得到文件名
        String fileName = extractFilename(file);
        //创建文件
        File desc = File.createTempFile(baseDir, fileName);
        //传入文件
        file.transferTo(desc);
        //得到虚拟路径名
        String pathFileName = getPathFileName(Constant.PATH, fileName);
        map.put("msg", "成功");
        map.put("path", pathFileName);
        map.put("localPath", baseDir + "/" + fileName);
        return map;
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        fileName = datePath() + "/" + getUUID() + "." + extension;
        return fileName;
    }

    /**
     * 得到日期字符串
     *
     * @return
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 得到uuid
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 得到文件的绝对路径
     *
     * @param uploadDir
     * @param fileName
     * @return
     * @throws IOException
     */
    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc.isAbsolute() ? desc : desc.getAbsoluteFile();
    }

    /**
     * 得到虚拟路径名
     *
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
    private static final String getPathFileName(String path, String fileName) throws IOException {
        String pathFileName = path + "/" + fileName;
        return pathFileName;
    }

    /**
     * 得到上传的excel文件
     *
     * @param multipartFile
     * @return
     */
    public static File getUploadExcelFile(MultipartFile multipartFile) {
        File file = null;
        String originalFilename = multipartFile.getOriginalFilename();

        if (!originalFilename.endsWith(".xls") && !originalFilename.endsWith(".xlsx")) {
            return null;
        }

        String prefix = null;

        if (originalFilename.endsWith(".xlsx")) {
            prefix = originalFilename.substring(0, originalFilename.length() - 5);
        }

        if (originalFilename.endsWith(".xls")) {
            prefix = originalFilename.substring(0, originalFilename.length() - 4);
        }

        String suffix = originalFilename.substring(prefix.length());
        try {
            file = File.createTempFile(prefix, suffix);
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double value = cell.getNumericCellValue();
                return String.valueOf(Math.round(value));
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
