package client;

import line.LineRenderer;
import line.DDALineRenderer;
import polygon.PolygonRenderer;
import polygon.FilledPolygonRenderer;
import polygon.WireframePolygonRenderer;

public class RendererTrio {
  private final LineRenderer lineRenderer;
  private final PolygonRenderer polygonRenderer;
  private final WireframePolygonRenderer wireframeRenderer;

  public RendererTrio(LineRenderer lineRenderer, PolygonRenderer polygonRenderer, WireframePolygonRenderer wireframeRenderer){
     this.lineRenderer = lineRenderer;
     this.polygonRenderer = polygonRenderer;
     this.wireframeRenderer = wireframeRenderer;
   }

   public LineRenderer getLineRenderer(){
     return this.lineRenderer;
   }
   public PolygonRenderer getFilledRenderer(){
     return this.polygonRenderer;
   }
   public WireframePolygonRenderer getWireframeRenderer(){
     return this.wireframeRenderer;
   }
}
