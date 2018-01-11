package text3d;

import java.util.*;

import render.*;

/**
 * TODO:
 *   add kerning support to Line
 *   and kerning/leading support to Stanza
 */
public class Stanza3D extends Geometry
{
  private Font3D font = null;
  Line3D[] lines = null;
  private RenderableContext renderer;
  private static final Line3D[] LINES = new Line3D[0];
  
  public Stanza3D(Font3D f, String[] lines) {
    this(f, lines, f.getSize());
  }
  
  public Stanza3D(Font3D font, String[] lines, double fontSize) 
  {
    this.font = font;  
    List tmp = new ArrayList();
    this.renderer = font.getRenderer();
    double yShift = ((lines.length-1)/2d) * (font.getLeading() *fontSize);
    for (int i = 0; i < lines.length; i++) {
      double yOffset = (-i*(font.getLeading()*fontSize))+yShift;
      renderer.push();
        renderer.translate(0, yOffset, 0);
        Line3D l = new Line3D(font, lines[i], fontSize); 
        l.setYOffset(yOffset); // book-keeping
        renderer.transform(l);
        tmp.add(l);
      renderer.pop();
      add(l);
    }
    this.lines = (Line3D[])tmp.toArray(LINES);
  }
  
  public Line3D getLine(int i) { return lines[i]; };

  public Geometry getMesh(int m, int n) {
    Geometry mesh = new Geometry().mesh(m,n);
    mesh.add(this);
    return mesh;
  }
  
  public Font3D getFont() {
    return font;
  }
  
  public Line3D[] getLines() {
    return lines;
  }
  
  public String toString() {
    String s= "";
    for (int i = 0; i < lines.length; i++)
      s += lines[i];
    return s;
  }
}// end
  