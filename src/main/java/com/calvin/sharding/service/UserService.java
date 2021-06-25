package com.calvin.sharding.service;

import com.calvin.sharding.pojo.TestUser;
import java.util.List;
import java.util.Map;

public interface UserService {
 
    Integer addUser(TestUser user);
 
    List<TestUser> getUsers();

    TestUser getUser(Integer id);
 
    boolean deleteOne(Integer id);

    List<Map> getLianHe();
}