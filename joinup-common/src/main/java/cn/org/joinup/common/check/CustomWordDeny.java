package cn.org.joinup.common.check;

import com.github.houbb.sensitive.word.api.IWordDeny;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            try (InputStream inputStream = mySensitiveWords.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                list = reader.lines().collect(Collectors.toList());
                log.info("成功读取敏感词数量：{}", list.size());
            }
        } catch (IOException e) {
            log.error("读取敏感词文件错误！", e);
        }
        return list;
    }

}
