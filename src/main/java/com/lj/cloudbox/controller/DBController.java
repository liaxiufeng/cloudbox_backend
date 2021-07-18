package com.lj.cloudbox.controller;

import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.FileItem;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class DBController {

    @GetMapping("/show")
    public Map<String,Object> test(){
        HashMap<String, Object> data = new HashMap<>();
        data.put("users",new User().selectAll());
        data.put("files",new FileItem().selectAll());
        return data;
    }

}
