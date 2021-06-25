package com.calvin.sharding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.calvin.sharding.mapper.UserMapper;
import com.calvin.sharding.pojo.TestUser;
import com.calvin.sharding.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Integer addUser(TestUser user) {
        return userMapper.insert(user);
    }

    @Override
    public List<TestUser> getUsers() {
        return userMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public TestUser getUser(Integer id) {
        return null;
    }

    @Override
    public boolean deleteOne(Integer id) {
        int i = userMapper.deleteById(id);
        return i > 0;
    }

    @Override
    public List<Map> getLianHe() {
        return userMapper.selectListIn();
    }


}