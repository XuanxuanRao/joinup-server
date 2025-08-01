package cn.org.joinup.file.service;

import cn.org.joinup.file.domain.po.File;
import cn.org.joinup.file.domain.vo.FileVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService extends IService<File> {

    Resource downloadFile(String fileName);

    FileVO uploadFile(MultipartFile file);
}
