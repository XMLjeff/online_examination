package com.project.online_examination.controller;

import com.project.online_examination.pojo.CoursePO;
import com.project.online_examination.pojo.ExaminationPaperPO;
import com.project.online_examination.pojo.MajorPO;
import com.project.online_examination.service.ICourseService;
import com.project.online_examination.service.IExaminationPaperService;
import com.project.online_examination.service.IMajorService;
import com.project.online_examination.vo.CourseVO;
import com.project.online_examination.vo.MajorVO;
import com.project.online_examination.vo.PaperVO;
import com.project.online_examination.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/21 21:47
 * @description：
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("baseData")
@Api(tags = "基础数据")
public class BaseDataController {

    @Autowired
    private IExaminationPaperService examinationPaperService;
    @Autowired
    private IMajorService majorService;
    @Autowired
    private ICourseService courseService;

    @ApiOperation(value = "得到所有试卷")
    @PostMapping("getAllPaper")
    public ResultVO<List<PaperVO>> getAllPaper() {

        List<ExaminationPaperPO> examinationPaperPOS = examinationPaperService.list();

        List<PaperVO> paperVOS = new ArrayList<>();

        for (ExaminationPaperPO examinationPaperPO : examinationPaperPOS) {
            PaperVO paperVO = new PaperVO();
            paperVO.setExaminationPaperId(examinationPaperPO.getExaminationPaperId());
            paperVO.setExaminationPaperName(examinationPaperPO.getExaminationPaperName());
            paperVOS.add(paperVO);
        }

        return ResultVO.ok().setData(paperVOS);
    }

    @ApiOperation(value = "得到所有专业")
    @PostMapping("getAllMajor")
    public ResultVO<List<MajorVO>> getAllMajor() {

        List<MajorPO> majorPOS = majorService.list();

        List<MajorVO> majorVOS = new ArrayList<>();

        for (MajorPO majorPO : majorPOS) {
            MajorVO majorVO = new MajorVO();
            majorVO.setMajorId(majorPO.getMajorId());
            majorVO.setMajorName(majorPO.getMajorName());
            majorVOS.add(majorVO);
        }

        return ResultVO.ok().setData(majorVOS);
    }

    @ApiOperation(value = "得到所有课程")
    @PostMapping("getAllCourse")
    public ResultVO<List<CourseVO>> getAllCourse() {

        List<CoursePO> coursePOS = courseService.list();

        List<CourseVO> courseVOS = new ArrayList<>();

        for (CoursePO coursePO : coursePOS) {
            CourseVO courseVO = new CourseVO();
            courseVO.setCourseId(coursePO.getCourseId());
            courseVO.setCourseName(coursePO.getCourseName());

            courseVOS.add(courseVO);
        }

        return ResultVO.ok().setData(courseVOS);
    }
}
