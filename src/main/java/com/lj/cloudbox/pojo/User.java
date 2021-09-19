package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.lj.cloudbox.mapper.typeHandler.HomeFileTypeHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@ToString
@TableName("user")
public class User extends Model<User> {
    @TableId(type = IdType.AUTO)
    private Integer uid;
    private String username;
    private String email;
    private String password;
    private String describeWord;
    @TableField(typeHandler = HomeFileTypeHandler.class)
    private FileBean homeFile;
    private Date birthday;
    private Date accountBirthday;

    @TableField(exist = false)
    private Integer age;
    @TableField(exist = false)
    private Integer accountAge;
    @TableField(exist = false)
    private String usedSpace;
    @TableField(exist = false)
    private String totalSpace;
    @TableField(exist = false)
    private String freeSpace;
    @TableField(exist = false)
    private Long freeSpaceLong;
    @TableField(exist = false)
    private Double usedPercent;
}
