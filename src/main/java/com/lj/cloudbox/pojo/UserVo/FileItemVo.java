package com.lj.cloudbox.pojo.UserVo;

import com.lj.cloudbox.pojo.FileItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class FileItemVo extends FileItem {
    public FileItemVo(FileItem fileItem){
        if (fileItem == null)return;
        this.setFid(fileItem.getFid());
        this.setPath(fileItem.getPath());
        this.setUid(fileItem.getUid());
        this.setIsSecret(fileItem.getIsSecret());
        this.setIsHeart(fileItem.getIsHeart());
        this.setIsLock(fileItem.getIsLock());
        this.setIsProject(fileItem.getIsProject());
    }
    private Long size;
    private Boolean isFolder;
    private Boolean isEmpty;
}
