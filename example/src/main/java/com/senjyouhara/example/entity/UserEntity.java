package com.senjyouhara.example.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_table")
public class UserEntity {

	@TableId
	private Integer id;

	private String type;

	private String username;

	private String password;

	private String token;

	private String saltKey;

	private String email;

}
