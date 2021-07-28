package com.lj.cloudbox.controller;

import com.lj.cloudbox.pojo.MSG;
import com.lj.cloudbox.utils.ProjectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class FileController {
    @Autowired
    ProjectProperties projectProperties;

    @PostMapping("fileUpLoad")
    public MSG fileUpLoad(@RequestPart("file")MultipartFile file,
                          @RequestPart("files")MultipartFile files) throws IOException {
        String fileName = file.getOriginalFilename();
        if (!file.isEmpty()){
            file.transferTo(new File(""));
        }
        return MSG.success("");
    }




}
