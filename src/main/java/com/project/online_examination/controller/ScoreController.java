package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.dto.ScoreDTO;
import com.project.online_examination.mapstruct.QuestionConverter;
import com.project.online_examination.mapstruct.ScoreConverter;
import com.project.online_examination.pojo.*;
import com.project.online_examination.service.*;
import com.project.online_examination.vo.PageInfoVO;
import com.project.online_examination.vo.QuestionVO;
import com.project.online_examination.vo.ResultVO;
import com.project.online_examination.vo.ScoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 22:21
 * @description：成绩管理
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("score")
@Api(tags = "成绩管理")
public class ScoreController {

    @Autowired
    private IExamineeScoreService examineeScoreService;
    @Autowired
    private IExamineeExaminationPaperService examineeExaminationPaperService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private IExaminationPaperService examinationPaperService;
    @Autowired
    private IMajorService majorService;
    @Autowired
    private ITeacherAndCourseService teacherAndCourseService;
    @Autowired
    private IExaminationQuestionsService examinationQuestionsService;

    @ApiOperation(value = "删除成绩")
    @PostMapping("deleteScore")
    @ApiOperationSupport(includeParameters = {"dto.examineeScoreIds"})
    @Transactional
    public ResultVO deleteScore(@RequestBody ScoreDTO dto) {

        //得到需要删除的成绩
        List<ExamineeScorePO> examineeScorePOS = examineeScoreService.list(Wrappers.lambdaQuery(ExamineeScorePO.class)
                .in(ExamineeScorePO::getExamineeScoreId, dto.getExamineeScoreIds()));

        //删除成绩
        examineeScoreService.remove(Wrappers.lambdaQuery(ExamineeScorePO.class)
                .in(ExamineeScorePO::getExamineeScoreId, dto.getExamineeScoreIds()));

        List<Long> userIds = examineeScorePOS.stream().map(t -> t.getUserId()).collect(Collectors.toList());
        //删除考生对应的考生试卷
        examineeExaminationPaperService.remove(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                .in(ExamineeExaminationPaperPO::getUserId, userIds));

        return ResultVO.ok();
    }

    @ApiOperation(value = "查询成绩")
    @PostMapping("queryScore")
    @ApiOperationSupport(includeParameters = {"dto.courseId", "dto.examinationPaperId", "dto.nickName", "dto.finalScore", "dto.pageNum", "dto.pageSize", "dto.teacherId"})
    public ResultVO<PageInfoVO<ScoreVO>> queryScore(@RequestBody ScoreDTO dto) {

        List<Long> courseIds = null;
        if (dto.getTeacherId() != null) {
            List<TeacherAndCoursePO> teacherAndCoursePOS = teacherAndCourseService.list(Wrappers.lambdaQuery(TeacherAndCoursePO.class).eq(TeacherAndCoursePO::getUserId, dto.getTeacherId()));
            courseIds = teacherAndCoursePOS.stream().map(t -> t.getCourseId()).collect(Collectors.toList());
        }

        LambdaQueryWrapper<ExamineeScorePO> wrapper = Wrappers.lambdaQuery(ExamineeScorePO.class);
        wrapper.eq(dto.getCourseId() != null, ExamineeScorePO::getCourseId, dto.getCourseId())
                .eq(dto.getExaminationPaperId() != null, ExamineeScorePO::getExaminationPaperId, dto.getExaminationPaperId())
                .eq(dto.getFinalScore() != null, ExamineeScorePO::getFinalSocre, dto.getFinalScore());

        List<Long> userIds = null;
        if (!StringUtils.isEmpty(dto.getNickName())) {
            List<UserPO> list = userService.list(Wrappers.lambdaQuery(UserPO.class).like(UserPO::getNickName, dto.getNickName()));
            if (!CollectionUtils.isEmpty(list)) {
                userIds = list.stream().map(t -> t.getUserId()).collect(Collectors.toList());
            } else {
                return ResultVO.ok().setData(new PageInfoVO<>(0L, null));
            }
        }

        if (!CollectionUtils.isEmpty(userIds)) {
            wrapper.in(ExamineeScorePO::getUserId, userIds);
        }

        if (!CollectionUtils.isEmpty(courseIds)) {
            wrapper.in(ExamineeScorePO::getCourseId, courseIds);
        }

        Page<ExamineeScorePO> page = examineeScoreService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        List<ExamineeScorePO> examineeScorePOS = page.getRecords();

        Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(UserPO::getUserId, UserPO::getNickName));
        Map<Long, String> courseMap = courseService.list().stream().collect(Collectors.toMap(CoursePO::getCourseId, CoursePO::getCourseName));
        Map<Long, String> paperMap = examinationPaperService.list().stream().collect(Collectors.toMap(ExaminationPaperPO::getExaminationPaperId, ExaminationPaperPO::getExaminationPaperName));
        Map<Long, String> courseIdMap = courseService.list().stream().collect(Collectors.toMap(CoursePO::getCourseId, CoursePO::getMajorIds));
        Map<Long, String> majorMap = majorService.list().stream().collect(Collectors.toMap(MajorPO::getMajorId, MajorPO::getMajorName));

