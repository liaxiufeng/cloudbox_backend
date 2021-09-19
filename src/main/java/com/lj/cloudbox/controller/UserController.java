package com.lj.cloudbox.controller;

import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.service.UserService;
import com.lj.cloudbox.utils.ProjectSettings;
import com.lj.cloudbox.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    ProjectSettings projectSettings;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public MSG login(User user) {
        User loginUser = userMapper.login(user);
        if (loginUser != null) {
            String uid = loginUser.getUid() + "";
            String token = TokenUtil.sign(uid);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            System.out.println("UserController : token = " + token);
            return MSG.success("登录成功", map);
        }
        return MSG.fail("登录失败");
    }

    @GetMapping("/user")
    @ResponseBody
    public MSG getLoginUser(@RequestAttribute("user") User user) {
        return user == null ? MSG.fail("未登录") : MSG.success("已登录", user);
    }

    @GetMapping("/user/used")
    @ResponseBody
    public MSG getUserUsed(@RequestAttribute("user") User user) {
        if (user == null) return MSG.fail("未登录");
        userService.packaging(user);
        return MSG.success("已登录", user);
    }


    @PostMapping("/register")
    public MSG register(User user) {
        Boolean flag = userMapper.canRegister(user);
        if (flag) {
            user.setAccountBirthday(new Date());
            user.insert();
            userService.createHome(user);
            return MSG.success("注册成功");
        }
        return MSG.fail("用户名或邮箱已存在");
    }


    @PostMapping("/register/checkUserName")
    public MSG userNameCheck(@RequestParam("username") String username) {
        return userMapper.userNameIsAvailable(username) ? MSG.success("用户名可用") : MSG.fail("用户名已存在");
    }

    @PostMapping("/register/checkEmail")
    public MSG userEmail(@RequestParam("email") String email) {
        return userMapper.emailIsAvailable(email) ? MSG.success("邮箱可用") : MSG.fail("邮箱已存在");
    }
}
