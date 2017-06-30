package com.cgutech.filesystem.controller;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cgutech.filesystem.dd.dao.DUserDao;
import com.cgutech.filesystem.dd.ding.pushTemplate.TextMessage;
import com.cgutech.filesystem.dd.ding.service.DingPushService;

@Controller
@RequestMapping("/dd")
public class DDPushController {
	@Autowired
	private DingPushService dingPushService;
	@Autowired
	private DUserDao dao;

	private Logger log = LoggerFactory.getLogger(DDPushController.class);

public static void main(String[] args) throws UnsupportedEncodingException {
	String text="测试";
	text=Base64.getUrlEncoder().encodeToString(text.getBytes("UTF8"));
	System.out.println(text);
	byte[] bs=Base64.getUrlDecoder().decode(text.getBytes());
	text=new String(bs, "UTF8");
	System.out.println(text);
}
	@RequestMapping("/push")
	@ResponseBody
	public String  pushadmin(@RequestParam(value = "text") String  text, HttpServletRequest request,
			@RequestParam(value = "userid",defaultValue="") String userId,
			@RequestParam(value = "dname",defaultValue="") String dname,HttpServletResponse response) throws UnsupportedEncodingException {		
		try {
				if(userId.equals("")&&dname.equals("")){
					return "参数userid或者dname不能为空";
				}
				if(userId.equals("")&&!dname.equals("")){
					userId=dao.findDuserByName(dname).getUserid();
				}
				// 通知审核钉钉管理员
				log.info(text);
				byte[] bs=Base64.getUrlDecoder().decode(text.getBytes());
				text=new String(bs, "UTF8");
				log.info("推送内容："+text);
				
					TextMessage textMessage = new TextMessage();
					dingPushService.pushService(textMessage.getDingPushMessageBean(text, userId));
				} catch (Exception e) {
					e.printStackTrace();
					return "error::"+e.getMessage();
				}
				
				return "success";

	}

}
