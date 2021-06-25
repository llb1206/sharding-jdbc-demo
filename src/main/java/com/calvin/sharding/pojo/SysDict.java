package com.calvin.sharding.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dict")
public class SysDict {

    private Long id;
    private String  dict_code;
    private String dict_value;
}
