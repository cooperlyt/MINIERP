package com.dgsoft.common.jbpm;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.xpath.DefaultXPath;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.Token;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: cooper
 * Date: 8/24/11
 * Time: 1:16 PM
 */
public abstract class TokenImg{

    @In
    ResourceBundle resourceBundle;
    @Logger
    protected Log log;

    private byte[] getGpdBytes() {
        if (getProcessDefinition().getFileDefinition() == null){
            return null;
        }
        return getProcessDefinition().getFileDefinition().getBytes("gpd.xml");
    }

    private byte[] getProcessDefinitionImageBytes() {
        if (getProcessDefinition().getFileDefinition() == null){
            return null;
        }
        return getProcessDefinition().getFileDefinition().getBytes("processimage.jpg");
    }

    protected abstract ProcessDefinition getProcessDefinition();

    /**
     * 生成流程定义图
     *
     *
     * @return
     */
    private BufferedImage getProcessDefinitionImage() {
        byte[] bytes = getProcessDefinitionImageBytes();
        if (bytes == null) {
            return null;
        }
        // 图型缓存
        BufferedImage bi = null;
        // 原始图型
        Image img = null;
        // MediaTracker 类是一个跟踪多种媒体对象状态的实用工具类。
        // 媒体对象可以包括音频剪辑和图像，但目前仅支持图像。
        MediaTracker tracker = null;
        // 图像默认宽度
        int w = 400;
        // 图像默认高度
        int h = 400;

        if (bytes == null) {
            bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            img = Toolkit.getDefaultToolkit().createImage(bytes);
            // 接收Image信息通知的异步更新接口
            Container p = new Container();
            tracker = new MediaTracker(p);
            tracker.addImage(img, 0);
            try {
                // 至关重要:没有它,图片的高宽无法得到
                // 它将等待图像信息加载完毕
                tracker.waitForAll();
                // tracker.waitForID(0);
                w = img.getWidth(p);
                h = img.getHeight(p);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage(), e);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
            bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        }

        Graphics2D g = bi.createGraphics();
        // 如果不设背景色,默认为黑色
        g.setBackground(Color.WHITE);
        // 留1个宽度的边
        g.clearRect(1, 1, w - 2, h - 2);
        if (tracker != null && MediaTracker.COMPLETE == tracker.statusAll(false)) {
            g.drawImage(img, 0, 0, null);
        } else {
            return null;
        }
        return bi;
    }


    protected byte[] bufferedImageToByteArray(BufferedImage img) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        encoder.encode(img);
        os.flush();
        return os.toByteArray();
    }

    // 图像参数
    final static Color RunningColor = new Color(0, 0, 255);
    final static Color RunningBackColor = new Color(153, 153, 255);//#9999FF
    final static Color SuspenedColor = new Color(170, 102, 0); //#AA6600
    final static Color SuspenedBackColor = new Color(255, 204, 153);
    final static Color LockedColor = new Color(170, 102, 0); //#AA6600
    final static Color LockedBackColor = new Color(255, 204, 153);//#FFAA99
    final static Color EndedColor = new Color(204, 0, 0); //#CC0000
    final static Color EndedBackColor = new Color(102, 0, 0);//#66000
    final static int BorderWidth = 1;
    final static int BoderBackWidth = 2;
    final static int FontSize = 10;
    final static int TitleHeight = 12;

    protected abstract Token getCurrentToken();

    protected abstract boolean drawChild();

    private void drawToken(BufferedImage img, Token token, Element root) {
        int[] boxConstraint = extractBoxConstraint(root, token);
        int x = boxConstraint[0] - BorderWidth * 2;
        int y = boxConstraint[1] - TitleHeight - BorderWidth * 2;
        int w = boxConstraint[2] + BorderWidth * 2;
        int h = boxConstraint[3] + TitleHeight + BorderWidth * 2;

        String title = "";
        Font font = new Font(resourceBundle.getString("token_title_font"), Font.PLAIN, FontSize);
        Stroke stroke = new BasicStroke(BorderWidth);
        Color color = null;
        Color bColor = null;
        if (token.isSuspended()) {
            title = resourceBundle.getString("suspended");
            color = SuspenedColor;
            bColor = SuspenedBackColor;
        } else if (token.isLocked()) {
            title = resourceBundle.getString("locked");
            color = LockedColor;
            bColor = LockedBackColor;
        } else if (token.hasEnded()) {
            title = resourceBundle.getString("ended");
            color = EndedColor;
            bColor = EndedBackColor;
        } else {
            title = resourceBundle.getString("running");
            color = RunningColor;
            bColor = RunningBackColor;
        }
        Graphics2D g = img.createGraphics();
        g.setFont(font);
        g.setColor(color);
        g.setStroke(stroke);
        // 画边框
        g.drawRect(x, y, w, h);
        // 画Title
        g.fillRect(x, y, w, TitleHeight + BorderWidth);
        g.setColor(bColor);
        // 画竖阴影
        g.fillRect(x + w + BorderWidth - BorderWidth / 2, y + BoderBackWidth, BoderBackWidth, h + BorderWidth - BorderWidth / 2);
        // 画横阴影
        g.fillRect(x + BoderBackWidth, y + h + BorderWidth - BorderWidth / 2, w + BorderWidth - BorderWidth / 2, BoderBackWidth);

        g.setColor(Color.WHITE);
        if (token.getName() != null) {
            title += " \"" + token.getName() + "\"";
        }
        // 字符串要离开边框3个宽度.
        g.drawString(title, x + BorderWidth + 3, y + TitleHeight / 2 + FontSize / 2);
    }


    public byte[] getImage() {
        BufferedImage bi = getProcessDefinitionImage();
        if (bi != null) {
            try {
                Element rootDiagramElement = DocumentHelper.parseText(new String(getGpdBytes())).getRootElement();
                drawToken(bi, getCurrentToken(), rootDiagramElement);
                if (drawChild()) {
                    boolean allEnded = true;
                    Collection<Token> childrenToken = getCurrentToken().getChildren().values();
                    for (Token token : childrenToken) {
                        if (!token.hasEnded()) {
                            allEnded = false;
                            break;
                        }
                    }
                    if (!allEnded) {
                        for (Token token : childrenToken) {
                            drawToken(bi, token, rootDiagramElement);
                        }
                    }
                }
                return bufferedImageToByteArray(bi);
            } catch (DocumentException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 提取座标及宽高
     *
     * @param root  根节点
     * @param token
     * @return
     */
    private int[] extractBoxConstraint(Element root, Token token) {
        int[] result = new int[4];
        String nodeName = token.getNode().getName();
        XPath xPath = new DefaultXPath("//node[@name='" + nodeName + "']");
        Element node = (Element) xPath.selectSingleNode(root);

        result[0] = Integer.valueOf(node.attribute("x").getValue()).intValue();
        result[1] = Integer.valueOf(node.attribute("y").getValue()).intValue();

        result[2] = Integer.valueOf(node.attribute("width").getValue())
                .intValue();
        result[3] = Integer.valueOf(node.attribute("height").getValue())
                .intValue();
        return result;
    }

}
