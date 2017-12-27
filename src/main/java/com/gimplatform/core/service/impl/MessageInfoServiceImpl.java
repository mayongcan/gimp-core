/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

import com.gimplatform.core.service.MessageInfoService;
import com.gimplatform.core.entity.MessageInfo;
import com.gimplatform.core.entity.MessageUser;
import com.gimplatform.core.repository.MessageInfoRepository;
import com.gimplatform.core.repository.MessageUserRepository;
import com.gimplatform.core.repository.UserInfoRepository;

@Service
public class MessageInfoServiceImpl implements MessageInfoService {
	
    @Autowired
    private MessageInfoRepository messageInfoRepository;
	
    @Autowired
    private MessageUserRepository messageUserRepository;
	
    @Autowired
    private UserInfoRepository userInfoRepository;

	@Override
	public JSONObject getList(Pageable page, MessageInfo messageInfo, Map<String, Object> params) {
		List<Map<String, Object>> list = messageInfoRepository.getList(messageInfo, params, page.getPageNumber(), page.getPageSize());
		int count = messageInfoRepository.getListCount(messageInfo, params);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);	
	}

	@Override
	public JSONObject add(MessageInfo messageInfo, UserInfo userInfo, String userIdList, boolean sendAll, boolean isNotice) {
		messageInfo.setIsValid(Constants.IS_VALID_VALID);
		//如果是通知消息，则发送者设置为null
		if(isNotice)
			messageInfo.setCreateBy(null);
		else
			messageInfo.setCreateBy(userInfo.getUserId());
		messageInfo.setCreateDate(new Date());
		messageInfo = messageInfoRepository.save(messageInfo);
		messageInfoRepository.flush();
		//判断是否发给所有人
		if(sendAll){
			UserInfo searchUser = new UserInfo();
			searchUser.setTenantsId(userInfo.getTenantsId());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("findChildUsers", 1);
			List<Map<String, Object>> list = userInfoRepository.getUserList(searchUser, params, 0, 1000);
			for(Map<String, Object> map : list){
				MessageUser messageUser = new MessageUser();
				messageUser.setIsRead("0");
				messageUser.setIsSend("0");
				messageUser.setMsgId(messageInfo.getMsgId());
				messageUser.setUserId(MapUtils.getLong(map, "userId"));
				messageUserRepository.save(messageUser);
			}
		}else{
			if(StringUtils.isNotBlank(userIdList)){
				String[] idList = userIdList.split(",");
				for(String id : idList){
					MessageUser messageUser = new MessageUser();
					messageUser.setIsRead("0");
					messageUser.setIsSend("0");
					messageUser.setMsgId(messageInfo.getMsgId());
					messageUser.setUserId(StringUtils.toLong(id));
					messageUserRepository.save(messageUser);
				}
			}
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject edit(MessageInfo messageInfo, UserInfo userInfo, String userIdList, boolean sendAll) {
		MessageInfo messageInfoInDb = messageInfoRepository.findOne(messageInfo.getMsgId());
		if(messageInfoInDb == null){
			return RestfulRetUtils.getErrorMsg("51006","当前编辑的对象不存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(messageInfo, messageInfoInDb);
		messageInfoRepository.save(messageInfoInDb);
		
		//删除所有旧数据
		messageUserRepository.delByMsgId(messageInfo.getMsgId());
		//判断是否发给所有人
		if(sendAll){
			UserInfo searchUser = new UserInfo();
			searchUser.setTenantsId(userInfo.getTenantsId());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("findChildUsers", 1);
			List<Map<String, Object>> list = userInfoRepository.getUserList(searchUser, params, 0, 1000);
			for(Map<String, Object> map : list){
				MessageUser messageUser = new MessageUser();
				messageUser.setIsRead("0");
				messageUser.setIsSend("0");
				messageUser.setMsgId(messageInfo.getMsgId());
				messageUser.setUserId(MapUtils.getLong(map, "userId"));
				messageUserRepository.save(messageUser);
			}
		}else{
			if(StringUtils.isNotBlank(userIdList)){
				String[] idList = userIdList.split(",");
				for(String id : idList){
					MessageUser messageUser = new MessageUser();
					messageUser.setIsRead("0");
					messageUser.setIsSend("0");
					messageUser.setMsgId(messageInfo.getMsgId());
					messageUser.setUserId(StringUtils.toLong(id));
					messageUserRepository.save(messageUser);
				}
			}
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject del(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		//判断是否需要移除
		List<Long> idList = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			idList.add(StringUtils.toLong(ids[i]));
			//删除所有旧数据
			messageUserRepository.delByMsgId(StringUtils.toLong(ids[i]));
		}
		//批量更新（设置IsValid 为N）
		if(idList.size() > 0){
			messageInfoRepository.delEntity(Constants.IS_VALID_INVALID, idList);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject addAndSend(MessageInfo messageInfo, UserInfo userInfo, String userIdList, boolean sendAll, boolean isNotice) {
		messageInfo.setIsValid(Constants.IS_VALID_VALID);
		//如果是通知消息，则发送者设置为null
		if(isNotice)
			messageInfo.setCreateBy(null);
		else
			messageInfo.setCreateBy(userInfo.getUserId());
		messageInfo.setCreateDate(new Date());
		messageInfo.setSendDate(new Date());
		messageInfo = messageInfoRepository.save(messageInfo);
		//判断是否发给所有人
		if(sendAll){
			UserInfo searchUser = new UserInfo();
			searchUser.setTenantsId(userInfo.getTenantsId());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("findChildUsers", 1);
			List<Map<String, Object>> list = userInfoRepository.getUserList(searchUser, params, 0, 1000);
			for(Map<String, Object> map : list){
				MessageUser messageUser = new MessageUser();
				messageUser.setIsRead("0");
				messageUser.setIsSend("1");
				messageUser.setSendDate(new Date());
				messageUser.setMsgId(messageInfo.getMsgId());
				messageUser.setUserId(MapUtils.getLong(map, "userId"));
				messageUserRepository.save(messageUser);
			}
		}else{
			if(StringUtils.isNotBlank(userIdList)){
				String[] idList = userIdList.split(",");
				for(String id : idList){
					MessageUser messageUser = new MessageUser();
					messageUser.setIsRead("0");
					messageUser.setIsSend("1");
					messageUser.setSendDate(new Date());
					messageUser.setMsgId(messageInfo.getMsgId());
					messageUser.setUserId(StringUtils.toLong(id));
					messageUserRepository.save(messageUser);
				}
			}
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject send(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		for (int i = 0; i < ids.length; i++) {
			MessageInfo messageInfo = messageInfoRepository.getOne(StringUtils.toLong(ids[i]));
			if(messageInfo != null){
				messageInfo.setSendDate(new Date());
				messageInfoRepository.save(messageInfo);
				messageUserRepository.updateMessageIsSend(StringUtils.toLong(ids[i]), new Date());
			}
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject getMyMessage(Pageable page, MessageUser messageUser, Map<String, Object> params) {
		List<Map<String, Object>> list = messageUserRepository.getList(messageUser, params, page.getPageNumber(), page.getPageSize());
		int count = messageUserRepository.getListCount(messageUser, params);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);	
	}

	@Override
	public JSONObject setMessageRead(Long userMessageId) {
		if(userMessageId == null) return RestfulRetUtils.getErrorParams();
		messageUserRepository.updateMessageIsRead(userMessageId, new Date());
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public int getUnReadMessageCount(UserInfo userInfo) {
		return messageUserRepository.getUnReadMessageCount(userInfo);
	}
}
