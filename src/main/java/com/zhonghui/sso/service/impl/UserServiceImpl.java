package com.zhonghui.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.huizhong.mapper.TbUserMapper;
import com.huizhong.pojo.TbUser;
import com.huizhong.pojo.TbUserExample;
import com.huizhong.pojo.TbUserExample.Criteria;
import com.zhonghui.common.pojo.ZhonghuiResult;
import com.zhonghui.common.utils.JsonUtils;
import com.zhonghui.sso.dao.JedisClient;
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
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;
	
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

	// 用户登录
	@Override
	public ZhonghuiResult userLogin(String username, String password) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		// 如果没有此用户名
		if(null == list || list.size() == 0){
			return ZhonghuiResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		// 比对密码
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
			return ZhonghuiResult.build(400, "用户名或密码错误");
		}
		// 生成token
		String token = UUID.randomUUID().toString();
		// 保存用户之前，把用户对象中的密码清空
		user.setPassword(null);
		String key = REDIS_USER_SESSION_KEY+":"+token;
		// 把用户信息写入redis
		jedisClient.set(key, JsonUtils.objectToJson(user));
		// 设置session的过期时间
		jedisClient.expire(key, SSO_SESSION_EXPIRE);
		// 返回token
		return ZhonghuiResult.ok(token);
	}

	@Override
	public ZhonghuiResult getUserByToken(String token) {
		// 根据token从redis中查询用户信息
		String key = REDIS_USER_SESSION_KEY+":"+token;
		String json = jedisClient.get(key);
		// 判断是否为空
		if(StringUtils.isBlank(json)){
			return ZhonghuiResult.build(400, "此session已经过期，请重新登录");
		}
		// 更新过期时间
		jedisClient.expire(key, SSO_SESSION_EXPIRE);
		// 返回用户信息
		return ZhonghuiResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}

}
