package com.project.online_examination.mapstruct;

import com.project.online_examination.dto.UserDTO;
import com.project.online_examination.pojo.UserPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mappings({})
    UserPO convertToPO(UserDTO dto);
}
