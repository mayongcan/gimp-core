package com.gimplatform.core.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gimplatform.core.entity.SmsInfo;
import com.gimplatform.core.repository.SmsinfoRepository;
import com.gimplatform.core.service.SmsinfoService;
import com.gimplatform.core.utils.BaiduSms;
import com.gimplatform.core.utils.HttpUtils;

@Service
public class SmsinfoServiceImpl implements SmsinfoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsinfoRepository smsinfoRepository;

    @Override
    public int sendSms(String phone) {
        // sendByBaiduSms(phone);
        sendByHssmSms(phone);
        return 0;
    }

    @Override
    public int verifyCode(String phone, String smsCode) {
        logger.info("发送手机号码：" + phone + " 短信验证码：" + smsCode);
        List<SmsInfo> smsinfoList = smsinfoRepository.selectByPhoneAndSmsCode(phone, smsCode);
        logger.info("发送短信結束");
        // 半小时内发送一次
        if (smsinfoList != null && smsinfoList.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 使用百度服务发送短信
     * @param phone
     */
    public void sendByBaiduSms(String phone) {
        // 生成6位随机码
        int radomInt = new Random().nextInt(999999);
        String code = String.valueOf(radomInt);
        SmsInfo smsinfo = new SmsInfo();
        smsinfo.setSmsCode(code);
        smsinfo.setPhone(phone);
        smsinfo.setCreateDate(new Date());
        smsinfo.setIsValid("Y");

        // List<SmsInfo> smsinfoList = smsinfoRepository.selectByPhone(phone);
        // 1分钟内发送一次
        // if (smsinfoList.size() <= 0) {
        smsinfoRepository.save(smsinfo);
        // 发送短信
        String templateCode = "smsTpl:e7476122a1c24e37b3b0de19d04ae901"; // 本次发送使用的模板Code
        BaiduSms.sendSms(phone, templateCode, code);
        // }
        logger.info("发送手机号码：" + phone + " 短信验证码：" + code);
    }

    /**
     * 安徽省医疗服务综合监管平台 使用的短信平台
     * @param phone
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void sendByHssmSms(String phone) {
        // 生成6位随机码
        int radomInt = new Random().nextInt(999999);
        String code = String.valueOf(radomInt);
        SmsInfo smsinfo = new SmsInfo();
        smsinfo.setSmsCode(code);
        smsinfo.setPhone(phone);
        smsinfo.setCreateDate(new Date());
        smsinfo.setIsValid("Y");
        smsinfoRepository.save(smsinfo);

        String epid = "AHHF1312820";
        String usernamen = "admin";
        String password = "8749c73fdc44f772";
        Map params = new HashMap();
        params.put("epid", epid);
        params.put("User_Name", usernamen);
        params.put("password", password);
        params.put("phone", phone);
        params.put("content", "【综合监管平台】您的验证码为：" + code + "，请于30分钟内输入验证，妥善保管，以免信息泄露。");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        HttpUtils.post("http://access.xx95.net:8886/Connect_Service.asmx/SendSms", params, headers);
        logger.info("发送手机号码：" + phone + " 短信验证码：" + code);
    }
}
