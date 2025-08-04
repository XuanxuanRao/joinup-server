package cn.org.joinup.file.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.file.domain.po.File;
import cn.org.joinup.file.domain.vo.FileVO;
import cn.org.joinup.file.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping({"/oss/file", "/external/oss/file"})
@RequiredArgsConstructor
public class FileController {

    private final IFileService fileService;

    @PostMapping("/upload")
    public Result<FileVO> upload(MultipartFile file) {
        FileVO fileVO = fileService.uploadFile(file);
        if (fileVO == null) {
            return Result.error("OSS Service Error!");
        } else {
            return Result.success(fileVO);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String fileUrl) {
        Resource resource = fileService.downloadFile(fileUrl);
        File file = fileService.lambdaQuery()
                .eq(File::getUrl, fileUrl)
                .one();
        if (resource == null || file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8) + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
