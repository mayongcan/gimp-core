package com.gimplatform.core.service;

public interface SmsinfoService {

    /**
     * 发送短信验证码
     * @param cdmsCumDeviceType
     * @return
     */
    public int sendSms(String phone);

    public int verifyCode(String phone, String smsCode);

}
