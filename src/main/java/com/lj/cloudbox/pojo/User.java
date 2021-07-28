package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
    private Date birthday;

    private Integer age;
    private String recommendWord;
    private String homePath;
    private Integer accountAge;


}
