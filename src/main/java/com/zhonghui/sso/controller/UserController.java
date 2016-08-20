package com.zhonghui.sso.controller;

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
	@RequestMapping("/register")
	@ResponseBody
	public ZhonghuiResult createUser(TbUser user){
		try {
			ZhonghuiResult result = userService.createUser(user);
			return result;
		} catch (Exception e) {
			return ZhonghuiResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
}
