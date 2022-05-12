package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lj.cloudbox.mapper.typeHandler.FileItemTypeHandler;
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
public class RecentOpenFile extends Model<RecentOpenFile>{
    @TableId(type = IdType.AUTO)
    private Integer rid;
    @TableField(value = "fid",typeHandler = FileItemTypeHandler.class)
    private FileItem fileItem;
    private Integer uid;
    private Boolean isFolder;
    private Date time;
}
