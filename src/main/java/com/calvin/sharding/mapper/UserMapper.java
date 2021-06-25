package com.calvin.sharding.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calvin.sharding.pojo.TestUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface UserMapper extends BaseMapper<TestUser> {

    @Select("<script>" +
            "select" +
            " * " +
            " from test_user a left join user b on a.id=b.id" +
            "</script>")
    List<Map> selectListIn();
}
 