package com.project.online_examination.mapstruct;

import com.project.online_examination.dto.QuestionDTO;
import com.project.online_examination.pojo.ExaminationQuestionsPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 21:58
 * @description：
 * @modified By：
 * @version: $
 */
@Mapper
public interface QuestionConverter {
    QuestionConverter INSTANCE = Mappers.getMapper(QuestionConverter.class);

    @Mappings({})
    ExaminationQuestionsPO convertToPO(QuestionDTO dto);
}
