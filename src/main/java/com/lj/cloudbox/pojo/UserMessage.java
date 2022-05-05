package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lj.cloudbox.mapper.typeHandler.FileItemTypeHandler;
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
public class UserMessage extends Model<UserMessage> {
    @TableId(type = IdType.AUTO)
    private Integer mid;
    private Integer type;
    @TableField(typeHandler = UserTypeHandler.class)
    private User proposer;
    @TableField(typeHandler = UserTypeHandler.class)
    private User verifier;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date proposeTime;
    private Boolean isRead;
    private String message;
}
