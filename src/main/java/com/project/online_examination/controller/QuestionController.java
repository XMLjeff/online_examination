package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.dto.QuestionDTO;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.mapstruct.QuestionConverter;
import com.project.online_examination.pojo.ExaminationPaperPO;
import com.project.online_examination.pojo.ExaminationQuestionsPO;
import com.project.online_examination.pojo.ExamineeExaminationPaperPO;
import com.project.online_examination.service.IExaminationQuestionsService;
import com.project.online_examination.service.IExamineeExaminationPaperService;
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

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 21:46
 * @description：试题管理
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("question")
@Api(tags = "试题管理")
public class QuestionController {

    @Autowired
    private IExaminationQuestionsService examinationQuestionsService;
    @Autowired
    private IExamineeExaminationPaperService examineeExaminationPaperService;

    @ApiOperation(value = "新增试题")
    @PostMapping("insertQuestion")
    @ApiOperationSupport(ignoreParameters = {"dto.examinationQuestionsId", "dto.examinationQuestionsIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO insertQuestion(@RequestBody QuestionDTO dto) {

        ExaminationQuestionsPO examinationQuestionsPO = examinationQuestionsService.getOne(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                .eq(ExaminationQuestionsPO::getExaminationQuestionsName, dto.getExaminationQuestionsName()));

        if (examinationQuestionsPO != null) {
            return new ResultVO(MessageEnum.QUESTION_EXIST);
        }

        examinationQuestionsPO = QuestionConverter.INSTANCE.convertToPO(dto);

        examinationQuestionsService.save(examinationQuestionsPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除试题")
    @PostMapping("deleteQuestion")
    @ApiOperationSupport(includeParameters = {"dto.examinationQuestionsIds"})
    @Transactional
    public ResultVO deleteQuestion(@RequestBody QuestionDTO dto) {

        //删除试题
        examinationQuestionsService.remove(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                .in(ExaminationQuestionsPO::getExaminationQuestionsId, dto.getExaminationQuestionsIds()));

        //删除试题id对应的考生试题
        examineeExaminationPaperService.remove(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                .in(ExamineeExaminationPaperPO::getExaminationQuestionsId, dto.getExaminationQuestionsIds()));

        return ResultVO.ok();
    }

    @ApiOperation(value = "查询试题")
    @PostMapping("queryQuestion")
    @ApiOperationSupport(includeParameters = {"dto.examinationQuestionsName", "dto.examinationQuestionsCategory", "dto.examinationPaperIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO<PageInfoVO<ExaminationQuestionsPO>> queryQuestion(@RequestBody QuestionDTO dto) {

        Page<ExaminationQuestionsPO> page = examinationQuestionsService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                .like(!StringUtils.isEmpty(dto.getExaminationQuestionsName()), ExaminationQuestionsPO::getExaminationQuestionsName, dto.getExaminationQuestionsName())
                .eq(dto.getExaminationQuestionsCategory() != null, ExaminationQuestionsPO::getExaminationQuestionsCategory, dto.getExaminationQuestionsCategory()));

        List<ExaminationQuestionsPO> examinationPaperPOS = page.getRecords();

        List<ExaminationQuestionsPO> examinationQuestionsPOList = new ArrayList<>();

        if (!StringUtils.isEmpty(dto.getExaminationPaperIds())) {

            String[] split = dto.getExaminationPaperIds().split(",");
            List<String> strings = Arrays.asList(split);

            examinationPaperPOS.forEach(t -> {
                String[] split1 = t.getExaminationPaperIds().split(",");
                List<String> strings1 = Arrays.asList(split1);
                if (strings1.containsAll(strings)) {
                    examinationQuestionsPOList.add(t);
                }
            });
        }

        if (!CollectionUtils.isEmpty(examinationQuestionsPOList)) {
            return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), examinationQuestionsPOList));
        } else {
            return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), examinationPaperPOS));
        }
    }

    @ApiOperation(value = "修改试题")
    @PostMapping("editQuestion")
    @ApiOperationSupport(ignoreParameters = {"dto.examinationQuestionsIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO editQuestion(@RequestBody QuestionDTO dto) {

        ExaminationQuestionsPO examinationQuestionsPO = examinationQuestionsService.getOne(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                .eq(ExaminationQuestionsPO::getExaminationQuestionsName, dto.getExaminationQuestionsName()));

        if (examinationQuestionsPO != null) {
            if (!dto.getExaminationQuestionsId().equals(examinationQuestionsPO.getExaminationQuestionsId())) {
                return new ResultVO(MessageEnum.QUESTION_EXIST);
            }
        }

        examinationQuestionsPO = QuestionConverter.INSTANCE.convertToPO(dto);

        examinationQuestionsService.updateById(examinationQuestionsPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "上传试题")
    @PostMapping("uploadQuestion")
    public ResultVO uploadQuestion(MultipartFile multipartFile, String paperIds) throws IOException {
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
        List<ExaminationQuestionsPO> examinationQuestionsPOS = new ArrayList<>();
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            //获取一行
            Row row = sheet.getRow(rowNum);
            String questionName = UploadFile.getStringValue(row.getCell(0));
            String questionScore = UploadFile.getStringValue(row.getCell(1));
            String questionCategory = UploadFile.getStringValue(row.getCell(2));
            String questionAnswer = UploadFile.getStringValue(row.getCell(3));
            if (StringUtils.isEmpty(questionName)) {
                break;
            }

            ExaminationQuestionsPO examinationQuestionsPO = new ExaminationQuestionsPO();
            examinationQuestionsPO.setExaminationPaperIds(paperIds);
            examinationQuestionsPO.setExaminationQuestionsName(questionName);
            examinationQuestionsPO.setExaminationQuestionsAnswer(questionAnswer);
            examinationQuestionsPO.setExaminationQuestionsCategory(Integer.valueOf(questionCategory));
            examinationQuestionsPO.setExaminationQuestionsScore(Integer.valueOf(questionScore));

            examinationQuestionsPOS.add(examinationQuestionsPO);

        }

        examinationQuestionsService.saveBatch(examinationQuestionsPOS);

        is.close();
        file.deleteOnExit();
        return ResultVO.ok();
    }

}
