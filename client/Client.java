package client;

import client.testPages.StarburstLineTest;
import geometry.Point2D;
import line.AlternatingLineRenderer;
import line.ExpensiveLineRenderer;
import line.LineRenderer;
import windowing.drawable.ColoredDrawable; //mine
import client.testPages.MeshPolygonTest;
import client.testPages.ParallelogramTest;
import client.testPages.RandomLineTest;
import client.testPages.RandomPolygonTest;
import client.testPages.StarburstPolygonTest;
import client.testPages.centeredTriangleTest; //new for ASSN2
import line.AntialiasingLineRenderer;
import line.BresenhamLineRenderer; //mine
import line.DDALineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.WireframePolygonRenderer; //new for ASSN 2
import polygon.PolygonRenderer;
import windowing.PageTurner;
import windowing.drawable.Drawable;
import windowing.drawable.DepthCueingDrawable; //assn2
import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.graphics.Dimensions;
import windowing.graphics.Color; //added for assn2

import client.testPages.myTest; //for debugging
import client.interpreter.SimpInterpreter;

public class Client implements PageTurner {
	public boolean hasArgument = false;

	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;

	private static final int NUM_PAGES = 17; //8 for assn2
	protected static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 1; //changed for assn2
	private static final Dimensions PANEL_SIZE = new Dimensions(300, 300);
	private static final Point2D[] lowCornersOfPanels = {
			new Point2D( 50, 700),
			new Point2D(700, 700),
			new Point2D( 50,  50),
			new Point2D(700,  50),
	};

	private final Drawable drawable;
	private int pageNumber = 0;

	private Drawable image;
	private Drawable[] panels;
	private Drawable[] ghostPanels;					// use transparency and write only white
	private Drawable fullPanel;

	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;
	private WireframePolygonRenderer wireframeRenderer; //add that DDA renderer


	public Client(Drawable drawable) {
		this.drawable = drawable;
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
	  image = new ColoredDrawable(image, ARGB_WHITE);

		fullPanel = new TranslatingDrawable(image, point(  50, 50),  dimensions(650, 650));

		createPanels();
		createGhostPanels();
	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];

		for(int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
		}
	}

	private void createGhostPanels() {
		ghostPanels = new Drawable[NUM_PANELS];

		for(int index = 0; index < NUM_PANELS; index++) {
			Drawable drawable = panels[index];
			ghostPanels[index] = new GhostWritingDrawable(drawable, GHOST_COVERAGE);
		}
	}
	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	private void createRenderers() {

		lineRenderers = new LineRenderer[4];
		lineRenderers[0] = BresenhamLineRenderer.make();
		//lineRenderers[0] = ExpensiveLineRenderer.make();
		lineRenderers[1] = DDALineRenderer.make();
		lineRenderers[2] = AlternatingLineRenderer.make();
		lineRenderers[3] = AntialiasingLineRenderer.make();

		polygonRenderer = FilledPolygonRenderer.make();

		//polygonRenderer = null;

		//ASSIGNMENT 2
		wireframeRenderer= WireframePolygonRenderer.make();
		wireframeRenderer.setRenderer(DDALineRenderer.make());
	}

	//given for assn3 and adjusted by me
	@Override
	public void nextPage() {
		if(Main.getArgs().length > 0){
			hasArgument = true;
		}
		if(hasArgument) {
			argumentNextPage();
		}
		else {
			noArgumentNextPage();
		}
	}

	//this was incomplete with no indication that it was
	private void argumentNextPage() {
		SimpInterpreter interpreter;
		String[] args = Main.getArgs();
		String filename;
		RendererTrio renderers = new RendererTrio(DDALineRenderer.make(), FilledPolygonRenderer.make(), wireframeRenderer);
		if(args.length > 0){
			filename = args[0];
		}
		else {filename = "broken";}

		image.clear();
		fullPanel.clear();

		interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}

	//also incomplete
	public void noArgumentNextPage() {
		SimpInterpreter interpreter;
		RendererTrio renderers = new RendererTrio(DDALineRenderer.make(), FilledPolygonRenderer.make(), wireframeRenderer);

		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;

		image.clear();
		fullPanel.clear();
		String filename;

		switch(pageNumber) {
		case 1:  filename = "page-a1";	 break;
		case 2:  filename = "page-a2";	 break;
		case 3:	 filename = "page-a3";	 break;
		case 4:  filename = "page-b1";	 break;
		case 5:  filename = "page-b2";	 break;
		case 6:  filename = "page-b3";	 break;
		case 7:  filename = "page-c1";	 break;
		case 8:  filename = "page-c2";	 break;
		case 9:  filename = "page-c3";	 break;
		case 10:  filename = "page-d";	 break;
		case 11:  filename = "page-e";	 break;
		case 12:	filename = "page-f1";	 break;
		case 13:  filename = "page-f2";	 break;
		case 14:  filename = "page-g";	 break;
		case 15:  filename = "page-h";	 break;
		case 16:  filename = "page-i";	 break;

		default: defaultPage();
				 return;
		}
		interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}


	@FunctionalInterface
	private interface TestPerformer {
		public void perform(Drawable drawable, LineRenderer renderer);
	}
	private void lineDrawerPage(TestPerformer test) {
		image.clear();

		for(int panelNumber = 0; panelNumber < panels.length; panelNumber++) {
			panels[panelNumber].clear();
			test.perform(panels[panelNumber], lineRenderers[panelNumber]);
		}
	}
	public void polygonDrawerPage(Drawable[] panelArray) {
		image.clear();
		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}
		new StarburstPolygonTest(panelArray[0], polygonRenderer);
		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
		new MeshPolygonTest(panelArray[2], polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
		new RandomPolygonTest(panelArray[3], polygonRenderer);
	}

	private void defaultPage() {
		image.clear();
		fullPanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
