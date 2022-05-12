package com.lj.cloudbox.controller;

import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.service.MailService;
import com.lj.cloudbox.service.UserService;
import com.lj.cloudbox.utils.CommonUtils;
import com.lj.cloudbox.utils.ProjectSettings;
import com.lj.cloudbox.utils.TokenUtil;
import com.lj.cloudbox.utils.file.FileTypeUtils;
import com.lj.cloudbox.utils.file.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    MailService mailService;

    @PostMapping("/login")
    public MSG login(User user) {
        User loginUser = userMapper.login(user);
        if (loginUser != null) {
            String uid = String.valueOf(loginUser.getUid());
            String token = TokenUtil.sign(uid);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            System.out.println("UserController : token = " + token);
            return MSG.success("登录成功", map);
        }
        return MSG.fail("登录失败");
    }

    @GetMapping("/mail/login")
    public MSG mailVerifierLogin(@RequestParam("verifierMail") String verifierMail,
                                 @RequestParam("verifierCode") String verifierCode){
        boolean verifierResult = mailService.verifierMailCode(verifierMail, verifierCode);
        if (!verifierResult) return MSG.fail("登录失败");
        User user = userMapper.getUserByEmail(verifierMail);
        if (user != null) {
            String uid = String.valueOf(user.getUid());
            String token = TokenUtil.sign(uid);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return MSG.success("登录成功", map);
        }
        return MSG.fail("登录失败");
    }

    @GetMapping("/user")
    public MSG getLoginUser(@RequestAttribute("user") User user) {
        return user == null ? MSG.fail("未登录") : MSG.success("已登录", user);
    }

    @GetMapping("/user/detail")
    public MSG getUserUsed(@RequestAttribute("user") User user) {
        if (user == null) return MSG.fail("未登录");
        userService.packaging(user);
        return MSG.success("已登录", user);
    }

    @GetMapping("/mail/sendCode")
    public void mailProposer(@RequestParam("verifierMail") String verifierMail){
        mailService.mail(verifierMail);
    }

    @GetMapping("/mail/verifier")
    public MSG mailVerifier(@RequestParam("verifierMail") String verifierMail,
                            @RequestParam("verifierCode") String verifierCode){
        return mailService.verifierMailCode(verifierMail, verifierCode) ? MSG.success("验证邮箱成功") : MSG.fail("验证邮箱失败");
    }



    @PostMapping("/register")
    public MSG register(User user) {
        Boolean flag = userMapper.canRegister(user);
        if (flag) {
            user.setAccountBirthday(new Date());
            user.setSex("男");
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

    @PutMapping("/user/username")
    public MSG updateUserName(@RequestAttribute("user") User user,
                              @RequestParam("username") String username){
        if (!CommonUtils.haveValue(username)) return MSG.fail("用户名不能为空！",user.getUsername());
        Boolean available = userMapper.userNameIsAvailable(username);
        if (available){
            Integer updateNum = userMapper.updateUserName(user.getUid(), username);
            return updateNum == 1 ? MSG.success("用户名修改成功!") : MSG.fail("用户名修改失败!",user.getUsername());
        }else {
             return MSG.fail("用户名已存在！",user.getUsername());
        }
    }

    @PutMapping("/user/email")
    public MSG updateEmail(@RequestAttribute("user") User user,
                           @RequestParam("email") String email){
        if (!CommonUtils.haveValue(email)) return MSG.fail("邮箱不能为空！",user.getEmail());
        Boolean available = userMapper.emailIsAvailable(email);
        if (available){
            Integer updateNum = userMapper.updateEmail(user.getUid(), email);
            return updateNum == 1 ? MSG.success("邮箱修改成功!") : MSG.fail("邮箱修改失败!",user.getEmail());
        }else {
            return MSG.fail("邮箱已存在！",user.getEmail());
        }
    }

    @PutMapping("/user/password")
    public MSG updatePassword(@RequestAttribute("user") User user,
                              @RequestParam("password") String password){
        if (!CommonUtils.haveValue(password)) return MSG.fail("密码不能为空！");
        Integer updateNum = userMapper.updatePassword(user.getUid(), password);
        return updateNum == 1 ? MSG.success("密码修改成功!") : MSG.fail("密码修改失败!");
    }

    @PutMapping("/user/describeWord")
    public MSG updateDescribeWord(@RequestAttribute("user") User user,
                                  @RequestParam("describeWord") String describeWord){
        Integer updateNum = userMapper.updateDescribeWord(user.getUid(), describeWord);
        return updateNum == 1 ? MSG.success("签名修改成功!") : MSG.fail("签名修改失败!",user.getDescribeWord());
    }

    @PutMapping("/user/birthday")
    public MSG updateBirthday(@RequestAttribute("user") User user,
                              @RequestParam("birthday") String birthday){
        if (!CommonUtils.haveValue(birthday)) return MSG.fail("生日不能为空！");
        Integer updateNum = userMapper.updateBirthday(user.getUid(), birthday);
        return updateNum == 1 ? MSG.success("生日修改成功!") : MSG.fail("生日修改失败!",user.getBirthday());
    }

    @PutMapping("/user/sex")
    public MSG updateSex(@RequestAttribute("user") User user,
                              @RequestParam("sex") String sex){
        if (!CommonUtils.haveValue(sex)) return MSG.fail("性别不能为空！");
        int sexInteger;
        if ("男".equals(sex)){
            sexInteger = 1;
        }else if ("女".equals(sex)){
            sexInteger = 2;
        }else {
            return MSG.fail("参数错误",user.getSex());
        }
        Integer updateNum = userMapper.updateSex(user.getUid(), sexInteger);
        return updateNum == 1 ? MSG.success("性别修改成功!") : MSG.fail("性别修改失败!",user.getSex());
    }



    @PostMapping("/user/photo")
    public MSG updateUserPhoto(@RequestAttribute("user") User user,
                               @RequestParam("file") MultipartFile file) throws IOException {
        long size = file.getSize();
        if (size > 2*1024*1024) return MSG.fail("头像文件不能大于2M!");
        if (size == 0) return MSG.fail("上传文件为空！");
        boolean isImage = FileTypeUtils.isImage(file.getInputStream());
        if (!isImage) return MSG.fail("请上传图片文件");
        String base64 = ImageUtils.getBase64(file.getInputStream());
        Integer updateNum = userMapper.updatePhoto(user.getUid(), base64);
        return updateNum == 1 ? MSG.success("头像修改成功!",base64) : MSG.fail("头像修改失败!",user.getPhoto());
    }
}
