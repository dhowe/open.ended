package text3d;

import java.util.*;

import render.*;

public class Line3D extends Geometry
{    
  private Font3D font;
  private String text;
  private double yOffset;  //???
  private int numChars;   // remove
  private double fontSize;// remove!!
  private RenderableContext renderer;
  Glyph3D[] glyphs;
 
  public Line3D(Font3D f, String text) {
    this(f, text, f.getSize());
  }

  public Line3D(Font3D f, String text, double fontSize)
  {
    Glyph3D glyph = null;
    this.font = f;
    this.text = text;
    this.fontSize = fontSize;
    this.numChars = text.length();
    this.renderer = f.getRenderer();
    List tmp = new ArrayList();
    for (int i = 0; i < numChars; i++) {
      add(glyph = f.getGlyph(text.charAt(i))); 
      tmp.add(glyph);
    }
    init(f, tmp);
  }
  
  private void init(Font3D f, List glyphList) {
    this.glyphs = (Glyph3D[])glyphList.toArray(GLYPHS);    
    double xOff = (numChars * f.getKerning() * fontSize)/-2.4; //FIX!!
    renderer.push();
      // rotate right-side up
      renderer.rotateX(Math.PI);
      // center line on X & scale
      renderer.translate(xOff, 0, 0);
      renderer.scale(fontSize, fontSize, fontSize);
      for (int i = 0; i < text.length(); i++) 
      {
        Glyph3D g = (Glyph3D)child(i);
        /*if (g.isSpace()) {
          renderer.translate(Font3D.SPACE_WIDTH, 0,0);
          continue;
        }
        else*/ if (g.isPunct()) {
          renderer.translate(Font3D.PUNCT_WIDTH, 0,0);
        }     
        renderer.push();  
          // move right w' each letter
          renderer.translate((i*f.getKerning()), 0, 0);         
          renderer.transform(g);
        renderer.pop();
      }
    renderer.pop();
  }  
  
  public String toString() {
    String s = "[";
    for (int i = 0; i < glyphs.length; i++) 
      s += glyphs[i].letter/*+"("+glyphs[i].getChildren()+")"*/;
    return s + "]";
  } 
  
  void growGlyphArray(Font3D f) {
    //System.out.println("Line3D.dimGlyphArray()");
    List tmp = new ArrayList();
    for (int i = 0; i < glyphs.length; i++) 
      tmp.add(glyphs[i]);
    Glyph3D g = f.getGlyph('X');
    g.setMaterial(glyphs[glyphs.length-1].material);
    tmp.add(g);
    numChars = tmp.size();
    //init(f, tmp);
    this.glyphs = (Glyph3D[])tmp.toArray(GLYPHS);    
  }
  
  public void setGlyph(
    Font3D f, int lineIdx, int numLines, char c, int cIdx) 
  { 
    System.out.println("setGlyph("+lineIdx+","+numLines+","+c+","+cIdx+")");    
    Glyph3D g = f.getGlyph(c);     
    while (cIdx >= glyphs.length)
      growGlyphArray(f);    
    Glyph3D old = getGlyph(cIdx);
    if (old.visibleMat == null)
      old.visibleMat = Glyph3D.DEFAULT_MATERIAL;
    g.setMaterial(old.visibleMat); // this should work!!!  
    g.setMaterial(TextCubeGL.textMat[0]);    
    g.hidden = false;
    glyphs[cIdx] = g;
    old = null;
    refresh(cIdx, g, lineIdx, numLines);
  }
  
  public void refresh(int charIdx, Glyph3D g, int lineIdx, int numLines) 
  {
    double lineHeight = (font.getLeading()*fontSize*.935);
    double leading = lineHeight * (lineIdx-1);
    switch (numLines) { // hack
      case 1: break;
      case 2: leading += lineHeight/2d; break;
      case 3: break;
      case 4: break;
      case 5: break;
    }   
    renderer.push();
      renderer.rotateX(Math.PI);
      renderer.translate(0, leading, 0);    
      renderer.scale(fontSize, fontSize, fontSize);
      if (g.isPunct())
        renderer.translate(Font3D.PUNCT_WIDTH, 0,0);
      renderer.translate((charIdx*font.getKerning()-5.4/*?????*/), 0, 0);         
      renderer.transform(g);
    renderer.pop();
    add(g);
  }
 
  public Glyph3D getGlyph(int i) {
    return glyphs[i]; 
  }  
  public void setYOffset(double offset) {
    this.yOffset = offset;
  }
  public int size() {
    return this.glyphs.length;
  }
  private static final Glyph3D[] GLYPHS = new Glyph3D[0];

} // end