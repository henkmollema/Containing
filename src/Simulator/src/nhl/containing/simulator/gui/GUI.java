package nhl.containing.simulator.gui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.game.Container;

import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.lwjgl.opengl.Display;

/**
 *
 * @author sietse
 */
public class GUI extends Behaviour{
    
    public static final int DEFAULT_TEXT_SIZE = 170;
    
    // Singleton
    private static GUI m_instance;
    public static GUI instance() {
        return m_instance;
    }
    
    // Items
    private Picture m_QRCode;
    private boolean visible = false;
    
    private Picture m_logo;
    private BitmapText m_worldInfo;
    private BitmapText m_containerInfo; // print con
    // print container details
    
    public static Node root() {
        return Main.guiRoot();
    }
    public static Vector2f screenSize() {
        return new Vector2f(screenWidth(), screenHeight());
    }
    public static float screenHeight() {
        return Display.getHeight();
    }
    public static float screenWidth() {
        return Display.getWidth();
    }
    
    @Override
    public void awake() {
        m_instance = this;
    }
    @Override
    public void start() {
        /** Write text on the screen (HUD) */
        int width = 365;
        int height = 250;
        Picture pic = new Picture("background");
        Material mat = new Material(Main.assets(), "Common/MatDefs/Misc/Unshaded.j3md");
        ColorRGBA colour = ColorRGBA.Black;
        mat.setColor("Color", colour.set(colour.r, colour.g, colour.b, 0.5f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.setTransparent(true);
        pic.setMaterial(mat);
        pic.setWidth(width);
        pic.setHeight(height);
        pic.setPosition(screenWidth() - width, screenHeight() - height);
        Main.guiRoot().attachChild(pic);
        Main.guiFont(Main.assets().loadFont("Interface/Fonts/Cousine.fnt"));
        Main.guiFont().getCharSet().setRenderedSize(DEFAULT_TEXT_SIZE);
        m_worldInfo = new BitmapText(Main.guiFont(), false);
        m_worldInfo.setLocalTranslation(screenWidth() - (width - 5), (screenHeight() - 25) + m_worldInfo.getHeight(), 0);
        m_containerInfo = new BitmapText(Main.guiFont(), false);
        m_containerInfo.setLocalTranslation(screenWidth() - (width - 5), (screenHeight() - 100) + m_containerInfo.getHeight(), 0);
        Main.guiRoot().attachChild(m_worldInfo);
        Main.guiRoot().attachChild(m_containerInfo);
    }
    /**
     * prints the given text on the screen
     * @param text the text to be printed
     */
    public void setWorldText(String text)
    {
        m_worldInfo.setText(text);
    }
    /**
     * prints the given text on the screen
     * @param text the text to be printed
     */
    public void setContainerText(String text)
    {
        m_containerInfo.setText(text);
    }
    /**
     * prints the data from the on the screen
     * @param container the container of which to set the data
     */
    public void setContainerInfo(Container container)
    {
        m_containerInfo.setText(container.getRFID().getData());
    }
    @Override
    public void rawUpdate() {
    }
    /**
     * Shows the QR code
     */
    public void showQR(){
        if(m_QRCode == null)
            return;
        if(visible)
            setQRHide();
        else
            setQRVisible();
    }
    
    /**
     * Sets the QR code visible
     */
    private void setQRVisible(){
        Main.guiRoot().attachChild(m_QRCode);
        visible = true;
    }
    
    /**
     * Hides the QR code
     */
    private void setQRHide(){
        Main.guiRoot().detachChild(m_QRCode);
        visible = false;
    }
    
    /**
     * Creates the QR - code from a string
     * @param input input string
     */
    public void makeQR(String input){
        if(input == null)
            return;
        int size = 300;
        BufferedImage image = null;
        try{
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(input, BarcodeFormat.QR_CODE, size, size,hintMap);
            image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D)image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrix.getWidth(), matrix.getHeight());
            graphics.setColor(Color.BLACK);
            for(int i = 0; i < matrix.getWidth(); i++){
                for(int j = 0; j < matrix.getWidth(); j++){
                    if(matrix.get(i, j)){
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
        writeQR(image);
    }
    
    /**
     * Writes the QR code to an picture object
     * @param b buffered image
     */
    private void writeQR(BufferedImage b){
        Texture2D tex = new Texture2D(300, 300, Image.Format.RGB8);
        AWTLoader loader = new AWTLoader();
        Image image = null;
        image = loader.load(b, true);
        tex.setImage(image);
        m_QRCode = new Picture("QR");
        m_QRCode.setTexture(Main.assets(), tex, false);
        m_QRCode.setHeight(300);
        m_QRCode.setWidth(300);
        m_QRCode.setPosition(screenWidth() /2 - 200, screenHeight() /2 - 200);
    }
}
