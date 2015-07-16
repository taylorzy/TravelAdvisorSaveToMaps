package ui;

import org.w3c.dom.Document;

public interface SimpleSwingBrowserDisplay {
	
	interface Presenter {
		String formGoogleMapUrl(int width, int height, String addr);
		
		void performAnalysis(Document document);

		void loadSuccessed();
	}
	
	void bindPresenter(SimpleSwingBrowserDisplay.Presenter presenter);
	
	String getCurrentUrl();
	
	void loadURL(String url);

	Document getCurrentDocument();

	void loadGoogleMap(String formGoogleMapUrl);
}
