package com.lj.cloudbox.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.lj.cloudbox.mapper.typeHandler.UserTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName(autoResultMap = true)
public class UserRelation extends Model<UserRelation> {
    @TableId(type = IdType.AUTO)
    private Integer rid;
    @TableField(typeHandler = UserTypeHandler.class)
    private User proposer;
    @TableField(typeHandler = UserTypeHandler.class)
    private User verifier;
    private Date time;
}
