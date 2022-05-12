package com.lj.cloudbox.controller;

import com.lj.cloudbox.exception.NameExistException;
import com.lj.cloudbox.mapper.FileMapper;
import com.lj.cloudbox.mapper.UserMapper;
import com.lj.cloudbox.pojo.FileItem;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.vo.FileTree;
import com.lj.cloudbox.service.FileService;
import com.lj.cloudbox.service.RecentOpenFileService;
import com.lj.cloudbox.utils.CommonUtils;
import com.lj.cloudbox.utils.file.FileTypeUtils;
import com.lj.cloudbox.utils.file.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RecentOpenFileService recentOpenFileService;

    @GetMapping("files")
    public MSG filesList(@RequestAttribute("user") User user,
                         @RequestParam("fid") Integer fid) {
        if (fid < 0) return MSG.fail("无效的文件编号");
        String userHome = user.getHomeFile().getName();
        if (fid == 0) {
            Integer homeFileId = user.getHomeFile().getFid();
            return MSG.success("获取文件成功", fileService.getChildrenFiles(userHome, homeFileId));
        } else {
            recentOpenFileService.open(fileMapper.selectById(fid));
            return MSG.success("获取文件成功", fileService.getChildrenFiles(userHome, fid));
        }
    }

    @GetMapping("file")
    public MSG file(@RequestAttribute("user") User user,
                    @RequestParam("fid") Integer fid){
        if (!CommonUtils.haveValue(user.getUid(),fid)) return MSG.fail("参数错误");
        FileItem fileItem = fileMapper.selectById(fid);
        return fileItem.getUid().equals(user.getUid()) ? MSG.success("获取文件成功", fileItem) : MSG.fail("无权限操作文件！");
    }

    @GetMapping("createNewFile")
    public void createNewFile(@RequestAttribute("user") User user,
                              @RequestParam("isFolder") Boolean isFolder,
                              @RequestParam("fileName") String fileName,
                              @RequestParam("parent") Integer parent)throws IOException{
        fileService.createFile(user,isFolder,fileName,parent);
    }



    @GetMapping("fileType")
    public MSG fileCheck(@RequestAttribute("user") User user,
                         @RequestParam("fid") Integer fid) {
        File file = fileService.getFile(fid, user);
        Map<String, Boolean> res = new HashMap<>();
        res.put("isTxt", FileTypeUtils.isTxt(file));
        res.put("isImg", FileTypeUtils.isImage(file));
        return MSG.success("成功", res);
    }

    @GetMapping("txt")
    public void txtGet(@RequestAttribute("user") User user,
                       @RequestParam("fid") Integer fid,
                       HttpServletResponse response) throws Exception {
        recentOpenFileService.open(fileMapper.selectById(fid));
        File file = fileService.getFile(fid, user);
        // 响应为二进制数据流
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        ServletOutputStream os = response.getOutputStream();
        FileInputStream is = new FileInputStream(file);
        CommonUtils.write(is, os);
    }

    @PutMapping("txt")
    public MSG txtSave(@RequestAttribute("user") User user,
                       @RequestParam("fid") Integer fid,
                       @RequestParam("content") String content) {
        try {
            fileService.txtSave(user, fid, content);
            return MSG.success("文件保存成功！");
        } catch (Exception e) {
            return MSG.fail("文件保存出错！");
        }
    }

    @GetMapping("img")
    public MSG imgGet(@RequestAttribute("user") User user,
                      @RequestParam("fid") Integer fid,
                      HttpServletResponse response) throws FileNotFoundException {
        recentOpenFileService.open(fileMapper.selectById(fid));
        File file = fileService.getFile(fid, user);
        if (file == null) return MSG.fail("获取文件失败");
//        String base64 = ImageUtils.getBase64(file);
        String base64 = ImageUtils.getBase64(new FileInputStream(file));
        return MSG.success("获取成功",base64);
    }


    @PutMapping("rename")
    public MSG renameFile(@RequestAttribute("user") User user,
                          @RequestParam("fid") Integer fid,
                          @RequestParam("newName") String newName) {
        return fileService.reName(user, fid, newName);
    }

    @DeleteMapping("delete")
    public MSG deleteFiles(@RequestAttribute("user") User user,
                           @RequestBody Integer[] fids) {
        return MSG.success("完成", fileService.delete(user, fids));
    }

    @PostMapping("move/conflict")
    public MSG moveFiles(@RequestAttribute("user") User user,
                         @RequestBody Integer[] srcFids,
                         @RequestParam("dest") Integer destFid,
                         @RequestParam("isCopy") Boolean isCopy,
                         @RequestParam("override") Boolean override) {
        return fileService.moveFiles(user, srcFids, destFid, isCopy, override);
    }


    @PostMapping("move")
    public MSG checkMove(@RequestAttribute("user") User user,
                         @RequestBody Integer[] srcFids,
                         @RequestParam("dest") Integer destFid,
                         @RequestParam("isCopy") Boolean isCopy) {
        return fileService.checkMove(user, srcFids, destFid, isCopy);
    }

    @PostMapping("upload")
    public MSG fileUpLoad(@RequestAttribute("user") User user,
                          @RequestParam("file") MultipartFile file,
                          @RequestParam("parent") Integer parent) throws Exception {
        String originalFilename = file.getOriginalFilename();
        Boolean available = fileMapper.checkNewName(user.getUid(), parent, false, originalFilename);
        if (!available) throw new NameExistException("文件名"+originalFilename+"已经存在!");
        fileService.upload(user, parent, originalFilename, file.getInputStream());
        return MSG.success(originalFilename + "上传成功");
    }


    @GetMapping("download")
    public void filesDownload(@RequestAttribute("user") User user,
                              @RequestParam("fid") Integer fid,
                              HttpServletResponse response) throws Exception {
        fileService.download(user, fid, response);
    }

    @GetMapping("heart")
    public MSG getHeart(@RequestAttribute("user") User user){
        List<FileItem> heart = fileMapper.getHeart(user.getUid());
        return MSG.success("读取成功！",heart);
    }

    @PutMapping("heart")
    public void addHeart(@RequestAttribute("user") User user,
                              @RequestParam("fid") Integer fid){
        User user1 = user.selectById();
        fileMapper.addHear(user.getUid(),fid);
    }

    @DeleteMapping("heart")
    public void removeHeart(@RequestAttribute("user") User user,
                         @RequestParam("fid") Integer fid){
        fileMapper.removeHear(user.getUid(),fid);
    }

    @DeleteMapping("heart/all")
    public void removeHeart(@RequestAttribute("user") User user){
        fileMapper.removeHeartAll(user.getUid());
    }



    @GetMapping("tree")
    public List<FileTree> getTreeFile(@RequestAttribute("user") User user,
                                      @RequestParam("fid") Integer fid) {
        if (fid == 0) {
            return fileService.getTreeFiles(user.getHomeFile()).getChildren();
        }
        return fileService.getTreeFiles(fileMapper.selectById(fid)).getChildren();
    }

}