        List<ScoreVO> scoreVOS = ScoreConverter.INSTANCE.convertToVO(examineeScorePOS);
        scoreVOS.forEach(t -> {
            t.setNickName(userMap.get(t.getUserId()));
            t.setCourseName(courseMap.get(t.getCourseId()));
            t.setPaperName(paperMap.get(t.getExaminationPaperId()));
            String majorIds = courseIdMap.get(t.getCourseId());
            String[] split = majorIds.split(",");
            List<String> list = new ArrayList<>();
            for (String s : split) {
                String majorName = majorMap.get(Long.valueOf(s));
                list.add(majorName);
            }
            t.setMajorsName(list.stream().collect(Collectors.joining(",")));
        });

        return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), scoreVOS));
    }

    @ApiOperation(value = "修改成绩")
    @PostMapping("editScore")
    @ApiOperationSupport(includeParameters = {"dto.examineeScoreId", "dto.finalScore"})
    @Transactional
    public ResultVO editScore(@RequestBody ScoreDTO dto) {

        ExamineeScorePO examineeScorePO = new ExamineeScorePO();
        examineeScorePO.setExaminationPaperId(dto.getExaminationPaperId());
        examineeScorePO.setFinalSocre(dto.getFinalScore());

        examineeScoreService.updateById(examineeScorePO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "得到考生试卷中的填空题和简答题")
    @PostMapping("getQuestion")
    @ApiOperationSupport(includeParameters = {"dto.courseId", "dto.examinationPaperId", "dto.userId"})
    @Transactional
    public ResultVO<List<QuestionVO>> getQuestion(@RequestBody ScoreDTO dto) {

        List<ExamineeExaminationPaperPO> examineeExaminationPaperPOS = examineeExaminationPaperService.list(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                .eq(ExamineeExaminationPaperPO::getCourseId, dto.getCourseId())
                .eq(ExamineeExaminationPaperPO::getExaminationPaperId, dto.getExaminationPaperId())
                .eq(ExamineeExaminationPaperPO::getUserId, dto.getUserId()));

        List<ExaminationQuestionsPO> examinationQuestionsPOS = examinationQuestionsService.list(Wrappers.lambdaQuery(ExaminationQuestionsPO.class)
                .in(ExaminationQuestionsPO::getExaminationQuestionsId, examineeExaminationPaperPOS.stream().map(t -> t.getExaminationQuestionsId()))
                .and(wrapper -> {
                    wrapper.eq(ExaminationQuestionsPO::getExaminationQuestionsCategory, 4)
                            .or()
                            .eq(ExaminationQuestionsPO::getExaminationQuestionsCategory, 5);
                }));

        Map<Long, String> answerMap = examineeExaminationPaperPOS.stream().collect(Collectors.toMap(ExamineeExaminationPaperPO::getExaminationQuestionsId, ExamineeExaminationPaperPO::getExamineeAnswer));

        List<QuestionVO> questionVOS = QuestionConverter.INSTANCE.convertToVO(examinationQuestionsPOS);

        questionVOS.forEach(t -> t.setExamineeAnswer(answerMap.get(t.getExaminationQuestionsId())));

        return ResultVO.ok().setData(questionVOS);
    }

    @ApiOperation(value = "教师阅卷，提交填空题和简答题的分数")
    @PostMapping("commit")
    @ApiOperationSupport(includeParameters = {"dto.examineeScoreId", "dto.score"})
    @Transactional
    public ResultVO commit(@RequestBody ScoreDTO dto) {

        ExamineeScorePO examineeScorePO = examineeScoreService.getById(dto.getExamineeScoreId());
        examineeScorePO.setFinalSocre(examineeScorePO.getScore() + dto.getScore());

        examineeScoreService.updateById(examineeScorePO);

        return ResultVO.ok();
    }
}
