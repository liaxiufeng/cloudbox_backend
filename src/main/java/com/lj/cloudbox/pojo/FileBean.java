package com.lj.cloudbox.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;


@Data
@NoArgsConstructor
@ToString
@TableName("file_item")
public class FileBean extends Model<FileBean> implements Comparable<FileBean> {
    @TableId(type = IdType.AUTO)
    private Integer fid;
    private Integer parent;
    private Integer uid;
    private String name;
    private Boolean isFolder;
    private Boolean isHeart;

    @TableField(exist = false)
    private String size;
    @TableField(exist = false)
    private Boolean isEmpty;
    @TableField(exist = false)
    private String lastUpdateDate;

    public FileBean(FileBean fileBean) {
        this.fid = fileBean.getFid();
        this.parent = fileBean.getParent();
        this.uid = fileBean.getUid();
        this.name = fileBean.getName();
        this.isFolder = fileBean.getIsFolder();
        this.isHeart = fileBean.getIsHeart();
    }

    @Override
    public int compareTo(FileBean o) {
        if (this.isFolder) {
            return o.getIsFolder() ? this.name.compareTo(o.getName()) : 1;
        } else {
            return o.getIsFolder() ? -1 : this.name.compareTo(o.getName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileBean) {
            FileBean temp = (FileBean) obj;
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
