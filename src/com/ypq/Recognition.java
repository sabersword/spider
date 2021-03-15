package com.ypq;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 封装过的tess4j,主要利用Tesseract来识别验证码
 *
 * @author god
 */
public class Recognition {

    /**
     * 封装过的识别图片,将图片二值化后识别
     *
     * @param imgPath
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    public static String recognize(String imgPath) throws IOException, TesseractException {
        BufferedImage bi = ImageIO.read(new File(imgPath));
        if (bi == null) {
            return null;        //打开失败
        }
        int h = bi.getHeight();
        int w = bi.getWidth();
        int[][] gray = new int[w][h];
        //将图片转换成灰度值存储在gray[][]数组中
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y] = getGray(bi.getRGB(x, y));
            }
        }
        //新建一个缓冲区存放二值化后的图片nbi
        BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (getAverageGray(gray, x, y, w, h) > THRESHOLD) {
                    int white = new Color(255, 255, 255).getRGB();
                    nbi.setRGB(x, y, white);
                } else {
                    int black = new Color(0, 0, 0).getRGB();
                    nbi.setRGB(x, y, black);
                }
            }
        }
        ImageIO.write(nbi, "jpg", new File("aaa.jpg"));

        //开始识别
        Tesseract instance = Tesseract.getInstance();
        instance.setTessVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");        //加入白名单,只识别数字
        instance.setPageSegMode(7);                                            //假设只有一行文本
        String result = instance.doOCR(nbi);
        result = result.replaceAll(" ", "");                                //去除识别出来的空格或者换行
        result = result.replaceAll("\n", "");

        return result;
    }

    /**
     * 根据图片的rgb值取三者之和的平均值,得到灰度值
     *
     * @param rgb
     * @return (r + g + b) / 3
     */
    private static int getGray(int rgb) {
        Color c = new Color(rgb);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return (r + g + b) / 3;
    }

    /**
     * 某一点的灰度取周围8个点加自身灰度的平均值
     *
     * @param gray
     * @param x
     * @param y
     * @param w
     * @param h
     * @return sum / 9
     */
    private static int getAverageGray(int[][] gray, int x, int y, int w, int h) {
        int sum = gray[x][y]
                + (x <= 0 ? 255 : gray[x - 1][y])
                + (x <= 0 || y <= 0 ? 255 : gray[x - 1][y - 1])
                + (y <= 0 ? 255 : gray[x][y - 1])
                + (x >= w - 1 || y <= 0 ? 255 : gray[x + 1][y - 1])
                + (x >= w - 1 ? 255 : gray[x + 1][y])
                + (x >= w - 1 || y >= h - 1 ? 255 : gray[x + 1][y + 1])
                + (y >= h - 1 ? 255 : gray[x][y + 1])
                + (x <= 0 || y >= h - 1 ? 255 : gray[x - 1][y + 1]);
        return sum / 9;
    }

    public static final int THRESHOLD = 150;        //设置二值化的阈值
}
