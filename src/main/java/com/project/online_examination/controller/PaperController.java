package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.dto.PaperDTO;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.pojo.*;
import com.project.online_examination.service.*;
import com.project.online_examination.utils.UploadFile;
import com.project.online_examination.vo.ExaminationPaperVO;
import com.project.online_examination.vo.PageInfoVO;
import com.project.online_examination.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 20:55
 * @description：试卷管理
 * @modified By：
 * @version: $
 */
@RestController
@RequestMapping("paper")
@Api(tags = "试卷管理")
public class PaperController {

    @Autowired
    private IExaminationPaperService examinationPaperService;
    @Autowired
    private IExamineeExaminationPaperService examineeExaminationPaperService;
    @Autowired
    private IExamineeScoreService examineeScoreService;
    @Autowired
    private IExaminationQuestionsService examinationQuestionsService;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private IMajorService majorService;
    @Autowired
    private ITeacherAndCourseService teacherAndCourseService;

    @ApiOperation(value = "新增试卷")
    @PostMapping("insertPaper")
    @ApiOperationSupport(ignoreParameters = {"dto.examinationPaperId", "dto.examinationPaperIds", "dto.pageNum", "dto.pageSize", "dto.userId"})
    public ResultVO insertPaper(@RequestBody PaperDTO dto) {

        ExaminationPaperPO examinationPaperPO = examinationPaperService.getOne(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                .eq(ExaminationPaperPO::getExaminationPaperName, dto.getExaminationPaperName()));

        if (examinationPaperPO != null) {
            return new ResultVO(MessageEnum.PAPER_EXIST);
        }

        examinationPaperPO = new ExaminationPaperPO();
        examinationPaperPO.setExaminationPaperName(dto.getExaminationPaperName());
        examinationPaperPO.setCourseId(dto.getCourseId());

        examinationPaperService.save(examinationPaperPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除试卷")
    @PostMapping("deletePaper")
    @ApiOperationSupport(includeParameters = {"dto.examinationPaperIds"})
    @Transactional
    public ResultVO deletePaper(@RequestBody PaperDTO dto) {

        for (Long examinationPaperId : dto.getExaminationPaperIds()) {
            //删除试卷
            examinationPaperService.removeById(examinationPaperId);

            //删除试卷对应的考生试题
            examineeExaminationPaperService.remove(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                    .eq(ExamineeExaminationPaperPO::getExaminationPaperId, examinationPaperId));

            //删除试卷对应的考生成绩
            examineeScoreService.remove(Wrappers.lambdaQuery(ExamineeScorePO.class)
                    .eq(ExamineeScorePO::getExaminationPaperId, examinationPaperId));

            //删除试卷对应的试题
            examinationQuestionsService.remove(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                    .in(ExaminationQuestionsPO::getExaminationPaperIds, examinationPaperId + ""));

            //更新的试题
            List<ExaminationQuestionsPO> list = new ArrayList<>();

            List<ExaminationQuestionsPO> examinationQuestionsPOS = examinationQuestionsService.list(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                    .like(ExaminationQuestionsPO::getExaminationPaperIds, examinationPaperId + ""));

            if (!CollectionUtils.isEmpty(examinationQuestionsPOS)) {
                //去除一个试题多个试卷中对应的试卷id
                examinationQuestionsPOS.forEach(v -> {
                    String[] paperIds = v.getExaminationPaperIds().split(",");
                    List<String> paperIdList = Arrays.asList(paperIds);
                    paperIdList.remove(String.valueOf(examinationPaperId));
                    String paperIdss = paperIdList.stream().collect(Collectors.joining(","));
                    v.setExaminationPaperIds(paperIdss);
                });

                list.addAll(examinationQuestionsPOS);
                examinationQuestionsService.saveOrUpdateBatch(list);
            }
        }
        return ResultVO.ok();
    }

    @ApiOperation(value = "查询试卷")
    @PostMapping("queryPaper")
    @ApiOperationSupport(includeParameters = {"dto.examinationPaperName", "dto.courseId", "dto.pageNum", "dto.pageSize", "dto.userId"})
    public ResultVO<PageInfoVO<ExaminationPaperVO>> queryPaper(@RequestBody PaperDTO dto) {

        List<Long> courseIds = null;
        if (dto.getUserId() != null) {
            courseIds = teacherAndCourseService.list(Wrappers.lambdaQuery(TeacherAndCoursePO.class).eq(TeacherAndCoursePO::getUserId, dto.getUserId()))
                    .stream().map(t -> t.getCourseId()).collect(Collectors.toList());
        }

        LambdaQueryWrapper<ExaminationPaperPO> wrapper = Wrappers.lambdaQuery(ExaminationPaperPO.class);
        wrapper.like(!StringUtils.isEmpty(dto.getExaminationPaperName()), ExaminationPaperPO::getExaminationPaperName, dto.getExaminationPaperName())
                .eq(dto.getCourseId() != null, ExaminationPaperPO::getCourseId, dto.getCourseId());
        if (!CollectionUtils.isEmpty(courseIds)) {
            wrapper.in(ExaminationPaperPO::getCourseId, courseIds);
        }

        Page<ExaminationPaperPO> page = examinationPaperService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        List<ExaminationPaperPO> examinationPaperPOS = page.getRecords();

        Map<Long, String> courseMap = courseService.list().stream().collect(Collectors.toMap(CoursePO::getCourseId, CoursePO::getMajorIds));
        Map<Long, String> majorMap = majorService.list().stream().collect(Collectors.toMap(MajorPO::getMajorId, MajorPO::getMajorName));

        List<ExaminationPaperVO> collect = examinationPaperPOS.stream().map(t -> {
            ExaminationPaperVO examinationPaperVO = new ExaminationPaperVO();
            examinationPaperVO.setExaminationPaperId(t.getExaminationPaperId());
            examinationPaperVO.setExaminationPaperName(t.getExaminationPaperName());
            examinationPaperVO.setCourseId(t.getCourseId());
            String majorIds = courseMap.get(t.getCourseId());
            String[] split = majorIds.split(",");
            List<String> list = new ArrayList<>();
            for (String s : split) {
                String majorName = majorMap.get(Long.valueOf(s));
                list.add(majorName);
            }
            examinationPaperVO.setMajorsName(list.stream().collect(Collectors.joining(",")));
            return examinationPaperVO;
        }).collect(Collectors.toList());

        return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), collect));
    }

    @ApiOperation(value = "修改试卷")
    @PostMapping("editPaper")
    @ApiOperationSupport(ignoreParameters = {"dto.examinationPaperIds", "dto.pageNum", "dto.pageSize", "dto.userId"})
    public ResultVO editPaper(@RequestBody PaperDTO dto) {

        ExaminationPaperPO examinationPaperPO = examinationPaperService.getOne(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                .eq(ExaminationPaperPO::getExaminationPaperName, dto.getExaminationPaperName()));

        if (examinationPaperPO != null) {
            if (!examinationPaperPO.getExaminationPaperId().equals(dto.getExaminationPaperId())) {
                return new ResultVO(MessageEnum.PAPER_EXIST);
            }
        }

        examinationPaperPO = new ExaminationPaperPO();
        examinationPaperPO.setExaminationPaperName(dto.getExaminationPaperName());
        examinationPaperPO.setCourseId(dto.getCourseId());
        examinationPaperPO.setExaminationPaperId(dto.getExaminationPaperId());
        examinationPaperService.updateById(examinationPaperPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "上传试卷")
    @PostMapping("uploadPaper")
    public ResultVO uploadPaper(MultipartFile multipartFile, Long courseId) throws IOException {
        File file = UploadFile.getUploadExcelFile(multipartFile);
        if (file == null) {
            return new ResultVO(MessageEnum.FILE_EXCEPTION);
        }

        InputStream is = new FileInputStream(file);
        Workbook workbook = null;

        //获取Excel工作薄
        if (file.getName().endsWith("xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            workbook = new HSSFWorkbook(is);
        }

        Sheet sheet = workbook.getSheetAt(0);
        List<ExaminationPaperPO> examinationPaperPOS = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            //获取一行
            Row row = sheet.getRow(rowNum);
            String paperName = UploadFile.getStringValue(row.getCell(0));
            if (StringUtils.isEmpty(paperName)) {
                break;
            }

            ExaminationPaperPO examinationPaperPO = new ExaminationPaperPO();
            examinationPaperPO.setCourseId(courseId);
            examinationPaperPO.setExaminationPaperName(paperName);

            examinationPaperPOS.add(examinationPaperPO);
        }

        examinationPaperService.saveBatch(examinationPaperPOS);

        is.close();
        file.deleteOnExit();
        return ResultVO.ok();
    }
}
