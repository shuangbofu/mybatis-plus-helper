package com.example.dao;

import com.example.dao.mapper.UserMapper;
import com.example.entity.User;
import io.github.shuangbofu.helper.dao.BaseDao;

/**
 * Created by shuangbofu on 2021/8/22 1:53 下午
 */
public interface UserDao extends BaseDao<User, UserMapper> {
}
