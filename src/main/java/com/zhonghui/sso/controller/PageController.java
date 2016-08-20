package com.zhonghui.sso.controller;

import org.springframework.stereotype.Controller;
/**
 * 页面跳转Contoller
 * @author DELL
 *
 */
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/page")
public class PageController {
	
	/**
	 * 注册页面
	 * @return
	 */
	@RequestMapping("/register")
	public String showRegister(){
		return "register";
	}
	
	/**
	 * 登录页面
	 * @return
	 */
	@RequestMapping("/login")
	public String showLogin(){
		return "login";
	}
}
