package com.calvin.sharding;

import com.calvin.sharding.mapper.DictMapper;
import com.calvin.sharding.pojo.SysDict;
import com.calvin.sharding.pojo.TestUser;
import com.calvin.sharding.service.DictService;
import com.calvin.sharding.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * https://learnku.com/articles/51239
 */
@SpringBootTest
class ShardingJdbcDemoApplicationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private DictService dictService;
    @Resource
    private DictMapper dictMapper;



    @Test
    public void getUsers() {
        List<TestUser> list = userService.getUsers();
        System.out.printf(list.toString());
    }

    @Test
    public void getUser() {
        Object x= userService.getUser(11);
        System.out.printf(x==null?"null":x.toString());
    }

    @Test
    public void add() {
        for (int i = 1; i <= 5; i++) {
            TestUser user = new TestUser();
            user.setUsername("ssssss-" + (i));
            user.setPassword("sssss" + i);
            userService.addUser(user);
        }
    }

    @Test
    public void addXx() {
        for (int i = 2; i <= 5; i++) {
            dictMapper.insertUser(Long.parseLong(i+""),"user-"+i,"ss-"+i);
        }
    }
    @Test
    public void getLianHe() {
        List<Map> list = userService.getLianHe();
        System.out.printf(list.toString());
    }

    @Test
    public void deleteOne() {
        userService.deleteOne(2);
    }

    /**
     * 测试公共表
     */
    @Test
    public void addDict() {
        SysDict dict = new SysDict();
        dict.setDict_code("Y");
        dict.setDict_value("正常");
        dictService.addDict(dict);
    }

    /**
     * 1.1 精准分库算法
     * 实现自定义精准分库、分表算法的方式大致相同，都要实现 PreciseShardingAlgorithm 接口，并重写 doSharding() 方法，只是配置稍有不同，而且它只是个空方法，得我们自行处理分库、分表逻辑。其他分片策略亦如此。
     *
     * SELECT * FROM t_order where  order_id = 1 or order_id in （1,2,3）;
     */
    @Test
    public void addDict2() {
      List l1=  Arrays.asList(1l,2l,3l);
     List<String> list=  dictMapper.addDict22(1L,l1 );
        for (String map : list) {
            System.out.println(map.toString());
        }
    }
    /**
     * 1.2 精准分表算法
     * 实现自定义精准分库、分表算法的方式大致相同，都要实现 PreciseShardingAlgorithm 接口，并重写 doSharding() 方法，只是配置稍有不同，而且它只是个空方法，得我们自行处理分库、分表逻辑。其他分片策略亦如此。
     *
     * SELECT * FROM t_order where  order_id = 1 or order_id in （1,2,3）;
     */
    @Test
    public void addDict22() {
        List<String> list=  dictMapper.addDict222(Arrays.asList(1l,2l,3l));
        for (String map : list) {
            System.out.println(map);
        }
    }

    /**
     * 2、范围分片算法
     * 使用场景：当我们 SQL 中的分片健字段用到 BETWEEN AND 操作符会使用到此算法，会根据 SQL 中给出的分片健值范围值处理分库、分表逻辑。
     */
    @Test
    public void addDict2222() {
        List<String> list=  dictMapper.addDict2222(1,10000);
        for (String map : list) {
            System.out.println(map);
        }
    }
}
