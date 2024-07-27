package com.tencent.wxcloudrun.model;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Data;




@Data
//@Primary 这个配置暂时用不上
@Configuration()
@PropertySource("classpath:application.yml") //读取配置文件
@ConfigurationProperties(prefix = "wx")
public class WXProperties {
    /** appId ： 微信公众号或者小程序等的appid */
    private  String appId;
    /** appSecret ： 应用密钥 */
    private  String appSecret;
    /** mchId ： 微信支付商户号 */
    private  String merchantId;
    /** mchKey ： 微信支付商户密钥 */
    private  String mchKey;
    /** mchKey ： 商户证书目录 */
    private  String keyPath ;
    /**商户API私钥路径*/
    private  String privateKeyPath ;
    /**商户API密钥证书路径*/
    private  String wechatPayCertificatePath ;
    /** 商户证书序列号*/
    private  String merchantSerialNumber ;
    /**商户APIV3密钥*/
    private  String apiV3Key ;
 
}