// $Id: TextCube.java,v 1.1 2012/08/23 00:41:05 dhowe Exp $

package text3d;

import render.*;

/** 4-sided 'cube' with Stanza3D on each face */
public class TextCube extends Geometry
{
  //private static final double   CUBE_W  = 5.;
  private static final double   FACE_W  = 1.;
  protected static final double SCALE   = 6.;
  
  protected Font3D font = null;
  protected RenderableContext renderer = null;
  protected final Face3D[] sides = new Face3D[4];
  protected Material faceMaterial, textMaterial;
  protected boolean hidden;
  
  public TextCube(Font3D fnt, String[][] text, String[][][] swaps) {
    this(fnt, text, swaps, fnt.getSize());
  }
  
  public TextCube(Font3D fnt, String[][] text, String[][][] swaps, double fontSz)
  {
    super();
    if (text.length != 4) throw new IllegalArgumentException
      ("Cubes must have text for exactly 4 sides");
    this.font = fnt;
    this.renderer = fnt.getRenderer();      
    for (int j = 0; j < swaps.length; j++) 
      initFace(j, fnt, text, swaps, fontSz);     
  }   
  
/*  private void XXX(int j, Font3D fnt, String[][] text, String[][][] swaps, double fontSz)
  {
    //for (int j = 0; j < text.length; j++) 
    //for (int j = 0; j < text.length; j++) {
      sides[j] = new Face3D(fnt, text[j], swaps[j], fontSz);
      add(sides[j]);
      positionFace(sides[j],j);  
   // }
  }
*/
  private void initFace(int j, Font3D fnt, String[][] text, String[][][] swaps, double fontSz)
  {
    sides[j] = new Face3D(fnt, text[j], swaps[j], fontSz);
    add(sides[j]);
    positionFace(sides[j],j);  
  }
  
  private void scaleFace(Face3D face, double s)
  {
    renderer.push();
    renderer.scale(s, s, s);
    renderer.transform(face);
    renderer.pop();
  }
  
  public void swapText(int faceIdx, int stanzaIdx) 
  {
    /*if (sides[faceIdx] == null)
      sides[faceIdx] = ;*/
    sides[faceIdx].showStanza(stanzaIdx);
  }  
  
  public void resetText() 
  {
    for (int i = 0; i < 4; i++)  {
      sides[i].unhideStanza(0);
      for (int j = 1; j <= 4; j++) 
        sides[i].hideStanza(j); 
    }
  }

  public Geometry setMaterial(Material m) {
    this.faceMaterial = m;
    faceMaterial.setDoubleSided(true);
    for (int i = 0; i < sides.length; i++)
      sides[i].setMaterial(faceMaterial); 
    return this;
  }
  
  public Geometry setTextMaterial(Material tm) {
    this.textMaterial = tm;
    for (int i = 0; i < sides.length; i++)
      sides[i].setTextMaterial(textMaterial); 
    return this;
  }
  
  public void reset()
  {   
    scaleFace(sides[0],1);
    positionFace(sides[0],0);
  }
  
  public void setHidden(boolean h)
  {
    this.hidden = h;
    if (h) scale(0);
    else scale(1);
  }

  // PRIVATES ----------------------------------
  private void positionFace(Face3D f, int i)
  {
    renderer.push();
    switch (i) {
      case 0: // front 
        renderer.translate(0,0,FACE_W); 
        break;
      case 1: // left 
        renderer.translate(-FACE_W,0,0);
        renderer.rotateY(-Math.PI/2);   
        break;
      case 2: // back 
        renderer.translate(0,0,-FACE_W); 
        renderer.rotateY(-Math.PI);
        break;  
      case 3: // right             
        renderer.translate(FACE_W,0,0);
        renderer.rotateY(Math.PI/2);                        
        break;
    }
    renderer.transform(f);
    renderer.pop();
  }

  private void scale(double s)
  {
    renderer.push();
    renderer.scale(s, s, s);
    renderer.transform(this);
    renderer.pop();
  }

  public boolean isHidden()
  {
    return hidden;
  }
  
}// end
