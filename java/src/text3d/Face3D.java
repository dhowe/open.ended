// $Id: Face3D.java,v 1.1 2012/08/23 00:41:05 dhowe Exp $

package text3d;

import render.*;

public class Face3D extends Geometry
{   
  Font3D font;
  String[] text;
  Stanza3D[] stanzas;
  RenderableContext context;
  Material realMaterial, realTextMaterial;

  public Face3D(Font3D font, String[] original)
  {
    this(font, original, font.getSize());
  }
  public Face3D(Font3D font, String[] original, double fontSz)
  {
    this(font, original, null, font.getSize());
  }
  public Face3D(Font3D font, String[] original, String[][] swaps){
    this(font, original, swaps, font.getSize());
  }
 
  public Face3D(Font3D font, String[] original, String[][] swaps, double fontSz)
  {
    super();
    mesh(2,2);
    this.font = font;
    this.context = font.getRenderer();
    if (swaps != null)
      initialize(font, original, swaps);
    else 
      initialize(font, original);    
    showStanza(0);
  }
  
  private void scaleStanza(Stanza3D stanza, double s) {
    context.push();
    context.scale(s,s,s);
    context.transform(stanza);
    context.pop();
  }  
  
  void hideStanza(int i) {
    scaleStanza(stanzas[i], 0);
  }
  
  void unhideStanza(int i) {
    scaleStanza(stanzas[i], 1);
  }
  
  public void showStanza(int idx) {
    for (int i = 0; i < stanzas.length; i++) {
      if (i != idx) hideStanza(i);
      else unhideStanza(i);      
    }
  }

  
  private void render(RenderableContext ra)
  {
    ra.push();
      ra.scale(TextCube.SCALE, TextCube.SCALE, 1);
      ra.transform(this);
    ra.pop();
  }
  
  // never called at present
  public void initialize(Font3D f, String[] txt)
  { 
    //System.out.println("Face3D.initialize(0)");
    this.text = txt;
    this.stanzas = new Stanza3D[1];
    stanzas[0] = new Stanza3D(f, txt, f.getSize()/18d);//????
    add(stanzas[0]);
    render(f.getRenderer());
  }
  
  public void initializeSwaps(Font3D f, String[][] swaps)
  {
    throw new RuntimeException("Implement me!");
  }
  
  public void initialize(Font3D f, String[] orig, String[][] swaps)
  { 
    //System.out.println("Face3D.initialize(1-4)");
    this.stanzas = new Stanza3D[swaps.length+1];
    this.stanzas[0] = new Stanza3D(f, orig, f.getSize()/18d);

    add(stanzas[0]);
    for (int i = 1; i < stanzas.length; i++) {
      this.stanzas[i] = new Stanza3D(f, swaps[i-1], f.getSize()/18d);
      add(stanzas[i]);
    }
    render(f.getRenderer());
  }

  public Geometry setMaterial(Material m) {
    m.setDoubleSided(true);
    this.realMaterial = m;
    super.setMaterial(m);
    return this;
  }
  
  public Geometry setTextMaterial(Material m) {
    m.setDoubleSided(true);
    this.realTextMaterial = m;
    for (int i = 0; i < stanzas.length; i++) {
      if (stanzas[i] != null) 
        stanzas[i].setMaterial(m);
    }
    return this;
  }

  public Font3D getFont()
  {
    return stanzas[0].getFont();
  }
  public String  toString() {
    String s = "";
    for (int i = 0; i < stanzas.length; i++) {
      s += stanzas[i]+", ";
    }
    return s;
  }
}// end
