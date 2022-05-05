package com.lj.cloudbox.pojo.vo;

import com.lj.cloudbox.pojo.FileItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class FileTree {
    private Boolean hasChild;
    private FileItem file;
    private Integer childrenNumber;
    private List<FileTree> children;
}
