package com.senjyouhara.core.io;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

  /**
   * 转换BufferedImage 数据为byte数组
   *
   * @param bImage
   * Image对象
   * @param format
   * image格式字符串.如"gif","png"
   * @return byte数组
   */
  public static byte[] imageToBytes(BufferedImage bImage, String format) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      ImageIO.write(bImage, format, out);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toByteArray();
  }

  /**
   * 转换byte数组为Image
   *
   * @param bytes
   * @return Image
   */
  public static Image bytesToImage(byte[] bytes) {
    Image image = Toolkit.getDefaultToolkit().createImage(bytes);
    try {
      MediaTracker mt = new MediaTracker(new Label());
      mt.addImage(image, 0);
      mt.waitForAll();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return image;
  }
}