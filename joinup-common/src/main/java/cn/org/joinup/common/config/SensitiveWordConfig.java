package cn.org.joinup.common.config;

import cn.org.joinup.common.check.CustomWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;

import com.github.houbb.sensitive.word.support.deny.WordDenys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author chenxuanrao06@gmail.com
 */
@Configuration
@ComponentScan("cn.org.joinup.common.check")
public class SensitiveWordConfig {
    @Resource
    private CustomWordDeny customWordDeny;

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .wordDeny(WordDenys.chains(WordDenys.system(), customWordDeny)) // 设置黑名单
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(true)
                .enableEmailCheck(true)
                .enableUrlCheck(true)
                .init();
    }

}
