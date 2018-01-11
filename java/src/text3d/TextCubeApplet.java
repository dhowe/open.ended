package text3d;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import render.*;

public class TextCubeApplet extends RenderApplet
  implements MouseListener, MouseMotionListener, ChangeListener
{
  private static int W = 800, H = 700; // no full screen

  static String SOUND_URL = "http://rednoise.org/~dhowe/open/open.mp3";
  
  private static boolean DBUG_NO_ROTATE = false;
  private static boolean USE_SLIDERS = true;
  private static boolean USE_SOUND = false;
  private static boolean HIDE_INNER = false;
  
  static final int faceR = 11, faceG = 134, faceB = 11;
  static final int bgR   = 140, bgG   = 112, bgB  = 131; 
  static final int OUT=0, GOING_OUT=2, IN=1, GOING_IN=3; 
  static final double LERP_SPEED = 5, LG = 6, SM = 4.2;
  static final double FULL_OUT=(LG/SM)- .1;  // .038 
  static final double STEP_SZ = FULL_OUT/20d; 
  static final double[] TRANSPARENCY = {.5, 0};
  static final double FULL_IN = 1d;
  static final int STEPS = 4; // YUCK
  static double INNER_SCALE = 1;
  static int STATE = GOING_IN;
  static int MX, MY, _MX, _MY, STEP_NUM = 0;
  static double[] adj = new double[12];
  
  static Font3D font;
  //static Mp3Player mp3Player; // temporarily removed
  static TextCube[] cubes   = new TextCube[2];
  static Material[] textMat = new Material[2];
  static Material[] faceMat = new Material[2];
  static JSlider leftSlider, rightSlider;

  boolean cubesAligned, dragging; 
  boolean autoRotateInner = !HIDE_INNER, autoRotateOuter = true;
  double elapsed, startTime = Double.MAX_VALUE;
  double innerRotation, outerRotation, frames;
  double outerSinTime, innerSinTime, noise;
  int innerRotations, outerRotations;

  public void init() 
  { 
    super.init(); 
    
    if (USE_SLIDERS) {
      setLayout(null);
      Panel sliders = new Panel();
      initSliderPanel(sliders, this);      
      add(sliders);
    }
//    if (USE_SOUND) {
//      mp3Player = new Mp3Player();
//      mp3Player.stream(SOUND_URL);
//    }
  }
  
//  public void stop() 
//  {
//    mp3Player.close();    
//  }
  
  public void initialize() 
  {  
    startTime = getCurrentTime();
    
    // Cube3Ds
    font = new Font3D(this, 1.2, 3.5);
    cubes[IN] = new TextCube(font, INNER_TEXT, INNER_SWAPS);
    
    cubes[OUT] = new TextCube(font, OUTER_TEXT, OUTER_SWAPS);    

    // Setup
    setFL(28);
    setFOV(1);
    addLight(1,1,.2,1,1,1);
    setBgColor(bgR/255d,bgG/255d,bgB/255d);
    
    addMouseListener(this);
    addMouseMotionListener(this);
    
    // Material    
    for (int i = 0; i < faceMat.length; i++) {
      faceMat[i] = new Material(); 
      faceMat[i].setTransparency(TRANSPARENCY[i]);
      faceMat[i].setAmbient(0.261,0.428,0.717);
    }
    (textMat[IN] = new Material()).setAmbient(0.9, .75, 0.75);
    (textMat[OUT] = new Material()).setAmbient(0.75, .75, 0.75);
    
    for (int i = 0; i < cubes.length; i++) {
      cubes[i].setMaterial(faceMat[i]);
      cubes[i].setTextMaterial(textMat[i]);
      if (!HIDE_INNER || i==OUT)
        world.add(cubes[i]);
    }
  }
  
  public void animate(double time) 
  {
    outerSinTime = Math.sin(time*OUTER_ROT_SPEED); 
    innerSinTime = Math.sin(time*INNER_ROT_SPEED); 
    
    if (STATE == GOING_OUT) {     // WE ARE MOVING OUT
      if (INNER_SCALE < (FULL_OUT)) 
      {
         INNER_SCALE = (INNER_SCALE+STEP_SZ >= FULL_OUT)
           ? FULL_OUT : INNER_SCALE+STEP_SZ; 

         double[] cam = renderer.getCamera().getUnsafe();
         if (STEP_NUM == 0) {       // get our offsets
           for (int i = 0; i < 12; i++) {
             if (i%4==3) continue;
             if (i==0 || i==5 || i==10) 
               adj[i] = (cam[i]-1.)/STEPS;
             else adj[i] = cam[i]/STEPS;
           }          
         }
         else {                     // make adjustments
           for (int i = 0; i < 12; i++) {
             if (i%4==3) continue;
             cam[i]-= adj[i]; 
           }
         }
         STEP_NUM++;
      }
      else {                      // WE ARE SCALED OUT
        swapText();               // DO TEXT-SWAP
        STEP_NUM = 0;
        STATE = OUT;
      }
    }
    else if (STATE == GOING_IN) { // WE ARE MOVING IN
      if (INNER_SCALE > FULL_IN) {
        INNER_SCALE = (INNER_SCALE-STEP_SZ <= FULL_IN)
          ? FULL_IN : INNER_SCALE-STEP_SZ;
      }
      else {                      // WE ARE RESET
        resetText();
        STATE = IN; 
      }
    }
    if (!dragging) 
      noise = Noise.noise(((frames/10.)*.2)-.1);  
    
    push(); // INNER CUBE
      if (autoRotateInner) {
        innerRotation = innerSinTime*-INNER_ROT_MAX; 
        setInnerRotations(innerRotation);
      }
      rotateX(noise/2d);
      if (cubesAligned) 
        rotateY(-noise/2d);
      if (!DBUG_NO_ROTATE)
        rotateY(innerRotation);
      if (INNER_SCALE!=FULL_IN)
        scale(INNER_SCALE);
      scale(SZ[IN]);
      transform(cubes[IN]);
    pop();
    
    push(); // OUTER CUBE    
      if (autoRotateOuter) {
        outerRotation = outerSinTime*OUTER_ROT_MAX; 
        setOuterRotations(outerRotation);
      }
      rotateX(noise/2d);
      if (cubesAligned)
        rotateY(-noise/2d);
      if (!DBUG_NO_ROTATE) 
        rotateY(outerRotation);
      scale(SZ[OUT]);      
      transform(cubes[OUT]);
    pop();     
    frames++;
  }
  
  private void resetText()
  {
    cubes[OUT].resetText();
    cubes[IN].resetText();
  }

  // MOUSE-ACTIONS  ==================================================
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2)  {
      if (!cubesAligned) {      
        autoRotateInner = false;
        autoRotateOuter = false;
        if (USE_SLIDERS) {
          leftSlider.setValue((innerRotations+1)*90);
          rightSlider.setValue((outerRotations+1)*90);
        }
        else {
          setValue(LEFT_SLIDER, (innerRotations+1)*90);
          setValue(RIGHT_SLIDER, (outerRotations+1)*90);
        }
        alignCubes();
      }
      else {
        autoRotateInner = true;
        unalignCubes();
        autoRotateOuter = true;
        if (USE_SLIDERS) {
          leftSlider.setValue(0);
          rightSlider.setValue(0);
        }
        else {
          setValue(LEFT_SLIDER, 0);
          setValue(RIGHT_SLIDER, 0);
        }
      }     
    }
  }
  
  public void mousePressed(MouseEvent e) {
    MX = e.getX(); MY = e.getY();
    super.mouseDown(null, e.getX(), e.getY());
  }
  
  public void mouseReleased(MouseEvent e) {
    dragging = false;
    MX = e.getX(); MY = e.getY();
    super.mouseUp(null, MX, MY);   
    _MX = MX; _MY = MY;
  }  

  public void mouseDragged(MouseEvent e) {
    if (cubesAligned) return;
    dragging = true;
    MX = e.getX(); MY = e.getY();
    if (!cubesAligned) { 
      theta += .03 * (MX - _MX);
      phi += .03 * (MY - _MY);
    }
    _MX = MX; _MY = MY;
  }

  public void mouseMoved(MouseEvent e) {
    MX = e.getX(); MY = e.getY();
    _MX = MX; _MY = MY;
  }
  
  public void mouseExited(MouseEvent e) {}
  
  public void mouseEntered(MouseEvent e) {}
  
  private void scale(double d) { scale(d,d,d); }
  
  public void drawOverlay(Graphics g) {
    g.setColor(SLATE); g.setFont(TITLE);
    int titleX = W-165, titleY = 30;
    g.drawString("open.ended", titleX, titleY);
    g.setColor(GRAY); g.setFont(SUBTITLE);
    g.drawString("drag to rotate",titleX+35,titleY+15);
  }
  
  void alignCubes() {
    if (STATE==IN || STATE==GOING_IN) 
      STATE = GOING_OUT;
    theta = phi = 0;
    cubesAligned = true;    
  } 
  
  void unalignCubes()  {
    cubesAligned = false;
    if (STATE == OUT) {
      STATE = GOING_IN;
      resetText();
    }
  } 
    
  int clip(int rotations) {
    while (rotations<0) rotations+=4; 
    while (rotations>3) rotations-=4; 
    return rotations;
  }
  
  void snapToInnerRotation(int value) {
    innerRotation = Math.toRadians(value); 
    innerRotations = (int)Math.round(innerRotation / (Math.PI/2d));
    innerRotations = clip(innerRotations);
  }
  
  void snapToOuterRotation(int value) {
    outerRotation = Math.toRadians(value); 
    outerRotations = (int)Math.round(outerRotation / (Math.PI/2d));
    outerRotations = clip(outerRotations); 
  }
  
  void setOuterRotations(double rotation) {
    outerRotations = (int)Math.round(rotation / (Math.PI/2d));
    outerRotations = clip(outerRotations);
  }
  
  void setInnerRotations(double rotation) {
    innerRotations = (int)Math.round(rotation / (Math.PI/2d));
    innerRotations = clip(innerRotations); 
  }
  
  JLabel leftDegrees, rightDegrees;
  private void initSliderPanel(Panel sliders, ChangeListener cl) 
  {   
    int sliderPanelH = 65;
    sliders.setBackground(BG);
    sliders.setLayout(null);
    sliders.setBounds(0, H-sliderPanelH, W, sliderPanelH);
     
    int leftStart = 100;
    JLabel leftLabel = new JLabel("inner");
    leftLabel.setFont(LABEL);
    leftLabel.setForeground(GRAY);
    leftLabel.setBounds(leftStart+3, -10, 85, sliderPanelH);
    sliders.add(leftLabel);
    
    leftDegrees = new JLabel(SLIDER_STR);
    leftDegrees.setFont(LABEL2);
    leftDegrees.setForeground(GRAY);
    leftDegrees.setBounds(leftStart+leftLabel.getWidth()-50,10,150,sliderPanelH);
    sliders.add(leftDegrees);
    
    leftSlider = new JSlider(SwingConstants.HORIZONTAL,0,360,0);
    leftSlider.setName(LEFT_SLIDER);
    leftSlider.addChangeListener(cl);
    leftSlider.setBackground(BG);
    leftSlider.setMajorTickSpacing(90);
    leftSlider.setPaintTicks(true);
    leftSlider.setLabelTable(leftSlider.createStandardLabels(90));
    leftSlider.setSnapToTicks(true);
    leftSlider.setBounds(leftLabel.getLocation().x+40, 0, 130, 25);
    sliders.add(leftSlider);
    
    int rightStart = W-300;
    JLabel rightLabel = new JLabel("outer");
    rightLabel.setFont(LABEL);
    rightLabel.setForeground(GRAY);
    rightLabel.setBounds(rightStart+3, -10, 85, sliderPanelH);
    sliders.add(rightLabel);  
    
    rightDegrees = new JLabel(SLIDER_STR);
    rightDegrees.setFont(LABEL2);
    rightDegrees.setForeground(GRAY);
    rightDegrees.setBounds(rightStart+rightLabel.getWidth()-50,10,150,sliderPanelH);
    sliders.add(rightDegrees);
        
    rightSlider = new JSlider(SwingConstants.HORIZONTAL,0,360,0);
    rightSlider.setName(RIGHT_SLIDER);
    rightSlider.addChangeListener(cl);
    rightSlider.setBackground(BG);
    rightSlider.setMajorTickSpacing(90);
    rightSlider.setPaintTicks(true);
    rightSlider.setLabelTable(rightSlider.createStandardLabels(90));
    rightSlider.setSnapToTicks(true);
    rightSlider.setBounds(rightLabel.getLocation().x+40, 0, 130, 25);;
    sliders.add(rightSlider);
  }
  
  public void setValue(String src, int val)
  {
    if (cubesAligned) unalignCubes();
    
    int value  = (val-90);            // offset for rest pos.    
    if (src.equals(RIGHT_SLIDER)) { 
      if (value > -90) {              // snapped to position 
        snapToOuterRotation(value);
        autoRotateOuter = false; 
        if (!autoRotateInner && !cubesAligned)
          alignCubes();
      }
      else {                          // auto-rotate mode  
        STATE = GOING_IN;
        freeRotate();
      }
    }
    if (src.equals(LEFT_SLIDER)) {
      if (value > -90) {              // snapped to position   
        snapToInnerRotation(value);
        autoRotateInner = false;
        if (!autoRotateOuter && !cubesAligned)
          alignCubes();
      }
      else {                          // auto-rotate mode 
        freeRotate();
      }
    }
  }
  
  public void stateChanged(ChangeEvent e)
  {
    //untouchedFrames = 0;
    if (cubesAligned) unalignCubes();
    
    JSlider js = (JSlider)e.getSource();
    int value  = (js.getValue()-90);   // offset for rest pos.
    String src = js.getName();
    if (src.equals(RIGHT_SLIDER)) { 
      if (js.getValueIsAdjusting()) {  // slider moving
        autoRotateOuter = false;
        outerRotation = Math.toRadians(value);
        setOuterRotations(outerRotation);
      }
      else {
        if (value > -90) {             // snapped to position 
          snapToOuterRotation(value);
          autoRotateOuter = false; 
          if (!autoRotateInner && !cubesAligned)
            alignCubes();
        }
        else {                         // auto-rotate mode  
          STATE = GOING_IN;
          freeRotate();
        }
      }
    }
    if (src.equals(LEFT_SLIDER)) {
      if (js.getValueIsAdjusting()) {  // slider moving
        autoRotateInner = false;
        innerRotation = Math.toRadians(value);
        setInnerRotations(innerRotation);
      }
      else {
        if (value > -90) {             // snapped to pos   
          snapToInnerRotation(value);
          autoRotateInner = false;
          if (!autoRotateOuter && !cubesAligned)
            alignCubes();
        }
        else {                         // auto-rotate mode 
          freeRotate();
        }
      }
    }
  }
 
  private void freeRotate()
  {
    autoRotateOuter = true;
    autoRotateInner = true;
    if (USE_SLIDERS) {
      leftSlider.setValue(0);
      rightSlider.setValue(0);
    }
    unalignCubes();
  }

  // Constants  ------------------------
  static final String LEFT_SLIDER = "IN_SLIDER";
  static final String RIGHT_SLIDER = "OUT_SLIDER";
  static final String SLIDER_STR = "free       90       270";
  static final Font  LABEL    = new Font("COURIER", Font.PLAIN, 11);
  static final Font  LABEL2   = new Font("COURIER", Font.PLAIN, 10);
  static final Font  TITLE    = new Font("COURIER", Font.BOLD, 16);
  static final Font  SUBTITLE = new Font("COURIER", Font.BOLD, 11);
  static final Color AQUA = new Color(205,255,106);
  static final Color DBLUE = new Color(48,71,118);
  static final Color GRAY = new Color(205,231,255);
  static final Color LBLUE = new Color(180,192,209);
  static final Color SLATE = new Color(155,174,218);
  static final Color BG = new Color(bgR, bgG, bgB);
  static final double INNER_ROT_SPEED = .07;
  static final double OUTER_ROT_SPEED = .15;
  static final int OUTER_ROT_MAX = 4;
  static final int INNER_ROT_MAX = 9;
  static final double[] SZ = {LG, SM};
  
  private void swapText()
  {
    cubes[OUT].swapText((outerRotations)%4, (innerRotations%4)+1);     
    cubes[IN].swapText((innerRotations)%4, (outerRotations%4)+1); 
  }
  
  static final String[][] OUTER_TEXT = new String[][]
  {
    {" GET", "EMOTIONALLY", "UNDRESSED"}, 
    {"COMPRESSING", "EVERY FACET", ""},
    {"EYES CLOSED", "   I AM", "ANYWHERE"}, 
    {"AN INSATIABLE ", "NEED TO ", "REPEAT    "},
  };
  
  static final String[][] INNER_TEXT = new String[][]
  {
    { " WE   ", "UNFOLD    " }, 
    { "THIS", "FANTASY" }, 
    { "  BREATHE", "SOFTLY" },
    { "AND ", "SURRENDER " },
  };                 
  
  static final String[][][] OUTER_SWAPS = new String[][][] 
  { 
    // { " GET", "EMOTIONALLY", "UNDRESSED" }
    {{"", " MOTION    ", "US       "}, {" GET", "", "UNDRESSED"},  
     {"A       ", "      IS", "  PRESSURE"}, {"AGAIN",  "EMOTIONAL  ", "" }},
     
    // { "COMPRESSING", "EVERY FACET", "" }
    {{"",  "EACH  FACET", "FANTASY    "}, {"COMING     ", "EACH       ", ""},
     {"","EVERY FACE ",""}, {"       SING","EVERY      ",""}},
     
    // { "EYES CLOSED", "   I AM", "ANYWHERE" }
    {{"", "   MAY WELL", "    HERE" }, {"     CLOSE ", "   ON A ", "" }, 
     {"EYES       ", " SO ", "    HERE" }, {"", "AGAIN  ", "" }},
                          
    // { "AN INSATIABLE ", "NEED TO ", "REPEAT " }   
    {{   "AN INSTINCT   ", "FEED    ", ""}, { "THIS  ", "NEED FOR", "" },
     { "   INSATIABLE ",   "  THAT  ", "REPEATS   " }, { "     A", "NEED    ", "" }}, 
  };
  
  static final String[][][] INNER_SWAPS = new String[][][] 
  { 
    // { " WE  ", "UNFOLD   " },        
    {{" WHEN",      "UNFOLDS   ", }, { "UNFOLDING ", "IN       ",},  
       { " WE   ",  "UNFOLD    ", },{ " WE  ", "REPEATEDLY" }}, 
     
    // { "THIS", "FANTASY" }
    {{"",  "THIS",  "FANTASY", ""}, {"TO HOLD", "FANTASY" },//CHANGE ME
       { "       WE","  IN",    "FANTASY", "" },
       {"  IS A",    "    TOUCH"}},
       
    // { "  BREATHE", "SOFTLY" },         
   {{"  BREATH ",  "SOFT",     }, {"   PRESS   ", "SOFTLY", },
    {"  BREATHE", "SOFTLY", },{ "  BREATH ", "SOFTLY",  }},
    
    // { "AND", "SURRENDER " },
    {{ "OPEN       ","    TO", "SURRENDER ",""},
      {"THROUGH",  "SURRENDER "},{"AND ", "SURROUNDED",},
    { "  NEW", "SURFACES  ", }}, 
  };
  
} // end