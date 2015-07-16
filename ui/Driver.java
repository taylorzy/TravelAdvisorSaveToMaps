package ui;

import javax.swing.SwingUtilities;

public class Driver {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final SimpleSwingBrowserDisplay browser = new SimpleSwingBrowser();
				new SimpleSwingBrowserPresenter(browser);
				browser.loadURL("http://www.tripadvisor.com/");
			}
		});
	}
}
