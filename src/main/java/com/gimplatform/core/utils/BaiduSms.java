package com.gimplatform.core.utils;

import java.util.HashMap;
import java.util.Map;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV2Request;
import com.baidubce.services.sms.model.SendMessageV2Response;

public class BaiduSms {

	static private String access_key_id = "3b7ca11bccb14e0790ffb9c68fa3589b";
	static private String secert_access_key = "16dde21b6b3c48bca833e100a849a8a6";
	// static public String template_id =
	// "smsTpl:e7476122a1c24e37b3b0de19d04ae901";
	static SmsClient client;

	public static int sendSms(String phoneNumber, String templateCode, String code) {
		// 相关参数定义
		String endPoint = "http://sms.bj.baidubce.com"; // SMS服务域名，可根据环境选择具体域名
		// String accessKeyId = "u23487324298ewuroiew"; // 发送账号安全认证的Access Key
		// ID
		// String secretAccessKy = "8273dsjhfkjdshf78327jkj"; // 发送账号安全认证的Secret
		// Access Key

		// ak、sk等config
		SmsClientConfiguration config = new SmsClientConfiguration();
		config.setCredentials(new DefaultBceCredentials(access_key_id, secert_access_key));
		config.setEndpoint(endPoint);

		// 实例化发送客户端
		SmsClient smsClient = new SmsClient(config);

		// 定义请求参数
		String invokeId = "gJLbGl9J-bE8P-BVDC"; // 发送使用签名的调用ID
		// String phoneNumber = "13450247865"; // 要发送的手机号码(只能填写一个手机号)
		// String templateCode = "smsTpl:e7476122a1c24e37b3b0de19d04ae901"; //
		// 本次发送使用的模板Code
		Map<String, String> vars = new HashMap<String, String>(); // 您的验证码是${code}，如非本人操作，请忽略本短信

		vars.put("code", code);

		// 实例化请求对象
		SendMessageV2Request request = new SendMessageV2Request();
		request.withInvokeId(invokeId).withPhoneNumber(phoneNumber).withTemplateCode(templateCode).withContentVar(vars);

		// 发送请求
		SendMessageV2Response response = smsClient.sendMessage(request);

		// 解析请求响应 response.isSuccess()为true 表示成功
		if (response != null && response.isSuccess()) {
			return 1;
		} else {
			return 0;
		}

	}

	public static void main(String[] args) {
		// 相关参数定义
		String endPoint = "http://sms.bj.baidubce.com"; // SMS服务域名，可根据环境选择具体域名
		// String accessKeyId = "u23487324298ewuroiew"; // 发送账号安全认证的Access Key
		// ID
		// String secretAccessKy = "8273dsjhfkjdshf78327jkj"; // 发送账号安全认证的Secret
		// Access Key

		// ak、sk等config
		SmsClientConfiguration config = new SmsClientConfiguration();
		config.setCredentials(new DefaultBceCredentials(access_key_id, secert_access_key));
		config.setEndpoint(endPoint);

		// 实例化发送客户端
		SmsClient smsClient = new SmsClient(config);

		// 定义请求参数
		String invokeId = "Gt9wVL58-JVrB-4mwu"; // 发送使用签名的调用ID
		String phoneNumber = "13450247865"; // 要发送的手机号码(只能填写一个手机号)
		String templateCode = "smsTpl:e7476122a1c24e37b3b0de19d04ae901"; // 本次发送使用的模板Code
		Map<String, String> vars = new HashMap<String, String>(); // 您的验证码是${code}，如非本人操作，请忽略本短信

		vars.put("code", "986082");

		// 实例化请求对象
		SendMessageV2Request request = new SendMessageV2Request();
		request.withInvokeId(invokeId).withPhoneNumber(phoneNumber).withTemplateCode(templateCode).withContentVar(vars);

		// 发送请求
		SendMessageV2Response response = smsClient.sendMessage(request);

		// 解析请求响应 response.isSuccess()为true 表示成功
		if (response != null && response.isSuccess()) {
			// submit success
		} else {
			// fail
		}
	}
}
