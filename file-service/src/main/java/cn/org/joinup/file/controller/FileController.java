package cn.org.joinup.file.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.file.domain.vo.FileVO;
import cn.org.joinup.file.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss/file")
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

}
