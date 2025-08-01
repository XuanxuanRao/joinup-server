package cn.org.joinup.file.util;

import cn.hutool.core.util.StrUtil;
import cn.org.joinup.file.config.OSSConfig;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 阿里云OSS操作工具类 <p>
 * 用于进行文件上传、下载、删除操作
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OSSUtil {

    private final OSSConfig ossConfig;

    /**
     * 文件上传
     *
     * @param bytes      file bytes
     * @param objectName filename in OSS
     * @return url of the file
     */
    public String upload(byte[] bytes, String objectName) {
        // 参数校验
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("文件内容不能为空");
        }
        if (StrUtil.isBlank(objectName)) {
            throw new IllegalArgumentException("对象名称不能为空");
        }

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

        try {
            ossClient.putObject(ossConfig.getBucketName(), objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS服务异常. ErrorCode: {}, Message: {}, RequestID: {}, HostID: {}",
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId(), oe.getHostId());
            throw new RuntimeException("文件上传失败(OSS异常)", oe);
        } catch (ClientException ce) {
            log.error("OSS客户端异常. Message: {}", ce.getMessage());
            throw new RuntimeException("文件上传失败(客户端异常)", ce);
        } catch (Exception e) {
            log.error("Unknown Error Occurs During Uploading File to OSS", e);
            throw new RuntimeException("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // url: https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://")
                .append(ossConfig.getBucketName())
                .append(".")
                .append(ossConfig.getEndpoint())
                .append("/")
                .append(objectName);

        log.info("file uploaded to {}", stringBuilder);

        return stringBuilder.toString();
    }

    /**
     * 获取文件内容
     * @param objectPath 文件路径
     * @return 文件内容
     */
    public Resource download(String objectPath) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

        // objectPath: https://BucketName.Endpoint/ObjectName
        String[] split = objectPath.split("/");
        String objectName = split[split.length - 1];

        try {
            OSSObject object = ossClient.getObject(ossConfig.getBucketName(), objectName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamUtils.copy(object.getObjectContent(), outputStream);
            ossClient.shutdown();
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (Throwable ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return null;
    }

}
