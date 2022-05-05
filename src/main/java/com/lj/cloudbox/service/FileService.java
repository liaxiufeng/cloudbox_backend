package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.FileMapper;
import com.lj.cloudbox.pojo.FileItem;
import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.pojo.msg.MSG;
import com.lj.cloudbox.pojo.msg.Result;
import com.lj.cloudbox.pojo.vo.FileTree;
import com.lj.cloudbox.utils.date.DateUtils;
import com.lj.cloudbox.utils.file.FileSizeFormatUtil;
import com.lj.cloudbox.utils.ProjectSettings;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {
    @Autowired
    ProjectSettings projectSettings;

    @Autowired
    UserService userService;

    @Autowired
    FileMapper fileMapper;
/*
基本方法
 */

    /**
     * 获取文件的路径文件
     *
     * @param fileItem 文件
     * @return 文件的路径上的文件，前一个为后一个的父级文件
     */
    public List<FileItem> getPathFiles(FileItem fileItem) {
        if (fileItem == null) return null;
        List<FileItem> location = new LinkedList<>();
        FileItem temp = fileItem;
        try {
            while (temp.getParent() != 0) {
                location.add(temp);
                temp = fileMapper.selectById(temp.getParent());
            }
        } catch (NullPointerException e) {
            Collections.reverse(location);
            return location;
        }
        Collections.reverse(location);
        return location;
    }

    /**
     * 通过路径文件得到路径字符串
     *
     * @param location 路径文件
     * @return 路径字符串
     */
    public String getPath(List<FileItem> location) {
        if (location == null) return null;
        String[] locationStr = new String[location.size()];
        for (int i = 0; i < location.size(); i++) {
            locationStr[i] = location.get(i).getName();
        }
        return projectSettings.pathConcat(locationStr);
    }

    /**
     * 获取文件的路径字符串（包含文件名）
     *
     * @param fileItem 文件
     * @return 路径字符串
     */
    public String getPath(FileItem fileItem) {
        return getPath(getPathFiles(fileItem));
    }

    /**
     * 创建一个新的用户的家目录，文件名为随机的uuid
     *
     * @param uid 用户id
     * @return 家文件
     */
    public FileItem createHome(Integer uid) {
        String home = projectSettings.createHome();
        FileItem homeFile = new FileItem();
        homeFile.setName(home);
        homeFile.setUid(uid);
        homeFile.setIsFolder(true);
        homeFile.insert();
        return homeFile;
    }

    /**
     * 包装文件的大小或其他信息
     *
     * @param userHome    用户家文件名
     * @param fileItem    文件
     * @param locationStr 路径字符串
     */
    public void packaging(String userHome, FileItem fileItem, String locationStr) {
        String realPath = projectSettings.getRealPath(userHome, locationStr, fileItem.getName());
        File file = new File(realPath);
        if (!file.exists()) return;
        long fileSize = FileUtils.sizeOf(file);
        fileItem.setSize(FileSizeFormatUtil.formatFileSize(fileSize));
        fileItem.setIsEmpty(fileSize == 0);
        fileItem.setLastUpdateDate(DateUtils.parse_total(file.lastModified()));
    }

    /**
     * 获取文件树
     *
     * @param fileItem 文件
     * @return 文件树
     */
    public FileTree getTreeFiles(FileItem fileItem) {
        if (fileItem == null) return null;
        FileTree fileTree = new FileTree();
        fileTree.setFile(fileItem);
        packagingTreeFiles(fileTree);
        return fileTree;
    }

    private void packagingTreeFiles(FileTree parent) {
        FileItem fileItem = parent.getFile();
        if (fileItem.getIsFolder()) {
            List<FileItem> children = fileMapper.getChildren(fileItem.getFid());
            if (children == null || children.size() == 0) {
                parent.setHasChild(false);
                parent.setChildrenNumber(0);
                return;
            }
            LinkedList<FileTree> fileTrees = new LinkedList<>();
            int childrenNumber = 0;
            for (FileItem child : children) {
                if (child == null) continue;
                FileTree fileTree = new FileTree();
                fileTree.setFile(child);
                packagingTreeFiles(fileTree);
                fileTrees.add(fileTree);
                childrenNumber += fileTree.getChildrenNumber() + 1;
            }
            parent.setHasChild(true);
            parent.setChildren(fileTrees);
            parent.setChildrenNumber(childrenNumber);
        } else {
            parent.setHasChild(false);
            parent.setChildrenNumber(0);
        }
    }

    /**
     * 通过id数组获取文件数组
     *
     * @param fids id数组
     * @return 文件数组
     */
    public List<FileItem> getFiles(Integer[] fids) {
        for (Integer fid : fids) {
            if (fid < 0) return null;
        }
        return fileMapper.selectBatchIds(Arrays.asList(fids));
    }

    /**
     * 判断操作的文件是否为登录用户的文件夹
     *
     * @param uid   登录用户id
     * @param files 文件列表
     * @return 是否为同一个用户
     */
    public Boolean isSameUser(Integer uid, List<FileItem> files) {
        for (FileItem fileItem : files) {
            if (!uid.equals(fileItem.getUid())) return false;
        }
        return true;
    }

    public Boolean isSameUser(Integer uid, FileItem... files) {
        for (FileItem fileItem : files) {
            if (!uid.equals(fileItem.getUid())) return false;
        }
        return true;
    }

    public File realFile(FileItem homeFile, FileItem fileItem) {
        String path = getPath(fileItem);
        String realPath = projectSettings.getRealPath(homeFile.getName(), path);
        return new File(realPath);
    }

    public File getFile(Integer fid, User user) {
        if (fid <= 0) throw new RuntimeException("无效的文件标识！");
        return getFile(fileMapper.selectById(fid), user);
    }

    public File getFile(FileItem fileItem, User user) {
        if (fileItem == null || !fileItem.getUid().equals(user.getUid())) throw new RuntimeException("文件不存在！");
        String realPath = projectSettings.getRealPath(user.getHomeFile().getName(), getPath(fileItem));
        return new File(realPath);
    }
/*
    业务类方法
*/

    /**
     * 获取目标文件下的文件列表
     *
     * @param userHome 用户家文件名
     * @param fid      目标文件
     * @return 文件列表及其他信息
     */
    public Map<String, Object> getChildrenFiles(String userHome, Integer fid) {
        FileItem parent = fileMapper.selectById(fid);
        if (parent == null) return null;
        List<FileItem> locationFiles = getPathFiles(parent);

        String locationStr = getPath(locationFiles);
        List<FileItem> children = fileMapper.getChildren(fid);
        for (FileItem child : children) {
            packaging(userHome, child, locationStr);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("locationFiles", locationFiles);
        result.put("location", locationStr);
        result.put("fileData", children);
        return result;
    }

    /**
     * 复制或剪切文件时，检查有无命名冲突，如果无，直接进行文件操作
     *
     * @param user    用户
     * @param srcFids 源文件id数组
     * @param destFid 目标文件id
     * @param isCopy  是否为复制，false为剪切
     * @return MSG消息类型
     */
    public MSG checkMove(User user, Integer[] srcFids, Integer destFid, Boolean isCopy) {
        Integer uid = user.getUid();
        List<FileItem> srcFiles = getFiles(srcFids);
        if (srcFiles == null) return MSG.fail("源文件的文件不存在！");
        FileItem destFile;
        if (destFid == 0) {
            destFile = user.getHomeFile();
        } else {
            destFile = fileMapper.selectById(destFid);
        }
        if (destFile == null) return MSG.fail("目的文件不存在！");
        if (!destFile.getIsFolder()) return MSG.fail("目的文件不是一个文件夹！");
        for (FileItem fileItem : srcFiles) {
            Integer parent = fileItem.getParent();
            if (parent == null) return MSG.fail("该文件不支持移动！");
            if (parent.equals(destFile.getFid())) return MSG.fail("文件已经存在该路径下");
        }
        if (uid.equals(destFile.getUid()) && isSameUser(uid, srcFiles)) {
            List<FileItem> conflict = new LinkedList<>();
            checkMoveFileCheckNameConflict(srcFiles, destFile, conflict);
            if (conflict.size() == 0) {
                return MSG.success("无冲突", moveFiles(user, srcFiles, destFile, isCopy, true));
            } else {
                return MSG.conflict("存在命名冲突", conflict);
            }
        } else {
            return MSG.fail("不能跨用户进行文件操作！");
        }
    }

    private void checkMoveFileCheckNameConflict(List<FileItem> srcFiles, FileItem destFile, List<FileItem> conflict) {
        if (srcFiles == null) return;
        for (FileItem srcFile : srcFiles) {
            checkMoveFileCheckNameConflict(getTreeFiles(srcFile), getTreeFiles(destFile), conflict);
        }
    }

    private void checkMoveFileCheckNameConflict(FileTree srcFile, FileTree destFile, List<FileItem> conflict) {
        FileItem srcFileItem = srcFile.getFile();
        if (!destFile.getHasChild()) return;
        FileItem destFileItem = destFile.getFile();
        List<FileTree> srcChildren = srcFile.getChildren();
        List<FileTree> destChildren = destFile.getChildren();
        if (!srcFileItem.getIsFolder() && destChildren != null) {
            FileItem srcCopy = new FileItem(srcFileItem);
            srcCopy.setParent(destFileItem.getFid());
            for (FileTree destChild : destChildren) {
                if (srcCopy.equals(destChild.getFile())) {
                    conflict.add(srcFileItem);
                }
            }
        }
        if (srcChildren != null && destChildren != null) {
            for (FileTree srcChild : srcChildren) {
                for (FileTree destChild : destChildren) {
                    checkMoveFileCheckNameConflict(srcChild, destChild, conflict);
                }
            }
        }

    }

    /**
     * 复制或剪切多个源文件到一个目标文件中
     *
     * @param user     登录用户
     * @param srcFids  多个源文件id
     * @param destFid  目标文件id
     * @param isCopy   是否为复制，否为剪切
     * @param override 命名冲突是否为覆盖，否为忽略
     * @return MSG消息
     */
    public MSG moveFiles(User user, Integer[] srcFids, Integer destFid, Boolean isCopy, Boolean override) {
        Integer uid = user.getUid();
        List<FileItem> srcFiles = getFiles(srcFids);
        if (srcFiles == null) return MSG.fail("源文件的文件不存在！");
        FileItem destFile;
        if (destFid == 0) {
            destFile = user.getHomeFile();
        } else {
            destFile = fileMapper.selectById(destFid);
        }
        if (destFile == null) return MSG.fail("目的文件不存在！");
        if (uid.equals(destFile.getUid()) && isSameUser(uid, srcFiles)) {
            return MSG.success("完成", moveFiles(user, srcFiles, destFile, isCopy, override));
        } else {
            return MSG.fail("不能跨用户进行文件操作！");
        }
    }


    private Map<String, List<Result>> moveFiles(User user, List<FileItem> srcFiles, FileItem destFile, Boolean isCopy, Boolean override) {
        String destPath = getPath(destFile);
        String destRealPath = projectSettings.getRealPath(user.getHomeFile().getName(), destPath);
        File destRealFile = new File(destRealPath);
        List<Result> success = new LinkedList<>();
        List<Result> fail = new LinkedList<>();
        for (FileItem srcFile : srcFiles) {
            String srcPath = getPath(srcFile);
            String srcRealPath = projectSettings.getRealPath(user.getHomeFile().getName(), srcPath);
            File srcRealFile = new File(srcRealPath);
            if (override) {
                moveFileByOverride(srcFile, destFile, srcRealFile, destRealFile, isCopy, success, fail);
            } else {
                FileTree src = getTreeFiles(srcFile);
                FileTree dest = getTreeFiles(destFile);
                moveFileByIgnore(src, dest, srcRealFile, destRealFile, isCopy, success, fail);
            }
        }
        Map<String, List<Result>> map = new HashMap<>();
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    private Integer moveFileByIgnore(FileTree src, FileTree dest, File srcRealFile, File destRealFile, Boolean isCopy, List<Result> success, List<Result> fail) {
        if (!destRealFile.exists() || !srcRealFile.exists() || !destRealFile.isDirectory() || src == null || dest == null)
            return 0;
        int actioned = 0;
        FileItem srcFile = src.getFile();
        FileItem destFile = dest.getFile();

        FileItem srcCopy = new FileItem(srcFile);
        srcCopy.setParent(destFile.getFid());
        FileItem sameName = fileMapper.sameName(srcCopy);
        FileTree destTemp = null;
        boolean conflict = sameName != null;
        if (conflict) {
            for (FileTree child : dest.getChildren()) {
                if (sameName.equals(child.getFile())) destTemp = child;
            }
            if (destTemp == null) destTemp = getTreeFiles(sameName);
        } else {
            boolean isSuccess = true;
            if (srcFile.getIsFolder()) {
                File file = new File(destRealFile, srcFile.getName());
                if (!file.exists()) {
                    file.mkdirs();
                }
            } else {
                try {
                    FileUtils.copyFileToDirectory(srcRealFile, destRealFile);
                } catch (IOException e) {
                    isSuccess = false;
                    e.printStackTrace();
                }
            }
            if (isSuccess) {
                srcCopy.insert();

                destTemp = new FileTree();
                destTemp.setFile(srcCopy);
                destTemp.setHasChild(false);
                destTemp.setChildrenNumber(0);

                success.add(Result.success(srcFile, (isCopy ? "复制" : "剪切") + "文件成功！"));
                actioned = 1;
            } else {
                fail.add(Result.fail(srcFile, (isCopy ? "复制" : "剪切") + "文件失败！"));
                actioned = 0;
            }
        }
        if (src.getHasChild()) {
            List<FileTree> children = src.getChildren();
            for (FileTree child : children) {
                actioned += moveFileByIgnore(child, destTemp, new File(srcRealFile, child.getFile().getName()), new File(destRealFile, srcFile.getName()), isCopy, success, fail);
            }
        }
        if (!isCopy && actioned == src.getChildrenNumber() + 1) {
            srcFile.deleteById();
            srcRealFile.delete();
        }
        return actioned;
    }

    private void moveFileByOverride(FileItem srcFile, FileItem destFile, File srcRealFile, File destRealFile, Boolean isCopy, List<Result> success, List<Result> fail) {
        try {
            if (isCopy) {
                if (srcRealFile.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(srcRealFile, destRealFile);
                } else {
                    FileUtils.copyFileToDirectory(srcRealFile, destRealFile);
                }
                copyFileBean(srcFile, destFile);
            } else {
                FileUtils.moveToDirectory(srcRealFile, destRealFile, false);
                cutFileBeanByOverride(srcFile, destFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail.add(new Result(srcFile, false, (isCopy ? "复制" : "剪切") + "文件出错"));
        }
        success.add(new Result(srcFile, true, (isCopy ? "复制" : "剪切") + "文件成功"));
    }


    private void copyFileBean(FileItem srcFile, FileItem destFile) {
        FileItem srcCopy = new FileItem(srcFile);
        srcCopy.setParent(destFile.getFid());
        FileItem sameName = fileMapper.sameName(srcCopy);
        if (sameName == null) {
            srcCopy.setFid(null);
            srcCopy.insert();
        } else {
            srcCopy = sameName;
        }
        if (srcFile.getIsFolder()) {
            List<FileItem> children = fileMapper.getChildren(srcFile.getFid());
            if (children != null && children.size() > 0) {
                for (FileItem child : children) {
                    copyFileBean(child, srcCopy);
                }
            }
        }
    }

    private void cutFileBeanByOverride(FileItem srcFile, FileItem destFile) {
        copyFileBean(srcFile, destFile);
        fileMapper.deleteById(srcFile.getFid());
    }

    /**
     * 重命名文件
     *
     * @param user    用户
     * @param fid     文件id
     * @param newName 新文件名
     * @return MSG消息
     */
    public MSG reName(User user, Integer fid, String newName) {
        if (!StringUtils.hasLength(newName)) return MSG.fail("文件名不能为空！");
        FileItem fileItem = fileMapper.selectById(fid);
        Integer parent;
        try {
            parent = fileItem.getParent();
        } catch (NullPointerException e) {
            return MSG.fail("用户的根目录不允许改名！");
        }
        if (!fileMapper.checkNewName(user.getUid(), parent, fileItem.getIsFolder(), newName))
            return MSG.fail("已存在该名字的文件或文件夹！");
        String path = getPath(fileItem);
        String realPath = projectSettings.getRealPath(user.getHomeFile().getName(), path);
        String location = realPath.replaceAll("/[^/]+$", "");
        String newPath = projectSettings.pathConcat(location, newName);
        File srcFile = new File(realPath);
        if (!srcFile.exists()) return MSG.fail("文件不存在！");
        if (fileMapper.reName(fid, newName) == 1) {
            srcFile.renameTo(new File(newPath));
            return MSG.success("文件夹重命名成功！！");
        } else {
            return MSG.fail("文件夹重命名失败！");
        }
    }

    /**
     * 删除文件
     *
     * @param user 操作的用户
     * @param fids 文件标识数组
     * @return 结果储存对象
     */
    public Map<String, List<FileItem>> delete(User user, Integer[] fids) {
        Map<String, List<FileItem>> map = new HashMap<>();
        List<FileItem> success = new LinkedList<>();
        List<FileItem> fail = new LinkedList<>();
        for (Integer fid : fids) {
            FileItem fileItem = fileMapper.selectById(fid);
            if (fileItem.getUid().equals(user.getUid())) {
                File realFile = realFile(user.getHomeFile(), fileItem);
                delete(fileItem, realFile, success, fail);
            } else {
                fail.add(fileItem);
            }
        }
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    /**
     * 删除文件
     *
     * @param fileItem 文件数据对象
     * @param realFile 文件实体对象
     * @param success  保存成功删除文件列表
     * @param fail     保存失败删除文件列表
     */
    public void delete(FileItem fileItem, File realFile, List<FileItem> success, List<FileItem> fail) {
        if (fileMapper.deleteById(fileItem.getFid()) == 1) {
            if (realFile.exists()) {
                FileUtils.deleteQuietly(realFile);
            }
            success.add(fileItem);
        } else {
            fail.add(fileItem);
        }
    }

    /**
     * 上传文件本质上是新建一个文件，并把文件的内容复制进去
     *
     * @param user   用户名
     * @param parent 新建文件所在的文件夹的id
     * @param name   新文件名
     * @param is     文件的输入流
     * @throws IOException 用户身份认证失败，或者复制文件内容时发生错误
     */
    public void upload(User user, Integer parent, String name, InputStream is) throws IOException {
        FileItem parentFile = parent == 0 ? user.getHomeFile() : fileMapper.selectById(parent);
        if (!user.getUid().equals(parentFile.getUid())) throw new IOException();
        String path = getPath(parentFile);
        String totalPath = projectSettings.getRealPath(user.getHomeFile().getName(), path, name);
        FileUtils.copyInputStreamToFile(is, new File(totalPath));
        FileItem fileItem = new FileItem();
        fileItem.setIsFolder(false);
        fileItem.setName(name);
        fileItem.setUid(user.getUid());
        fileItem.setParent(parentFile.getFid());
        fileItem.setIsHeart(false);
        fileItem.insert();
    }


    /**
     * 下载文件或文件夹
     *
     * @param user     请求操作的用户
     * @param fid      文件标识
     * @param response 响应对象
     * @throws Exception 文件不存在或者读取错误
     */
    public void download(User user, Integer fid, HttpServletResponse response) throws Exception {
        if (fid <= 0) throw new RuntimeException("无效的文件标识！");
        FileItem fileItem = fileMapper.selectById(fid);
        if (fileItem == null || !fileItem.getUid().equals(user.getUid())) throw new RuntimeException("文件不存在！");
        String realPath = projectSettings.getRealPath(user.getHomeFile().getName(), getPath(fileItem));
        System.out.println("realPath = " + realPath);
        download(response, realPath);
    }

    /**
     * 下载文件或文件夹
     *
     * @param response 响应对象
     * @param path     完整路径
     * @throws Exception 文件不存在或者读取错误
     */
    private void download(HttpServletResponse response, String path) throws Exception {
        // 转为path
        Path folderPath = Paths.get(path);
        // 响应为二进制数据流
        response.setContentType("application/octet-stream");
        if (!Files.isDirectory(folderPath)) { // 文件下载
            File file = new File(path);
            if (!file.exists()) {
                throw new IOException("file not exists: " + path);
            }
            try (InputStream input = new FileInputStream(file);
                 OutputStream output = response.getOutputStream()) {
                // 写入数据
                int len;
                // 设置10kb缓冲区
                byte[] buffer = new byte[1024 * 10];
                // 文件设置，附件的形式打开
                response.setHeader("content-disposition", "attachment; filename=" + file.getName());
                while ((len = input.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                output.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else { // 文件夹下载
            // 文件设置，附件形式打开
            response.setHeader("content-disposition", "attachment;");
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
                // 文件路径/ID
                LinkedList<String> filePath = new LinkedList<>();
                Files.walkFileTree(folderPath, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        // 开始遍历目录
                        if (!dir.equals(folderPath)) {
                            filePath.addLast(dir.getFileName().toString());
                            // 写入目录
                            ZipEntry zipEntry = new ZipEntry(filePath.stream().collect(Collectors.joining("/", "", "/")));
                            try {
                                zipOutputStream.putNextEntry(zipEntry);
                                zipOutputStream.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // 开始遍历文件
                        try (InputStream inputStream = Files.newInputStream(file)) {
                            // 创建一个压缩项，指定名称
                            String fileName = filePath.size() > 0
                                    ? filePath.stream().collect(Collectors.joining("/", "", "")) + "/" + file.getFileName().toString()
                                    : file.getFileName().toString();
                            ZipEntry zipEntry = new ZipEntry(fileName);
                            // 添加到压缩流
                            zipOutputStream.putNextEntry(zipEntry);
                            // 写入数据
                            int len;
                            // 设置10kb缓冲区
                            byte[] buffer = new byte[1024 * 10];
                            while ((len = inputStream.read(buffer)) > 0) {
                                zipOutputStream.write(buffer, 0, len);
                            }
                            zipOutputStream.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        // 结束遍历目录
                        if (!filePath.isEmpty()) {
                            filePath.removeLast();
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        保存文本文件
     */
    public void txtSave(User user, Integer fid, String content) throws Exception {
        File file = getFile(fid, user);
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.write(content);
        printWriter.flush();
    }

    public void createFile(User user, Boolean isFolder, String fileName, Integer parent) throws IOException {
        FileItem parentFile = parent == 0 ? user.getHomeFile() : fileMapper.selectById(parent);
        if (!user.getUid().equals(parentFile.getUid())) throw new IOException();
        String path = getPath(parentFile);
        String totalPath = projectSettings.getRealPath(user.getHomeFile().getName(), path, fileName);
        File file = new File(totalPath);
        if (file.exists()) throw new IOException("文件已存在！");
        if (isFolder) {
            file.mkdir();
        } else {
            file.createNewFile();
        }
        FileItem fileItem = new FileItem();
        fileItem.setUid(user.getUid());
        fileItem.setName(fileName);
        fileItem.setParent(parent == 0 ? user.getHomeFile().getFid() : parent);
        fileItem.setIsFolder(isFolder);
        fileItem.setIsHeart(false);
        System.out.println("fileItem = " + fileItem);
        try {
            fileItem.insert();
        } catch (Exception e) {
            file.delete();
            throw e;
        }
    }
}
