package com.ypq;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * ��װ����tess4j,��Ҫ����Tesseract��ʶ����֤��
 * @author god
 *
 */
public class Recognition {

	/**
	 * ��װ����ʶ��ͼƬ,��ͼƬ��ֵ����ʶ��
	 * @param imgPath
	 * @return
	 * @throws IOException
	 * @throws TesseractException
	 */
	public static String recognize(String imgPath) throws IOException, TesseractException {
		BufferedImage bi = ImageIO.read(new File(imgPath));
		if(bi == null)
			return null;		//��ʧ��
		
		int h = bi.getHeight();
		int w = bi.getWidth();
		int[][] gray = new int[w][h];
		//��ͼƬת���ɻҶ�ֵ�洢��gray[][]������
		for(int x = 0; x < w; x++)
			for(int y = 0; y < h; y++) {
				gray[x][y] = getGray(bi.getRGB(x, y));
			}
		//�½�һ����������Ŷ�ֵ�����ͼƬnbi
		BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		for(int x = 0; x < w; x++)
			for(int y = 0; y < h; y++) {
				if(getAverageGray(gray, x, y, w, h) > threshold) {
					int white = new Color(255, 255, 255).getRGB();
					nbi.setRGB(x, y, white);
				}
				else {
					int black = new Color(0, 0, 0).getRGB();
					nbi.setRGB(x, y, black);
				}
			}
		ImageIO.write(nbi, "jpg", new File("aaa.jpg"));
		
		//��ʼʶ��
		Tesseract instance = Tesseract.getInstance();
		instance.setTessVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");		//���������,ֻʶ������
		instance.setPageSegMode(7);											//����ֻ��һ���ı�
		String result=instance.doOCR(nbi);
		result = result.replaceAll(" ", "");								//ȥ��ʶ������Ŀո���߻���
		result = result.replaceAll("\n", "");
		
		return result;
	}
	
	/**
	 * ����ͼƬ��rgbֵȡ����֮�͵�ƽ��ֵ,�õ��Ҷ�ֵ
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
	 * ĳһ��ĻҶ�ȡ��Χ8���������Ҷȵ�ƽ��ֵ
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
							+ (y >= h -1 ? 255 : gray[x][y + 1])
							+ (x <= 0 || y >= h -1 ? 255 : gray[x - 1][y + 1]);
		return sum / 9;
	}
	
	public static final int threshold = 150;		//���ö�ֵ������ֵ
}
