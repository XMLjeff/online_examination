package com.project.online_examination.mapstruct;

import com.project.online_examination.dto.ExamPaperDTO;
import com.project.online_examination.pojo.ExamineeExaminationPaperPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ExamConverter {

    ExamConverter INSTANCE = Mappers.getMapper(ExamConverter.class);

    @Mappings({})
    List<ExamineeExaminationPaperPO> convertToPO(List<ExamPaperDTO> dtos);
}
