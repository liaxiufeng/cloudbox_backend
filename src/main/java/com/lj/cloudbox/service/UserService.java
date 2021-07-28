package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public User getUser(String token){
        if (!StringUtils.hasLength(token)) return null;
        try {
            String uid = TokenUtil.decode(token);
            if (StringUtils.hasLength(uid)){
                User user = userMapper.selectById(Integer.parseInt(uid));
                return user;
            }else {
                return null;
            }
        }catch (NumberFormatException e){
            return null;
        }
    }

}
