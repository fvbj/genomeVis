package geometry;

import dataModel.Record;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLTexture2D;
import transforms.Vec2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class TextureLabel {
    //Make alphabet
    public static OGLTexture2D createTexture(){
        int w = 512, h = 512;
        Font font = new Font("SansSerif", Font.PLAIN, 16);
        OGLRenderTarget rt =
                new OGLRenderTarget(w,h);
        OGLTextRenderer textR =
                new OGLTextRenderer(w,h, font);
        rt.bind();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics gr = image.createGraphics();
        gr.setColor(Color.WHITE);
        gr.setFont(font);
        int step = 64, count = 48;
        for(int x =step/2; x<w; x+=step )
            for(int y =step/2; y<h; y+=step ) {
                String s = "\"_"+ (char) count +"_\"";
                gr.drawString(s, x, y);
                Rectangle2D textBox = gr.getFontMetrics().getStringBounds(s, 0,
                        s.length(), gr);
                int x1 = x+(int)(-textBox.getX() -textBox.getWidth()/2);
                int y1 = y+(int)(-textBox.getY() -textBox.getHeight()/2);

                textR.addStr2D(x1,y1,s);
                count++;
                if (count==58) count =65;
                if (count==91) count =97;

            }
        textR.draw();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return rt.getColorTexture();
   }

    //create text
    public static OGLTexture2D createTextureFromCSVRecord(List<Record> dataRecords, Vec2D textTextureSize){
        int step = 64;
        int w = step * (int)textTextureSize.getY();
        int h = step * (int)textTextureSize.getX();
        Font font = new Font("SansSerif", Font.PLAIN, 16);
        OGLRenderTarget rt =
                new OGLRenderTarget(w,h);
        OGLTextRenderer textR =
                new OGLTextRenderer(w,h, font);
        rt.bind();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics gr = image.createGraphics();
        gr.setColor(Color.WHITE);
        gr.setFont(font);
        int count = 0;
        for(int x =step/2; x<w; x+=step )
            for(int y =step/2; y<h; y+=step ) {
                if (count < dataRecords.size()) {
                    String s = dataRecords.get(count).abr;
                    dataRecords.get(count).id = count;
                    gr.drawString(s, x, y);
                    Rectangle2D textBox = gr.getFontMetrics().getStringBounds(s, 0,
                            s.length(), gr);
                    int x1 = x + (int) (-textBox.getX() - textBox.getWidth() / 2);
                    int y1 = y + (int) (-textBox.getY() - textBox.getHeight() / 2);

                    textR.addStr2D(x1, y1, s);
                    count++;
                }


            }
        textR.draw();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return rt.getColorTexture();

    }
}
