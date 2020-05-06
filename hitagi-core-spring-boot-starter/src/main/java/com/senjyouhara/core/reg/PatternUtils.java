package com.senjyouhara.core.reg;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
  private PatternUtils(){}
  /**
   * 	检测返回所有匹配项
   * @param str	匹配字符
   * @param regex 匹配正则
   * @return	匹配返回列表
   */
  public static List<String> matcher(String str, String regex){
    Matcher matcher = Pattern.compile(regex).matcher(str);
    ArrayList<String> strings = new ArrayList<>();
    while(matcher.find()){
      strings.add(matcher.group());
    }
    return strings;
  }

  /**
   * 是否有匹配项
   * @param str	匹配字符串
   * @param regex 匹配正则
   * @return
   */
  public static boolean hasValidate(String str,String regex){
    Matcher matcher = Pattern.compile(regex).matcher(str);
    return matcher.matches();
  }

}
