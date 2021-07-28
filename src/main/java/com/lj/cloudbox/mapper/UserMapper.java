package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where  email = #{email} and `password` = #{password}")
    User login(User user);

    @Select("select count(*) from user where  username = #{username} and `password` = #{password}")
    Boolean canRegister(@Param("username") String username);

}
