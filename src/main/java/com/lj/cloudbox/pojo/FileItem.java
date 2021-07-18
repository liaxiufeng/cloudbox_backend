package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@TableName("file_item")
public class FileItem extends Model<FileItem> {
    private Integer fid;
    private String path;
    private Integer uid;
    private Boolean isSecret;
    private Boolean isHeart;
    private Boolean isLock;
    private Boolean isProject;

}
