package com.project.online_examination.mapstruct;

import com.project.online_examination.pojo.ExamineeScorePO;
import com.project.online_examination.vo.ScoreVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 22:53
 * @description：
 * @modified By：
 * @version: $
 */
@Mapper
public interface ScoreConverter {
    ScoreConverter INSTANCE = Mappers.getMapper(ScoreConverter.class);

    @Mappings({})
    List<ScoreVO> convertToVO(List<ExamineeScorePO> pos);
}
