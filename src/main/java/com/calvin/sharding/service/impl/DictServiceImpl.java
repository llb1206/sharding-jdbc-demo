package com.calvin.sharding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.calvin.sharding.mapper.DictMapper;
import com.calvin.sharding.mapper.UserMapper;
import com.calvin.sharding.pojo.SysDict;
import com.calvin.sharding.pojo.TestUser;
import com.calvin.sharding.service.DictService;
import com.calvin.sharding.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DictServiceImpl implements DictService {

    @Resource
    private DictMapper dictMapper;

    @Override
    public Integer addDict(SysDict sysDict) {
        return dictMapper.insert2(sysDict);
    }

}