package com.zhonghui.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.huizhong.mapper.TbUserMapper;
import com.huizhong.pojo.TbUser;
import com.huizhong.pojo.TbUserExample;
import com.huizhong.pojo.TbUserExample.Criteria;
import com.zhonghui.common.pojo.ZhonghuiResult;
import com.zhonghui.sso.service.UserService;
/**
 * 用户管理Service
 * @author DELL
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	@Override
	public ZhonghuiResult checkData(String content, Integer type) {
		// 创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();		
		// 对数据进行校验：1、2、3分别代表username、phone、email
		// 用户名校验
		if(1 == type){
			criteria.andUsernameEqualTo(content);
		}else if(2 == type){ // 电话校验
			criteria.andPhoneEqualTo(content);
		}else { // 邮箱校验
			criteria.andEmailEqualTo(content);
		}
		// 执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size() == 0){
			return ZhonghuiResult.ok(true);
		}
		return ZhonghuiResult.ok(false);
	}

	@Override
	public ZhonghuiResult createUser(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// md5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return ZhonghuiResult.ok();
	}

}
