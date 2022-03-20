package com.project.online_examination.mapstruct;

import com.project.online_examination.dto.TeacherDTO;
import com.project.online_examination.dto.UserDTO;
import com.project.online_examination.pojo.UserPO;
import com.project.online_examination.vo.TeacherVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mappings({})
    UserPO convertToPO(UserDTO dto);

    @Mappings({})
    UserPO convertToPO(TeacherDTO dto);

    @Mappings({})
    TeacherVO convertToVO(UserPO po);

    @Mappings({})
    List<TeacherVO> convertToVO(List<UserPO> po);
}
