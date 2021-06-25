package com.calvin.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calvin.sharding.pojo.SysDict;
import com.calvin.sharding.pojo.TestUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface DictMapper extends BaseMapper<SysDict> {


    @Insert("insert into sys_dict(id,dict_code,dict_value) values(#{id},#{dict_code},#{dict_value}) ")
    Integer insert2(SysDict sysDict);


    @Insert("insert into user(id,name,code) values(#{id},#{name},#{code})")
    Integer insertUser(Long id,String name,String code);

    @Select("SELECT username FROM test_user where  id = #{id} ")
    List<String> addDict22(Long id,List ids);

    @Select("<script>" +
            "select" +
            " * " +
            " from test_user t " +
            " where t.id in " +
            " <foreach collection='orderIds' open='(' separator=',' close=')' item='id'>" +
            " #{id} " +
            " </foreach>" +
            "</script>")
    List<String> addDict222(@Param("orderIds") List<Long> orderIds);

    @Select("SELECT * FROM test_user where  id BETWEEN #{i} AND #{i1}")
    List<String> addDict2222(int i, int i1);
}
 