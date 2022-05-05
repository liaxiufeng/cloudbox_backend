package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("file_item")
public class FileItem extends Model<FileItem> implements Comparable<FileItem> {
    @TableId(type = IdType.AUTO)
    private Integer fid;
    private Integer parent;
    private Integer uid;
    private String name;
    private Boolean isHeart;
    private Boolean isFolder;


    @TableField(exist = false)
    private String size;
    @TableField(exist = false)
    private Boolean isEmpty;
    @TableField(exist = false)
    private String lastUpdateDate;
    @TableField(exist = false)
    private Integer childrenNumber;

    public FileItem(FileItem fileItem) {
        this.fid = fileItem.getFid();
        this.parent = fileItem.getParent();
        this.uid = fileItem.getUid();
        this.name = fileItem.getName();
        this.isFolder = fileItem.getIsFolder();
        this.isHeart = fileItem.getIsHeart();
    }

    @Override
    public int compareTo(FileItem o) {
        if (this.isFolder) {
            return o.getIsFolder() ? this.name.compareTo(o.getName()) : 1;
        } else {
            return o.getIsFolder() ? -1 : this.name.compareTo(o.getName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileItem) {
            FileItem temp = (FileItem) obj;
            return this.uid.equals(temp.getUid()) &&
                    ((this.parent == null && temp.getParent() == null) || (this.parent.equals(temp.getParent()))) &&
                    this.name.equals(temp.getName()) &&
                    this.isFolder.equals(temp.getIsFolder());
        } else if (obj instanceof File) {
            File temp = (File) obj;
            return this.name.equals(temp.getName());
        } else {
            return false;
        }
    }
}
