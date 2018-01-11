package text3d;

import render.*;

public class Glyph3D extends Geometry 
{ 
  static double XSCALE=.1, YSCALE=.15, ZSCALE=.2;
  static final Material DEFAULT_MATERIAL = new Material();
  static final Material TRANSPARENT = new Material();
  static { 
    TRANSPARENT.setTransparency(1); 
    DEFAULT_MATERIAL.setAmbient(1,0,0);
  }

  char letter;
  int partCount;
  double[][] data;
  boolean hidden;
  Material visibleMat;
  RenderableContext renderer;
  
  public Glyph3D(RenderableContext renderer, char c, double[][] dIn) 
  { 
    super();      
    this.renderer = renderer;
    this.init(c, dIn);
  }
  
  private void init(char c, double[][] dIn)
  {
    child = null;
    this.letter = c;
    this.data = new double[dIn.length-1][];
    for (int j = 1; j < dIn.length; j++) {
       double[] dTmp = dIn[j];       
       data[j-1] = new double[dTmp.length];
       for (int k = 0; k < dTmp.length; k++) {
         data[j-1][k] = dTmp[k];
         if (k%2==0) partCount++;
       }      
    }
    int count = 0;
    Geometry[] g = new Geometry[partCount];
    for (int i = 0; i < data.length; i++)  {
      for (int j = 0; j < data[i].length; j+=2) 
      {   
        double sz  = data[i][j+1] - data[i][j];
        double avg = (data[i][j+1] + data[i][j])/2d;
        double trans = (avg-.5) * 2; // constant=2?
        g[count] = add().cube();   
        renderer.push();
          renderer.translate(trans, i/3d, 0);
          renderer.scale(sz, YSCALE, ZSCALE);         
          renderer.transform(g[count]); 
        renderer.pop();
        count++;
      }
    }
    renderer.push();
      renderer.rotateX(Math.PI);
      renderer.translate(0, -.7d/*(*fontsize)*/, 0);
      renderer.transform(this); 
    renderer.pop();
  }
  
  public Geometry setMaterial(Material m)
  {
    visibleMat = m;
    return super.setMaterial(m);
  }
  
  public void hide()
  {
    if (hidden) return;
    visibleMat = material;
//System.out.println(letter+".visible="+visible);    
    setMaterial(TRANSPARENT);
    hidden = true;
  } 

  public void unhide()
  {
    if (!hidden) {
      System.out.println("not hidden, returning");
      return;
    }
    //System.out.println(letter+".unhide()");
    if (visibleMat == null) 
      visibleMat = DEFAULT_MATERIAL;
    setMaterial(visibleMat);   
    hidden = false;
  }
  
  static final char[] PUNCT = {',','.',':',';','(',')','!'};
  boolean isPunct() {
    for (int i = 0; i < PUNCT.length; i++)
      if (letter == PUNCT[i]) return true;
    return false;
  }
  
  boolean isSpace() { return (letter == ' '); }
  
  public String toString() {
     String s = "[ "+Character.toString(letter)+": ";
     for (int j = 0; j < data.length; j++) {
       s += "{";
       for (int k = 0; k < data[j].length; k++)
         s += data[j][k]+",";
       s += "}";
     }
     return s + " ]\n";
  }
  
  char getLetter() { return letter; }
  double[][] getShapeData() { return data; }


} // end
