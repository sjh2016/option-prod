package com.waben.option.common.util;

import com.waben.option.common.model.vo.slider.SliderImageVO;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Random;

@Slf4j
public class SliderVerifyUtil {

    static int targetWidth = 55;//小图长
    static int targetHeight = 45;//小图宽
    static int circleR = 8;//半径
    static int r1 = 4;//距离点

    /**
     * @return int[][]
     * @throws
     * @Createdate: 2019年1月24日上午10:52:42
     * @Title: getBlockData
     * @Description: 生成小图轮廓
     * @author zhoujin
     */
    private static int[][] getBlockData() {
        int[][] data = new int[targetWidth][targetHeight];
        double x2 = targetWidth - circleR; //47

        //随机生成圆的位置
        double h1 = circleR + Math.random() * (targetWidth - 3 * circleR - r1);
        double po = Math.pow(circleR, 2); //64

        double xbegin = targetWidth - circleR - r1;
        double ybegin = targetHeight - circleR - r1;

        //圆的标准方程 (x-a)²+(y-b)²=r²,标识圆心（a,b）,半径为r的圆
        //计算需要的小图轮廓，用二维数组来表示，二维数组有两张值，0和1，其中0表示没有颜色，1有颜色
        for (int i = 0; i < targetWidth; i++) {
            for (int j = 0; j < targetHeight; j++) {
                double d2 = Math.pow(j - 2, 2) + Math.pow(i - h1, 2);
                double d3 = Math.pow(i - x2, 2) + Math.pow(j - h1, 2);
                if ((j <= ybegin && d2 < po) || (i >= xbegin && d3 > po)) {
                    data[i][j] = 0;
                } else {
                    data[i][j] = 1;
                }
            }
        }
        return data;
    }

    /**
     * @param oriImage      原图
     * @param targetImage   抠图拼图
     * @param templateImage 颜色
     * @param x
     * @param y             void
     * @throws
     * @Title: cutByTemplate
     * @Description: 有这个轮廓后就可以依据这个二维数组的值来判定抠图并在原图上抠图位置处加阴影,
     * @author zhoujin
     */
    private static void cutByTemplate(BufferedImage oriImage, BufferedImage targetImage, int[][] templateImage, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        //创建shape区域
        for (int i = 0; i < targetWidth; i++) {
            for (int j = 0; j < targetHeight; j++) {
                int rgb = templateImage[i][j];
                // 原图中对应位置变色处理
                int rgb_ori = oriImage.getRGB(x + i, y + j);

                if (rgb == 1) {
                    targetImage.setRGB(i, j, rgb_ori);

                    //抠图区域高斯模糊
                    readPixel(oriImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
                } else {
                    //这里把背景设为透明
                    targetImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }
        }
    }


    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; i++)
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;

                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);

            }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }

    /**
     * @return Map<String, Object>  返回生成的抠图和带抠图阴影的大图 base64码及抠图坐标
     * @Description: 读取本地图片，生成拼图验证码
     * @author zhoujin
     */
    public static SliderImageVO createImage(File file) {
        SliderImageVO imageVO = new SliderImageVO();
        try {
            BufferedImage oriImage = ImageIO.read(file);
            Random random = new Random();
            //X轴距离右端targetWidth  Y轴距离底部targetHeight以上
            int widthRandom = random.nextInt(oriImage.getWidth() - 2 * targetWidth) + targetWidth;
            int heightRandom = random.nextInt(oriImage.getHeight() - targetHeight);
            log.info("原图大小{} x {},随机生成的坐标 X,Y 为（{}，{}）", oriImage.getWidth(), oriImage.getHeight(), widthRandom, heightRandom);

            BufferedImage targetImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_4BYTE_ABGR);
            cutByTemplate(oriImage, targetImage, getBlockData(), widthRandom, heightRandom);

            imageVO.setBigImage(getImageBASE64(oriImage));
            imageVO.setSmallImage(getImageBASE64(targetImage));
            imageVO.setXWidth(widthRandom);
            imageVO.setYHeight(heightRandom);
        } catch (Exception e) {
            log.info("创建图形验证码异常", e);
        } finally {
            return imageVO;
        }
    }

    /**
     * @param image
     * @return
     * @Title: getImageBASE64
     * @Description: 图片转BASE64
     * @author zhoujin
     */
    public static String getImageBASE64(BufferedImage image) throws IOException {
        byte[] imagedata = null;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bao);
        imagedata = bao.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        String BASE64IMAGE = encoder.encodeBuffer(imagedata).trim();
        BASE64IMAGE = BASE64IMAGE.replaceAll("\r|\n", "");  //删除 \r\n
        return BASE64IMAGE;
    }

    public static File getFile(String url) {
        //对本地文件命名
        File file = null;

        URL urlFile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            file = File.createTempFile("net_url", ".jpg");
            //下载
            urlFile = new URL(url);
            inStream = urlFile.openStream();
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != inStream) {
                    inStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
