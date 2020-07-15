package com.senjyouhara.example;

import lombok.extern.log4j.Log4j2;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoTest {


	@Autowired
	private StringEncryptor stringEncryptor;



	@Test
	public void test(){

	}


}
