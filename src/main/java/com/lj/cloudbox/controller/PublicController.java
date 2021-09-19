package com.lj.cloudbox.controller;

import com.lj.cloudbox.service.FileService;
import com.lj.cloudbox.utils.ProjectSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("public")
public class PublicController {
    @Autowired
    ProjectSettings projectSettings;
    @Autowired
    FileService fileService;
}
