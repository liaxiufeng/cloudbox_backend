package com.lj.cloudbox.utils.processor;

import com.lj.cloudbox.pojo.FileItem;
import com.lj.cloudbox.pojo.UserVo.FileItemVo;
import com.lj.cloudbox.utils.Constant;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;

@Data
@Component
public class FileProcessor {
    public FileItem process(FileItem fileItem) {
        FileItemVo fileItemVo = new FileItemVo(fileItem);
        String nextPath = fileItemVo.getPath();
        File file = new File(Constant.PATH + nextPath);
        if (!file.exists()) return fileItemVo;
        boolean isDir = file.isDirectory();
        fileItemVo.setIsFolder(isDir);
        if (!isDir) return fileItemVo;
        String[] list = file.list();
        fileItemVo.setIsEmpty(list == null || list.length == 0);
        return fileItemVo;
    }
}
