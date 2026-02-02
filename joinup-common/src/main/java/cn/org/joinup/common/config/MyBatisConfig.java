package cn.org.joinup.common.config;

import cn.org.joinup.common.handler.StringArrayTypeHandler;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({MybatisPlusInterceptor.class, BaseMapper.class})
public class MyBatisConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 根据数据库URL自动检测数据库类型
        DbType dbType = detectDbType(databaseUrl);
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(dbType);
        paginationInnerInterceptor.setMaxLimit(100L);
        paginationInnerInterceptor.setOverflow(true);

        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    private DbType detectDbType(String url) {
        if (url.contains("postgresql")) {
            return DbType.POSTGRE_SQL;
        } else if (url.contains("mysql")) {
            return DbType.MYSQL;
        } else {
            return DbType.MYSQL;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.getTypeHandlerRegistry()
                .register(StringArrayTypeHandler.class);
    }
}