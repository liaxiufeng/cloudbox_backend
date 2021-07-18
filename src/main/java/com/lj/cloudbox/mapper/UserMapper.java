package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select count(*) from user where  username = #{username} and `password` = #{password}")
    Boolean canLogin(User user);

    @Select("select * from user where  username = #{username} and `password` = #{password}")
    User loginReturnUser(User user);

    @Select("select count(*) from user where username = #{username} and `password` = #{password}")
    Boolean canLogin(@Param("username") String username,@Param("password") String password);

    @Select("select count(*) from user where  username = #{username} and `password` = #{password}")
    Boolean canRegister(@Param("username") String username);
    @Select("select * from user where uid = #{uid}")
    User selectById(Integer uid);

}
