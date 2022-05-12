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

    User getUserByEmail(String email);

    @Update("update user set home_file = #{homeFileId} where uid = #{uid}")
    void setHome(@Param("uid") Integer uid, @Param("homeFileId") Integer homeFileId);

    String getHome(@Param("uid") Integer uid);

    @Select("select if(count(*)>0,false,true) from user where  username = #{username} or `email` = #{email}")
    Boolean canRegister(User user);

    /**
     * 判断用户名是否可用
     * @param username 用户名
     * @return 可用？
     */
    @Select("select if(count(*)>0,false,true) from user where username = #{username}")
    Boolean userNameIsAvailable(@Param("username") String username);

    /**
     * 判断邮箱是否可用
     * @param email 邮箱
     * @return 可用？
     */
    @Select("select if(count(*)>0,false,true) from user where email = #{email}")
    Boolean emailIsAvailable(@Param("email") String email);

    @Update("update user set username = #{username} where uid = #{uid}")
    Integer updateUserName(@Param("uid")Integer uid,@Param("username")String username);

    @Update("update user set email = #{email} where uid = #{uid}")
    Integer updateEmail(@Param("uid")Integer uid,@Param("email")String email);

    @Update("update user set password = #{password} where uid = #{uid}")
    Integer updatePassword(@Param("uid")Integer uid,@Param("password")String password);

    @Update("update user set describe_word = #{describeWord} where uid = #{uid}")
    Integer updateDescribeWord(@Param("uid")Integer uid,@Param("describeWord")String describeWord);

    @Update("update user set birthday = #{birthday} where uid = #{uid}")
    Integer updateBirthday(@Param("uid")Integer uid,@Param("birthday")String birthday);

    @Update("update user set sex = #{sex} where uid = #{uid}")
    Integer updateSex(@Param("uid")Integer uid,@Param("sex")Integer sex);

    @Update("update user set photo = #{photo} where uid = #{uid}")
    Integer updatePhoto(@Param("uid")Integer uid,@Param("photo")String photo);

    /**
     * 通过准确的uid，username或者email查询用户
     * @param searchStr 准确的uid，username或者email
     * @return 用户（查询错误为空）
     */
    User searchUser(@Param("searchStr")String searchStr);
}
