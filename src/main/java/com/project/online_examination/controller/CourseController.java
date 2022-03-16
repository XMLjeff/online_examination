package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.dto.CourseDTO;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.pojo.*;
import com.project.online_examination.service.*;
import com.project.online_examination.utils.UploadFile;
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
import java.util.stream.Collectors;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 20:30
 * @description：课程信息管理
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("course")
@Api(tags = "课程信息管理")
public class CourseController {

    @Autowired
    private ICourseService courseService;
    @Autowired
    private IExamineeExaminationPaperService examineeExaminationPaperService;
    @Autowired
    private IExamineeScoreService examineeScoreService;
    @Autowired
    private IExaminationQuestionsService examinationQuestionsService;
    @Autowired
    private IExaminationPaperService examinationPaperService;

    @ApiOperation(value = "新增课程")
    @PostMapping("insertCourse")
    @ApiOperationSupport(ignoreParameters = {"dto.courseId", "dto.courseIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO insertCourse(@RequestBody CourseDTO dto) {

        CoursePO coursePO = courseService.getOne(Wrappers.lambdaQuery(CoursePO.class).eq(CoursePO::getCourseName, dto.getCourseName()));
        if (coursePO != null) {
            return new ResultVO(MessageEnum.COURSE_EXIST);
        }

        coursePO = new CoursePO();
        coursePO.setCourseName(dto.getCourseName());
        coursePO.setMajorIds(dto.getMajorIds());

        courseService.save(coursePO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除课程")
    @PostMapping("deleteCourse")
    @ApiOperationSupport(includeParameters = {"dto.courseIds"})
    @Transactional
    public ResultVO deleteCourse(@RequestBody CourseDTO dto) {

        for (Long courseId : dto.getCourseIds()) {
            //删除课程
            courseService.removeById(courseId);

            //删除课程对应的考生试题
            examineeExaminationPaperService.remove(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                    .eq(ExamineeExaminationPaperPO::getCourseId, courseId));

            //删除课程对应的考生成绩
            examineeScoreService.remove(Wrappers.lambdaQuery(ExamineeScorePO.class)
                    .eq(ExamineeScorePO::getCourseId, courseId));

            List<ExaminationPaperPO> examinationPaperPOS = examinationPaperService.list(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                    .eq(ExaminationPaperPO::getCourseId, courseId));
            if (!CollectionUtils.isEmpty(examinationPaperPOS)) {
                //得到需要删除的试卷id
                List<Long> examinationPaperIds = examinationPaperPOS.stream()
                        .map(t -> t.getExaminationPaperId()).collect(Collectors.toList());

                List<String> examinationPaperIdList = examinationPaperIds.stream().map(t -> t + "").collect(Collectors.toList());

                //删除课程对应的试卷
                examinationPaperService.remove(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                        .eq(ExaminationPaperPO::getCourseId, courseId));

                //删除试卷对应的试题
                examinationQuestionsService.remove(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                        .in(ExaminationQuestionsPO::getExaminationPaperIds, examinationPaperIdList));

                //更新的试题
                List<ExaminationQuestionsPO> list = new ArrayList<>();

                for (Long examinationPaperId : examinationPaperIds) {
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
                    }
                }
                examinationQuestionsService.saveOrUpdateBatch(list);
            }
        }

        return ResultVO.ok();
    }

    @ApiOperation(value = "查询课程")
    @PostMapping("queryCourse")
    @ApiOperationSupport(includeParameters = {"dto.courseName", "dto.majorIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO<PageInfoVO<CoursePO>> queryCourse(@RequestBody CourseDTO dto) {

        Page<CoursePO> page = courseService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), Wrappers.lambdaQuery(CoursePO.class)
                .like(!StringUtils.isEmpty(dto.getCourseName()), CoursePO::getCourseName, dto.getCourseName()));

        List<CoursePO> coursePOS = page.getRecords();

        List<CoursePO> coursePOList = new ArrayList<>();

        if (!StringUtils.isEmpty(dto.getMajorIds())) {

            String[] split1 = dto.getMajorIds().split(",");
            List<String> strings1 = Arrays.asList(split1);

            coursePOS.forEach(t -> {
                String[] split = t.getMajorIds().split(",");
                List<String> strings = Arrays.asList(split);
                if (strings.containsAll(strings1)) {
                    coursePOList.add(t);
                }
            });
        }

        if (!CollectionUtils.isEmpty(coursePOList)) {
            return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), coursePOList));
        } else {
            return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), coursePOS));
        }
    }

    @ApiOperation(value = "修改课程")
    @PostMapping("editCourse")
    @ApiOperationSupport(ignoreParameters = {"dto.courseIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO editCourse(@RequestBody CourseDTO dto) {

        CoursePO coursePO = courseService.getOne(Wrappers.lambdaQuery(CoursePO.class).eq(CoursePO::getCourseName, dto.getCourseName()));
        if (coursePO != null) {
            if (!coursePO.getCourseId().equals(dto.getCourseId())) {
                return new ResultVO(MessageEnum.COURSE_EXIST);
            }
        }

        coursePO = new CoursePO();
        coursePO.setCourseName(dto.getCourseName());
        coursePO.setMajorIds(dto.getMajorIds());
        coursePO.setCourseId(dto.getCourseId());
        courseService.updateById(coursePO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "上传课程")
    @PostMapping("uploadCourse")
    public ResultVO uploadCourse(MultipartFile multipartFile, String majorIds) throws IOException {
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
        List<CoursePO> coursePOS = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            //获取一行
            Row row = sheet.getRow(rowNum);
            String courseName = UploadFile.getStringValue(row.getCell(0));
            if (StringUtils.isEmpty(courseName)) {
                break;
            }
            CoursePO coursePO = new CoursePO();
            coursePO.setMajorIds(majorIds);
            coursePO.setCourseName(courseName);

            coursePOS.add(coursePO);
        }

        courseService.saveBatch(coursePOS);

        is.close();
        file.deleteOnExit();
        return ResultVO.ok();
    }
}
