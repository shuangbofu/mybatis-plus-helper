package com.example.entity;

import io.github.shuangbofu.helper.entity.BaseEntity;

/**
 * Created by shuangbofu on 2021/8/22 1:54 下午
 */
public class User extends BaseEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
