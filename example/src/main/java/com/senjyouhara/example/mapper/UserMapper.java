package com.senjyouhara.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senjyouhara.example.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
