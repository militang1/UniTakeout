package com.nbufe.zfr.UniTakeout_server.controller.admin;

import com.nbufe.zfr.UniTakeout_server.common.Result;
import com.nbufe.zfr.UniTakeout_server.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class AdminUploadController {
    
    @Autowired
    private AliOssUtil aliOssUtil;
    
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }
            
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            
            // 支持的图片格式
            String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
            boolean isValidExtension = false;
            for (String ext : allowedExtensions) {
                if (extension.equalsIgnoreCase(ext)) {
                    isValidExtension = true;
                    break;
                }
            }
            
            if (!isValidExtension) {
                return Result.error("不支持的文件格式，仅支持：jpg, jpeg, png, gif, bmp, webp");
            }
            
            // 验证文件大小（限制为5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过5MB");
            }
            
            // 生成唯一文件名
            String folder = "images/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String objectName = folder + "/" + fileName;
            
            // 上传到OSS
            byte[] fileBytes = file.getBytes();
            String url = aliOssUtil.upload(fileBytes, objectName);
            
            return Result.success("上传成功", url);
            
        } catch (IOException e) {
            return Result.error("文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}


