package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.dto.MajorDTO;
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
 * @date ：Created in 2022/1/16 20:13
 * @description：专业管理
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("major")
@Api(tags = "专业管理")
public class MajorController {

    @Autowired
    private IMajorService majorService;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private IExaminationPaperService examinationPaperService;
    @Autowired
    private IExaminationQuestionsService examinationQuestionsService;
    @Autowired
    private IExamineeExaminationPaperService examineeExaminationPaperService;
    @Autowired
    private IExamineeScoreService examineeScoreService;

    @ApiOperation(value = "新增专业")
    @PostMapping("insertMajor")
    @ApiOperationSupport(ignoreParameters = {"dto.majorId", "dto.majorIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO insertMajor(@RequestBody MajorDTO dto) {

        MajorPO majorPO = majorService.getOne(Wrappers.lambdaQuery(MajorPO.class).eq(MajorPO::getMajorName, dto.getMajorName()));
        if (majorPO != null) {
            return new ResultVO(MessageEnum.MAJOR_EXIST);
        }

        majorPO = new MajorPO();
        majorPO.setMajorName(dto.getMajorName());

        majorService.save(majorPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除专业")
    @PostMapping("deleteMajor")
    @ApiOperationSupport(includeParameters = {"dto.majorIds"})
    @Transactional
    public ResultVO deleteMajor(@RequestBody MajorDTO dto) {

        for (Long majorId : dto.getMajorIds()) {
            //删除专业信息
            majorService.removeById(majorId);

            List<CoursePO> coursePOS = courseService.list(Wrappers.lambdaQuery(CoursePO.class).eq(CoursePO::getMajorIds, majorId));
            if (!CollectionUtils.isEmpty(coursePOS)) {
                List<Long> courseIds = coursePOS.stream().map(t -> t.getCourseId()).collect(Collectors.toList());
                //删除课程信息
                courseService.removeByIds(courseIds);

                //删除课程对应的考生试题
                examineeExaminationPaperService.remove(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                        .in(ExamineeExaminationPaperPO::getCourseId, courseIds));

                //删除课程对应的考生成绩
                examineeScoreService.remove(Wrappers.lambdaQuery(ExamineeScorePO.class)
                        .in(ExamineeScorePO::getCourseId, courseIds));

                List<ExaminationPaperPO> examinationPaperPOS = examinationPaperService.list(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                        .in(ExaminationPaperPO::getCourseId, courseIds));
                if (!CollectionUtils.isEmpty(examinationPaperPOS)) {
                    //得到需要删除的试卷id
                    List<Long> examinationPaperIds = examinationPaperPOS.stream()
                            .map(t -> t.getExaminationPaperId()).collect(Collectors.toList());

                    //删除课程对应的试卷
                    examinationPaperService.remove(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                            .in(ExaminationPaperPO::getCourseId, courseIds));

                    //删除试卷对应的试题
                    examinationQuestionsService.remove(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                            .in(ExaminationQuestionsPO::getExaminationPaperIds, examinationPaperIds));

                    //更新的试题
                    List<ExaminationQuestionsPO> list = new ArrayList<>();

                    for (Long examinationPaperId : examinationPaperIds) {
                        List<ExaminationQuestionsPO> examinationQuestionsPOS = examinationQuestionsService.list(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                                .like(ExaminationQuestionsPO::getExaminationPaperIds, examinationPaperId));
                        if (!CollectionUtils.isEmpty(examinationQuestionsPOS)) {
                            //去除一个试题多个试卷中对应的试卷id
                            examinationQuestionsPOS.forEach(v -> {
                                String[] paperIds = v.getExaminationPaperIds().split(",");
                                List<String> paperIdList = Arrays.asList(paperIds);
                                paperIdList.remove(examinationPaperId);
                                String paperIdss = paperIdList.stream().collect(Collectors.joining(","));
                                v.setExaminationPaperIds(paperIdss);
                            });

                            list.addAll(examinationQuestionsPOS);
                        }
                    }
                    examinationQuestionsService.saveOrUpdateBatch(list);
                }
            }

            coursePOS = courseService.list(Wrappers.lambdaQuery(CoursePO.class).like(CoursePO::getMajorIds, majorId));
            if (!CollectionUtils.isEmpty(coursePOS)) {
                //去除一个课程多个专业中对应的专业id
                coursePOS.forEach(t -> {
                    String[] majorIds = t.getMajorIds().split(",");
                    List<String> majorIdList = Arrays.asList(majorIds);
                    majorIdList.remove(majorId);
                    String majorIdss = majorIdList.stream().collect(Collectors.joining(","));
                    t.setMajorIds(majorIdss);
                });
                courseService.saveOrUpdateBatch(coursePOS);
            }
        }


        return ResultVO.ok();
    }

    @ApiOperation(value = "查询专业")
    @PostMapping("queryMajor")
    @ApiOperationSupport(includeParameters = {"dto.majorName", "dto.pageNum", "dto.pageSize"})
    public ResultVO<PageInfoVO<MajorPO>> queryMajor(@RequestBody MajorDTO dto) {

        Page<MajorPO> page = majorService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), Wrappers.lambdaQuery(MajorPO.class)
                .like(!StringUtils.isEmpty(dto.getMajorName()), MajorPO::getMajorName, dto.getMajorName()));

        List<MajorPO> majorPOS = page.getRecords();

        return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), majorPOS));
    }

    @ApiOperation(value = "修改专业")
    @PostMapping("editMajor")
    @ApiOperationSupport(ignoreParameters = {"dto.majorIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO editMajor(@RequestBody MajorDTO dto) {

        MajorPO majorPO = majorService.getOne(Wrappers.lambdaQuery(MajorPO.class).eq(MajorPO::getMajorName, dto.getMajorName()));
        if (majorPO != null) {
            if (!majorPO.getMajorId().equals(dto.getMajorId())) {
                return new ResultVO(MessageEnum.MAJOR_EXIST);
            }
        }

        majorPO = new MajorPO();
        majorPO.setMajorId(dto.getMajorId());
        majorPO.setMajorName(dto.getMajorName());

        majorService.updateById(majorPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "上传专业")
    @PostMapping("uploadMajor")
    public ResultVO uploadMajor(MultipartFile multipartFile) throws IOException {
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
        List<MajorPO> majors = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            //获取一行
            Row row = sheet.getRow(rowNum);
            String majorName = UploadFile.getStringValue(row.getCell(0));
            if (StringUtils.isEmpty(majorName)) {
                break;
            }
            MajorPO majorPO = new MajorPO();
            majorPO.setMajorName(majorName);
            majors.add(majorPO);
        }

        majorService.saveBatch(majors);

        is.close();
        file.deleteOnExit();
        return ResultVO.ok();
    }
}
