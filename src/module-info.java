module schedsplitter {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires java.base;
	
	opens openjfx to javafx.fxml;
	
	exports openjfx;
}