package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.model.WXProperties;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;

import cn.hutool.http.HttpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.spec.AlgorithmParameterSpec;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Resource;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import java.util.Arrays;
import java.util.List;

// import com.wechat.pay.java.core.Config;
// import com.wechat.pay.java.core.RSAAutoCertificateConfig;
// import com.wechat.pay.java.core.exception.HttpException;
// import com.wechat.pay.java.core.exception.MalformedMessageException;
// import com.wechat.pay.java.core.exception.ServiceException;
// import com.wechat.pay.java.service.payments.jsapi.JsapiService;
// import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
// import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
// import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
// import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByIdRequest;
// import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
// import com.wechat.pay.java.service.payments.model.Transaction;

/**
 * counter控制器
 */
@RestController

public class CounterController {

  final CounterService counterService;
  final Logger logger;
  @Resource
  private WXProperties wxProperties;

  public CounterController(@Autowired CounterService counterService) {
    this.counterService = counterService;
    this.logger = LoggerFactory.getLogger(CounterController.class);
  }


  /**
   * 获取当前计数
   * @return API response json
   */
  @GetMapping(value = "/api/count")
  ApiResponse get() {
    logger.info("/api/count get request");
    Optional<Counter> counter = counterService.getCounter(1);
    Integer count = 0;
    if (counter.isPresent()) {
      count = counter.get().getCount();
    }

    return ApiResponse.ok(count);
  }



 
  @PostMapping("/getOpenid")
  public String getOpenid(@RequestParam("code") String code){
       //这里是直接拼接的一个url
    String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" 
                + wxProperties.getAppId() + "&secret=" + wxProperties.getAppSecret() 
                + "&js_code=" + code + "&grant_type=authorization_code";
    //HttpUtil这个是hutool工具包里面的，一个很好用的封装工具；当然你也可以用别的工具
    String result = HttpUtil.get(url);
    return result;
  }



  // public static RSAAutoCertificateConfig config = null ;
  // public static JsapiServiceExtension service = null ;

  // @GetMapping("/prepay")
  // public PrepayWithRequestPaymentResponse WeChartPay(String amountString) {
     
  //     String openId = "oI*******************iGiA";
  //     // 订单号
  //     String orderUuid = IdUtils.getUUID();
  //     //元转换为分
  //     Integer amountInteger = Integer.valueOf(AmountUnitConversionUtil.changeYuanAndFen(amountString));
  //     //私钥文件路径（本地自己测试看自己的私钥文件存放路径）
  //     String filePath ="***/***/***/apiclient_key.pem";//测试环境可放到resource目录下

  //     // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
  //     if (config == null) {
  //         config =new RSAAutoCertificateConfig.Builder()
  //                         .merchantId(mchId)
  //                         .privateKeyFromPath(filePath)
  //                         .merchantSerialNumber(merchantSerialNumber)
  //                         .apiV3Key(apiV3Key)
  //                         .build();
  //     }
  //     // 构建service
  //     if (service == null) {
  //         service = new JsapiServiceExtension.Builder().config(config).build();
  //     }

  //     //组装预约支付的实体
  //     // request.setXxx(val)设置所需参数，具体参数可见Request定义
  //     PrepayRequest request = new PrepayRequest();
  //     //计算金额
  //     Amount amount = new Amount();
  //     amount.setTotal(amountInteger);
  //     amount.setCurrency("CNY");
  //     request.setAmount(amount);
  //     //公众号appId
  //     request.setAppid(appId);
  //     //商户号
  //     request.setMchid(mchId);
  //     //支付者信息
  //     Payer payer = new Payer();
  //     payer.setOpenid(openId);
  //     request.setPayer(payer);
  //     //描述
  //     request.setDescription("支付测试");
  //     //微信回调地址，需要是https://开头的，必须外网可以正常访问
  //     //本地测试可以使用内网穿透工具，网上很多的
  //     request.setNotifyUrl(v3PayNotifyUrl);
  //     //订单号
  //     request.setOutTradeNo(orderUuid);
  //     // 加密
  //     PrepayWithRequestPaymentResponse payment = service.prepayWithRequestPayment(request);
  //     //默认加密类型为RSA
  //     payment.setSignType("MD5");
  //    //返回数据，前端调起支付
  //     return payment;
  // }



  @PostMapping("/wxLogin")
  public ApiResponse wxLogin(@RequestParam("code") String code,
                              @RequestParam("encryptedIv") String encryptedIv,
                              @RequestParam("rawData") String rawData,
                              @RequestParam("encryptedData") String encryptedData) 
                              {
        //wx.login返回的code
        logger.info("微信登录参数code：" + code);
        //不需要解密的话可以不传入这些参数看自己业务
        //解密的iv
        logger.info("登录信息参数encryptedIv：" + encryptedIv);
        //要解密的数据
        logger.info("登录信息参数encryptedData：" + encryptedData);
        //rawData微信头像，昵称等信息，看自己业务需要
        logger.info("登录微信账户参数信息rawData：" + rawData);
        //想微信服务器发送请求获取用户信息
        //https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="
                + wxProperties.getAppId() + "&secret=" + wxProperties.getAppSecret()
                + "&js_code=" + code + "&grant_type=authorization_code";
        String result = HttpUtil.get(url );
        JSONObject jsonObject = JSONObject.parseObject(result);
        logger.info("result：" + result);
        //获取session_key和openid
        String sessionKey = jsonObject.getString("session_key");
        //解密 是获取微信用户的手机号
        String decryptResult = "";
        try {
            //如果没有绑定微信开放平台，解析结果是没有unionid的。
            decryptResult = decryptionUserInfo(sessionKey, encryptedIv, encryptedData);
            return ApiResponse.ok(decryptResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("微信登录失败！");
        }
        //解析成功就可以写自己的系统业务逻辑了
        //我这里就开始调用登录方法创建token了，至于自己的登录方法逻辑是什么要做什么逻辑处理就看业务需要了
        // if (StringUtils.hasText(decryptResult)) {
        //     //如果解析成功,获取token
        //     String token = loginService.wxLogin(decryptResult, result, rawData);
        //     Result r = Result.success();
        //     r.put(Constants.TOKEN, token);
        //     return r;
        // } else {
        //     return AjaxResult.error("微信登录失败！");
        // }
    }


  public String decryptionUserInfo(String sessionKey, String iv, String encryptedData) {
        String result = null;
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            //初始化
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivByte);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] doFinal = cipher.doFinal(dataByte);
            result = new String(doFinal);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


  /**
   * 更新计数，自增或者清零
   * @param request {@link CounterRequest}
   * @return API response json   create方法处理所有发送到/api/count的post请求
   */
  @PostMapping(value = "/api/count")
  ApiResponse create(@RequestBody CounterRequest request) {
    logger.info("/api/count post request, action: {}", request.getAction());

    Optional<Counter> curCounter = counterService.getCounter(1);
    if (request.getAction().equals("inc")) {
      Integer count = 1;
      if (curCounter.isPresent()) {
        count += curCounter.get().getCount();
      }
      Counter counter = new Counter();
      counter.setId(1);
      counter.setCount(count);
      counterService.upsertCount(counter);
      return ApiResponse.ok(count);
    } else if (request.getAction().equals("clear")) {
      if (!curCounter.isPresent()) {
        return ApiResponse.ok(0);
      }
      counterService.clearCount(1);
      return ApiResponse.ok(0);
    } else {
      return ApiResponse.error("参数action错误");
    }
  }
  
}