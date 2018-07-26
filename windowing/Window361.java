package windowing;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import windowing.drawable.Drawable;

public class Window361 {
	public static final int STANDARD_GAP = 15; 		// pixels
	public static final int BUTTON_PLUS_GAP_WIDTH = 120;

	private PageTurner pageTurner;
	private HBox root;
	private Scene scene;
	private Image361 image = new Image361();
	private Button nextPageButton = new Button("next page");


	public Window361(Stage primaryStage) {
		primaryStage.setTitle("C361 Window");
		root = new HBox(STANDARD_GAP);

		setNextPageButtonCallback();

		root.getChildren().addAll(image, nextPageButton);
		scene = new Scene(root, Image361.IMAGE_X + BUTTON_PLUS_GAP_WIDTH, Image361.IMAGE_Y);
		primaryStage.setScene(scene);
	}

	private void setNextPageButtonCallback() {
		EventHandler<ActionEvent> callback = event->{pageTurner.nextPage();};
		nextPageButton.setOnAction(callback);
	}
	
	public Drawable getDrawable() {
		return image;
	}
	
	public void setPageTurner(PageTurner turner) {
		this.pageTurner = turner;
	}

}
