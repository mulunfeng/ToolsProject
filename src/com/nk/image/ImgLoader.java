package com.nk.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by zhangyuyang1 on 2016/11/8.
 */
public class ImgLoader {

    // 改变成十六进制码
    public static String[][] getPX(String args) {
        int[] rgb = new int[3];

        File file = new File(args);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int width = bi.getWidth();
        int height = bi.getHeight();
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        String[][] list = new String[width][height];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                list[i][j] = rgb[0] + "," + rgb[1] + "," + rgb[2];

            }
        }
        return list;

    }

    public static void main(String[] args) {
        String[][] list1 = getPX("D:\\englishphoto4.jpg");
        System.out.println(list1[0][0]);
    }
}
