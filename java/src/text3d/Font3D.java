package text3d;

import render.*;

public final class Font3D
{
  private static final double KERNING = 2.2;
  private static final double LEADING = 1.5;
  private static final double FONT_SZ = 1.;
  static final double SPACE_WIDTH = -1.4;
  static final double PUNCT_WIDTH = -.9;
 
  private RenderableContext renderer;
  private double size, leading, kerning;  
  
  public Font3D(RenderableContext renderer) {
    this(renderer, FONT_SZ, LEADING);
  }
  
  public Font3D(RenderableContext renderer, double size) {
    this(renderer, size, LEADING);
  }
  
  public Font3D(RenderableContext renderer, double size, double leading) {
    this.size = size;
    this.leading = leading;
    this.renderer = renderer;
    this.kerning = KERNING;
  }
  
  static double letterData[][][] = {
    {{'A'}, {.4,.6},{.3,.7},{.2,.4,.6,.8},{.1,.9},{0,.2,.8,1}},
    {{'B'}, {0,.8},{0,.2,.8,1},{0,.8},{0,.2,.8,1},{0,.8}},
    {{'C'}, {.2,.8},{0,.2,.8,1},{0,.2},{0,.2,.8,1},{.2,.8}},
    {{'D'}, {0,.8},{0,.2,.8,1},{0,.2,.8,1},{0,.2,.8,1},{0,.8}},
    {{'E'}, {0,1},{0,.2,},{0,.8,},{0,.2,},{0,1}},
    {{'F'}, {0,1},{0,.2,},{0,.8,},{0,.2,},{0,.2}},
    {{'G'}, {.2,1},{0,.2,},{0,.2,.6,1},{0,.2,.8,1},{.2,.8}},
    {{'H'}, {0,.2,.8,1},{0,.2,.8,1},{0,1},{0,.2,.8,1},{0,.2,.8,1}},
    {{'I'}, {.2,.8},{.4,.6},{.4,.6},{.4,.6},{.2,.8}},
    {{'J'}, {.8,1},{.8,1},{.8,1},{0,.2,.8,1},{.2,.8}},
    {{'K'}, {0,.2,.8,1},{0,.2,.6,.8},{0,.6},{0,.2,.6,.8},{0,.2,.8,1}},
    {{'L'}, {0,.2},{0,.2,},{0,.2,},{0,.2,},{0,1}},
    {{'M'}, {0,.2,.8,1},{0,.4,.6,1},{0,.2,.4,.6,.8,1},{0,.2,.8,1},{0,.2,.8,1}},
    {{'N'}, {0,.2,.8,1},{0,.4,.8,1},{0,.2,.4,.6,.8,1},{0,.2,.6,1},{0,.2,.8,1}},
    {{'O'}, {.2,.8},{0,.2,.8,1},{0,.2,.8,1},{0,.2,.8,1},{.2,.8}},
    {{'P'}, {0,.8},{0,.2,.8,1},{0,.8},{0,.2},{0,.2}},
    {{'Q'}, {.2,.8},{0,.2,.8,1},{0,.2,.8,1},{0,.2,.6,.8},{.2,.6,.8,1}},
    {{'R'}, {0,.8},{0,.2,.8,1},{0,.8},{0,.2,.6,.8},{0,.2,.8,1}},
    {{'S'}, {.2,.9},{0,.2},{.2,.7},{.7,.9},{.2,.7}},
    {{'T'}, {0,1},{.4,.6},{.4,.6},{.4,.6},{.4,.6}},
    {{'U'}, {0,.2,.8,1},{0,.2,.8,1},{0,.2,.8,1},{0,.2,.8,1},{.2,.8}},
    {{'V'}, {0,.2,.8,1},{.1,.3,.7,.9},{.2,.4,.6,.8},{.3,.7},{.4,.6}},
    {{'W'}, {0,.2,.8,1},{0,.2,.8,1},{0,.2,.4,.6,.8,1},{0,.4,.6,1},{0,.2,.8,1}},
    {{'X'}, {0,.2,.8,1},{.2,.4,.6,.8},{.4,.6},{.2,.4,.6,.8},{0,.2,.8,1}},
    {{'Y'}, {0,.2,.8,1},{.2,.4,.6,.8},{.4,.6},{.4,.6},{.4,.6}},
    {{'Z'}, {0,1},{.6,.8},{.4,.6},{.2,.4},{0,1}},
    {{'#'}, {.2,.3,.7,.8},{0,1},{.2,.3,.7,.8},{0,1},{.2,.3,.7,.8}}, 
    {{')'}, {.6,.8},{.7,.9},{.75,.95},{.7,.9},{.6,.8}},
    {{'('}, {.6,  .8},{.5,.7,},{.45,.65},{.5,.7,},{.6, .8}},
    {{','}, {},{},{},{.5,.7},{.4,.6}},
    {{'.'}, {},{},{},{},{.4,.6}},
    {{' '}, {},{},{},{},{}},
    {{':'}, {},{.4,.6},{},{.4,.6},{}},
    {{'"'}, {.25,.4,.6,.75},{.25,.4,.6,.75},{},{},{}},
    {{'\''},{.45,.6},{.45,.6},{},{},{}},
    {{'!'}, {.5,.7},{.5,.7},{.5,.7},{},{.5,.7}},
    {{'|'}, {.5,.7},{.5,.7},{.5,.7},{.5,.7},{.5,.7}},
    {{'?'}, {.2,.8},{.8,1},{.45,.8},{},{.4,.6}},
    {{'-'}, {},{},{.1,.9},{},{}}, 
    {{'0'}, {.25,.75},{0.15,.25,.75,.85},{0.15,.25,.75,.85},{0.15,.25,.75,.85},{.25,.75}},
    {{'1'}, {.5,.7},{.3,.45,.5,.7},{.5,.7},{.5,.7},{.4,.8}},
    {{'2'}, {.2,.8},{.7,.9},{.55,.75},{.35,.55},{.2,.9}},
    {{'3'}, {.3,.85},{.8,1},{.4,.85},{.8,1},{.3,.85}},
    {{'4'}, {.5,.7},{.25,.4,.5,.7},{.1,.85},{.5,.7},{.5,.7}},
    {{'5'}, {.3,.85},{.25,.4},{.3,.8},{.75,.9},{.3,.8}},
  };   
  
  public double[][] getShapeData(char c) {
    c = Character.toUpperCase(c);
    for (int i = 0; i < letterData.length; i++) {
       char tmp = (char)letterData[i][0][0];
       if (tmp == c) {       
         double[][] d = letterData[i];            
         return d;
       }
    }
    throw new RuntimeException("ERROR: no letter: "+c);
  }

  public Glyph3D getGlyph(char c) {
    c = Character.toUpperCase(c);
    for (int i = 0; i < letterData.length; i++) {
       char tmp = (char)letterData[i][0][0];
       if (tmp == c) {       
         double[][] d = letterData[i];            
         return new Glyph3D(renderer, c, d);
       }
    }
    throw new RuntimeException("ERROR: no letter: "+c);
  }
  
  public Line3D getLine3D(String s) {
    if (true) throw new RuntimeException("UNTESTED!");
    return new Line3D(this, s);
  }
  
  // getters/setters ------------------
  public RenderableContext getRenderer() {
    return renderer;
  }
  public double getSize() {
    return size;
  }
  public void setSize(double size) {
    this.size = size;
  }
  public double getKerning() {
    return kerning;
  }
  public void setKerning(double kerning) {
    this.kerning = kerning;
  }
  public double getLeading() {
    return leading;
  }
  public void setLeading(double leading)  {
    this.leading = leading;
  }
}// end