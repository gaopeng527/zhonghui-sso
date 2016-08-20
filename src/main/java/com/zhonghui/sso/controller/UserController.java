package com.zhonghui.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * 用户Controller
 * @author DELL
 *
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huizhong.pojo.TbUser;
import com.zhonghui.common.pojo.ZhonghuiResult;
import com.zhonghui.common.utils.ExceptionUtil;
import com.zhonghui.sso.service.UserService;
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public Object checkData(@PathVariable String param, @PathVariable Integer type, String callback){
		
		ZhonghuiResult result = null;
		
		
		// 参数有效性校验
		if(StringUtils.isBlank(param)){
			result = ZhonghuiResult.build(400, "校验内容不能为空");
		}
		if(type == null){
			result = ZhonghuiResult.build(400, "校验的内容类型不能为空");
		}
		if(type != 1 && type != 2 && type != 3){
			result = ZhonghuiResult.build(400, "校验的内容类型错误");
		}
		// 校验出错
		if(null != result){
			if(null != callback){
				MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
				mappingJacksonValue.setJsonpFunction(callback);
				return mappingJacksonValue;
			}else{
				return result;
			}
		}
		// 调用服务
		try {
			result = userService.checkData(param, type);
		} catch (Exception e) {
			result = ZhonghuiResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(null != callback){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else{
			return result;
		}
	}
	
	// 创建用户
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseBody
	public ZhonghuiResult createUser(TbUser user){
		try {
			ZhonghuiResult result = userService.createUser(user);
			return result;
		} catch (Exception e) {
			return ZhonghuiResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	// 用户登录
	@RequestMapping(value="/login", method=RequestMethod.POST)
	@ResponseBody
	public ZhonghuiResult userLogin(String username, String password,
			HttpServletRequest request, HttpServletResponse response){
		try {
			ZhonghuiResult result = userService.userLogin(username, password, request, response);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ZhonghuiResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	@RequestMapping("/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback){
		ZhonghuiResult result = null;
		try {
			result = userService.getUserByToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = ZhonghuiResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		// 判断是否为jsonp调用
		if(StringUtils.isBlank(callback)){
			return result;
		}else{
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
	}
}
