package com.senjyouhara.commons.mail.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MailProperty {

//  发件人  默认为spring.mail.username
  private String fromEmail;

//  收件人
  private String toEmail;

//  邮件标题
  private String emailTitle;

//  邮件文件路径  相对于资源文件目录下
  private String emailFilePath;

//  渲染页面的数据
  private Map<String,Object> data = new HashMap<>();

}
