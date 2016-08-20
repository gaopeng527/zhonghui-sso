package com.zhonghui.sso.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huizhong.pojo.TbUser;
import com.zhonghui.common.pojo.ZhonghuiResult;

public interface UserService {

	ZhonghuiResult checkData(String content, Integer type);
	ZhonghuiResult createUser(TbUser user);
	ZhonghuiResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response);
	ZhonghuiResult getUserByToken(String token);
}
