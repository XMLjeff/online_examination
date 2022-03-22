package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.dto.CourseDTO;
import com.project.online_examination.dto.ExamPaperDTO;
import com.project.online_examination.dto.PaperDTO;
import com.project.online_examination.dto.UserDTO;
import com.project.online_examination.mapstruct.ExamConverter;
import com.project.online_examination.pojo.*;
import com.project.online_examination.service.*;
import com.project.online_examination.vo.ExamScoreVO;
import com.project.online_examination.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/19 22:02
 * @description：考试
 * @modified By：
 * @version: $
 */
@RestController
@RequestMapping("exam")
@Api(tags = "考试")
public class ExamController {

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

    @ApiOperation(value = "得到当前考生的课程")
    @PostMapping("getCourse")
    @ApiOperationSupport(includeParameters = {"dto.majorIds"})
    public ResultVO<List<CoursePO>> getCourse(@RequestBody UserDTO dto) {
        //得到当前考生的专业
        String majorIds = dto.getMajorIds();
        List<String> majorIdList = Arrays.asList(majorIds.split(","));
        //所有的课程
        List<CoursePO> coursePOS = courseService.list();
        //最终返回的课程
        List<CoursePO> coursePOList = new ArrayList<>();

        for (CoursePO coursePO : coursePOS) {
            List<String> strings = Arrays.asList(coursePO.getMajorIds().split(","));
            for (String s : majorIdList) {
                if (strings.contains(s)) {
                    coursePOList.add(coursePO);
                    break;
                }
            }
        }

        return ResultVO.ok().setData(coursePOList);
    }

    @ApiOperation(value = "得到当前所选课程的试卷")
    @PostMapping("getPaper")
    @ApiOperationSupport(includeParameters = {"dto.courseId"})
    public ResultVO<List<ExaminationPaperPO>> getPaper(@RequestBody CourseDTO dto) {
        List<ExaminationPaperPO> examinationPaperPOS = examinationPaperService.list(Wrappers.lambdaQuery(ExaminationPaperPO.class)
                .eq(ExaminationPaperPO::getCourseId, dto.getCourseId()));

        return ResultVO.ok().setData(examinationPaperPOS);
    }

    @ApiOperation(value = "得到试题")
    @PostMapping("getQuestion")
    @ApiOperationSupport(includeParameters = {"dto.examinationPaperId"})
    public ResultVO<List<ExaminationQuestionsPO>> getQuestion(@RequestBody PaperDTO dto) {
        List<ExaminationQuestionsPO> examinationQuestionsPOS = examinationQuestionsService.list();

        List<ExaminationQuestionsPO> collect = examinationQuestionsPOS
                .stream()
                .filter(t -> Arrays.asList(t.getExaminationPaperIds().split(",")).contains(dto.getExaminationPaperId() + ""))
                .collect(Collectors.toList());

        return ResultVO.ok().setData(collect);
    }

    @ApiOperation(value = "交卷")
    @PostMapping("commit")
    @Transactional
    public ResultVO commit(@RequestBody List<ExamPaperDTO> dtos) {

        Long userId = dtos.get(0).getUserId();

        List<ExamineeExaminationPaperPO> examineeExaminationPaperPOS = ExamConverter.INSTANCE.convertToPO(dtos);
        examineeExaminationPaperPOS.forEach(t -> {
            t.setUserId(userId);
        });

        examineeExaminationPaperService.saveBatch(examineeExaminationPaperPOS);

        //得到除填空题和简答题的试题
        List<ExaminationQuestionsPO> examinationQuestionsPOS = examinationQuestionsService.list(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                .eq(ExaminationQuestionsPO::getExaminationQuestionsCategory, 1)
                .or()
                .eq(ExaminationQuestionsPO::getExaminationQuestionsCategory, 2)
                .or()
                .eq(ExaminationQuestionsPO::getExaminationQuestionsCategory, 3));

        //得到试题和对应的答案
        Map<Long, String> answerMap = examinationQuestionsPOS.stream()
                .collect(Collectors.toMap(ExaminationQuestionsPO::getExaminationQuestionsId, ExaminationQuestionsPO::getExaminationQuestionsAnswer));

        //得到试题和对应的分数
        Map<Long, Integer> scoreMap = examinationQuestionsPOS.stream()
                .collect(Collectors.toMap(ExaminationQuestionsPO::getExaminationQuestionsId, ExaminationQuestionsPO::getExaminationQuestionsScore));

        int totalScore = 0;

        for (ExamPaperDTO dto : dtos) {
            if (answerMap.get(dto.getExaminationQuestionsId()).equals(dto.getExamineeAnswer())) {
                totalScore += scoreMap.get(dto.getExaminationQuestionsId());
            }
        }

        List<Long> questionIds = dtos.stream().map(t -> t.getExaminationQuestionsId()).collect(Collectors.toList());
        List<ExaminationQuestionsPO> examinationQuestionsPOList = examinationQuestionsService.list(Wrappers.lambdaQuery(ExaminationQuestionsPO.class).in(ExaminationQuestionsPO::getExaminationQuestionsId, questionIds));
        //判断是否存在填空题或简答题
        boolean anyMatch = examinationQuestionsPOList.stream().anyMatch(t -> t.getExaminationQuestionsCategory().equals(4) || t.getExaminationQuestionsCategory().equals(5));
        //存在
        if (anyMatch) {
            ExamineeScorePO examineeScorePO = new ExamineeScorePO();
            examineeScorePO.setExaminationPaperId(dtos.get(0).getExaminationPaperId());
            examineeScorePO.setCourseId(dtos.get(0).getCourseId());
            examineeScorePO.setScore(totalScore);
            examineeScorePO.setUserId(userId);

            examineeScoreService.save(examineeScorePO);
        } else {
            //不存在
            ExamineeScorePO examineeScorePO = new ExamineeScorePO();
            examineeScorePO.setExaminationPaperId(dtos.get(0).getExaminationPaperId());
            examineeScorePO.setCourseId(dtos.get(0).getCourseId());
            examineeScorePO.setScore(totalScore);
            examineeScorePO.setFinalScore(totalScore);
            examineeScorePO.setUserId(userId);

            examineeScoreService.save(examineeScorePO);
        }


        return ResultVO.ok();
    }

    @ApiOperation(value = "查询成绩")
    @PostMapping("queryScore")
    @ApiOperationSupport(includeParameters = {"dto.userId"})
    public ResultVO<List<ExamScoreVO>> queryScore(@RequestBody UserDTO dto) {

        Long userId = dto.getUserId();

        //考生成绩
        List<ExamineeScorePO> examineeScorePOS = examineeScoreService.list(Wrappers.lambdaQuery(ExamineeScorePO.class).eq(ExamineeScorePO::getUserId, userId));
        //课程map
        Map<Long, String> courseMap = courseService.list().stream().collect(Collectors.toMap(CoursePO::getCourseId, CoursePO::getCourseName));

        List<ExamScoreVO> examScoreVOS = new ArrayList<>();

        for (ExamineeScorePO examineeScorePO : examineeScorePOS) {
            ExamScoreVO examScoreVO = new ExamScoreVO();
            examScoreVO.setCourseName(courseMap.get(examineeScorePO.getCourseId()));
            examScoreVO.setFinalScore(examineeScorePO.getFinalScore());
            examScoreVOS.add(examScoreVO);
        }

        return ResultVO.ok().setData(examScoreVOS);
    }
}
