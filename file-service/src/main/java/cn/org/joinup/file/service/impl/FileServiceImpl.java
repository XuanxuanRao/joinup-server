package cn.org.joinup.file.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.file.domain.po.File;
import cn.org.joinup.file.domain.vo.FileVO;
import cn.org.joinup.file.mapper.FileMapper;
import cn.org.joinup.file.service.IFileService;
import cn.org.joinup.file.util.FileUtil;
import cn.org.joinup.file.util.OSSUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    private final OSSUtil ossUtil;

    @Override
    public Resource downloadFile(String filePath) {
        return ossUtil.download(filePath);
    }

    @Override
    @Transactional
    public FileVO uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            return null;
        }

        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
        String url;
        try {
            String objectName = UUID.randomUUID() + extension;
            url = ossUtil.upload(file.getBytes(), objectName);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setSize(file.getSize());
        fileEntity.setUrl(url);
        fileEntity.setMd5(FileUtil.calculateMD5(file));
        fileEntity.setUploaderId(UserContext.getUserId());
        save(fileEntity);

        return BeanUtil.copyProperties(fileEntity, FileVO.class);
    }

}
