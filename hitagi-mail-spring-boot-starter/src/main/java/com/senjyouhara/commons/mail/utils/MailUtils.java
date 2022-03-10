
package com.senjyouhara.commons.mail.utils;

import com.senjyouhara.commons.mail.config.MailProperty;
import com.senjyouhara.core.config.ApplicationContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Set;

public class MailUtils {

  private static JavaMailSender javaMailSender;

  private static TemplateEngine templateEngine;

  private static String mail;

  static {
    ApplicationContext context = ApplicationContextHolder.getApplicationContext();
    javaMailSender = context.getBean(JavaMailSender.class);
    templateEngine = context.getBean(TemplateEngine.class);
    mail = context.getEnvironment().getProperty("spring.mail.username");
  }

  private static void setMessage(MimeMessage mimeMessage, String emailContent, MailProperty mailProperty) throws MessagingException {
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    mimeMessageHelper.setTo(mailProperty.getToEmail());
    mimeMessageHelper.setFrom(StringUtils.isNotBlank(mailProperty.getFromEmail())? mailProperty.getFromEmail() : mail);
    mimeMessageHelper.setSubject(mailProperty.getEmailTitle());
    mimeMessageHelper.setText(emailContent, true);
      // 发送邮件
      javaMailSender.send(mimeMessage);
  }


  /**
   * 发送邮件
   * @param mailProperty
   * @throws MessagingException
   */
  public static void sendEmail(MailProperty mailProperty) throws MessagingException {
    // 创建信息对象
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    mimeMessage.addHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");

    // 设置thymeleaf模板的值
    Context context = new Context();

    Set<Map.Entry<String, Object>> entries = mailProperty.getData().entrySet();

    for(Map.Entry<String, Object> e: entries){
      context.setVariable(e.getKey(), e.getValue());
    }
    String emailFilePath = mailProperty.getEmailFilePath();

    String emailContent = templateEngine.process(emailFilePath, context);

    // 设置信息
    setMessage(mimeMessage, emailContent, mailProperty);

  }
}
