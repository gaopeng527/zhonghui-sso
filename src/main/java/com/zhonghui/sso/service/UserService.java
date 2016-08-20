package com.zhonghui.sso.service;

import com.huizhong.pojo.TbUser;
import com.zhonghui.common.pojo.ZhonghuiResult;

public interface UserService {

	ZhonghuiResult checkData(String content, Integer type);
	ZhonghuiResult createUser(TbUser user);
	ZhonghuiResult userLogin(String username, String password);
	ZhonghuiResult getUserByToken(String token);
}
