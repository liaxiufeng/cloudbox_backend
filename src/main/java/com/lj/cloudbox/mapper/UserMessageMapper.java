package com.lj.cloudbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.cloudbox.pojo.UserMessage;
import com.lj.cloudbox.pojo.vo.RelationMessageSimple;
import com.lj.cloudbox.pojo.vo.UserMessageSimple;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {
    /**
     * 获得指定用户的所有好友聊天消息
     * @param uid 指定用户
     * @return 消息对象
     */
    List<UserMessage> getUserMessage(@Param("uid")Integer uid);

    /**
     * 获得指定用户的指定好友聊天消息
     * @param uid1 uid1为好友id，uid2为用户id，且可以互换
     * @param uid2 uid1为好友id，uid2为用户id，且可以互换
     * @return 消息对象
     */
    List<UserMessage> getRelationMessage(@Param("uid1")Integer uid1,@Param("uid2")Integer uid2);

    /**
     * 获得指定用户的指定好友聊天消息(仅用作消息更新，访问较少数据)
     * @param uid1 uid1为好友id，uid2为用户id，且可以互换
     * @param uid2 uid1为好友id，uid2为用户id，且可以互换
     * @return 消息对象
     */
    @Select("select * from user_message where (proposer = #{uid1} and verifier = #{uid2}) or (verifier = #{uid1} and proposer = #{uid2})")
    List<RelationMessageSimple> getRelationMessageSimple(@Param("uid1")Integer uid1,@Param("uid2")Integer uid2);

    /**
     * 分别获取指定用户的每个好友的好友id，未读消息记录数，最近一条消息的消息类型和消息内容
     * @param uid 用户id
     * @return 包装对象UserMessageSimple
     */
    @Select("select friend_uid_table.proposer as proposer," +
            "(select count(*) from user_message m1 where m1.proposer = friend_uid_table.proposer and  m1.verifier = #{uid} and is_read = 0) as not_read_number," +
            "(select type from user_message m2 where ( m2.proposer = friend_uid_table.proposer and  m2.verifier = #{uid} ) or ( m2.verifier = friend_uid_table.proposer and  m2.proposer = #{uid} ) order by m2.mid desc limit 0,1) as type," +
            "(select message from user_message m3 where ( m3.proposer = friend_uid_table.proposer and  m3.verifier = #{uid} ) or ( m3.verifier = friend_uid_table.proposer and  m3.proposer = #{uid} ) order by m3.mid desc limit 0,1) as last_message " +
            "from (select proposer from user_relation where verifier = #{uid} union select verifier from user_relation where proposer = #{uid}) as friend_uid_table;")
    List<UserMessageSimple> getMessageSimpleList(@Param("uid")Integer uid);

    /**
     * 更改指定用户的指定好友的消息已读状态
     * @param proposer 发送消息的用户，即好友id
     * @param verifier 接受消息的用户，即用户id
     */
    @Update("update user_message set is_read = 1 where proposer = #{proposer} and verifier = #{verifier}")
    void readRelationMessage(@Param("proposer")Integer proposer, @Param("verifier")Integer verifier);

    /**
     * 更新用户的最后聊天用户
     * @param uid 用户id
     * @param chatUid 最后聊天的好友id
     */
    @Update("update user set last_chat = #{chatUid} where uid = #{uid}")
    void changeLastChat(@Param("uid")Integer uid, @Param("chatUid")Integer chatUid);

    /**
     * 删除指定好友的全部消息，好友端数据同时删除
     * @param uid1 uid1为好友id，uid2为用户id，且可以互换
     * @param uid2 uid1为好友id，uid2为用户id，且可以互换
     */
    @Delete("delete from user_message where (proposer = #{uid1} and verifier = #{uid2}) or (verifier = #{uid1} and proposer = #{uid2})")
    void deleteRelationMessage(@Param("uid1")Integer uid1,@Param("uid2")Integer uid2);

    /**
     * 删除指定用户的全部消息，好友端数据同时删除
     * @param uid 用户id
     */
    @Delete("delete from user_message where proposer = #{uid} or verifier = #{uid}")
    void deleteUserMessage(@Param("uid")Integer uid);
}
