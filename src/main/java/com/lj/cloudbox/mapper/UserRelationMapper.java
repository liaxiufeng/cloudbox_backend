package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.UserRelation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRelationMapper extends BaseMapper<UserRelation> {

    List<UserRelation> getUserRelations(@Param("uid") Integer uid);

    @Delete("delete from user_relation where (proposer = #{uid1} and verifier = #{uid2}) or (verifier = #{uid1} and proposer = #{uid2})")
    void deleteRelation(@Param("uid1") Integer uid1, @Param("uid2") Integer uid2);

    List<UserRelation> searchRelationUser(@Param("uid") Integer uid, @Param("searchStr") String searchStr);

    UserRelation getRelation(@Param("uid1") Integer uid1, @Param("uid2") Integer uid2);
}
