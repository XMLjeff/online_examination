package com.project.online_examination.service.impl;

import com.project.online_examination.service.IUserService;
import com.project.online_examination.mapper.UserMapper;
import com.project.online_examination.pojo.UserPO;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
/**
 *
 * @author xmljeff
 * @since 2022-03-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements IUserService {


}
