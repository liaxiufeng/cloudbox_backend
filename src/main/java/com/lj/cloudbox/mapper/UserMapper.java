package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User login(User user);

    @Update("update user set home_file = #{homeFileId} where uid = #{uid}")
    Integer setHome(@Param("uid") Integer uid, @Param("homeFileId") Integer homeFileId);

    String getHome(@Param("uid") Integer uid);

    @Select("select if(count(*)>0,false,true) from user where  username = #{username} or `email` = #{email}")
    Boolean canRegister(User user);

    @Select("select if(count(*)>0,false,true) from user where  username = #{username}")
    Boolean userNameIsAvailable(@Param("username") String username);

    @Select("select if(count(*)>0,false,true) from user where email = #{email}")
    Boolean emailIsAvailable(@Param("email") String username);

    User searchUser(@Param("searchStr")String searchStr);
}
