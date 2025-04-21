package cn.org.joinup.common.check;

import com.github.houbb.sensitive.word.api.IWordDeny;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@Slf4j
public class CustomWordDeny implements IWordDeny {
    @Override
    public List<String> deny() {
        List<String> list = new ArrayList<>();
        try {
            Resource mySensitiveWords = new ClassPathResource("sensitive-words.txt");
            Path mySensitiveWordsPath = Paths.get(mySensitiveWords.getFile().getPath());
            log.info("尝试读取敏感词文件路径: {}", mySensitiveWordsPath); // 打印文件路径
            list =  Files.readAllLines(mySensitiveWordsPath, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("读取敏感词文件错误！", ioException); // 打印完整的异常堆栈
        }
        return list;
    }

}
