package ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import javax.swing.*;

import org.w3c.dom.Document;

import shared.TAtoGoogleUtils;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;

import static javafx.concurrent.Worker.State.FAILED;

public class SimpleSwingBrowser extends JFrame implements SimpleSwingBrowserDisplay {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 499165618807918034L;

	private final JFXPanel jfxPanel = new JFXPanel();
	private Scene mainScene;
	private WebEngine engine;

	private final JPanel panel = new JPanel(new BorderLayout());
	private final JLabel lblStatus = new JLabel();

	private final JButton btnGo = new JButton("Refresh");
	private final JButton btnBak = new JButton("\u22b2Back");
	private final JButton btnFwd = new JButton("Forward\u22b3");
	private final JTextField txtURL = new JTextField();
	private final JTextField textField = new JTextField("找到attraction后请按分析钮");
	private final JButton analysis = new JButton("Analysis");
	private final JProgressBar progressBar = new JProgressBar();

	private Presenter presenter;

	public SimpleSwingBrowser() {
		super();
		initComponents();
		setVisible(true);
	}

	private void initComponents() {
		createScene();

		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadURL(txtURL.getText());
			}
		};

		btnGo.addActionListener(al);
		txtURL.addActionListener(al);
		txtURL.setSize(180, btnGo.getHeight());

		ActionListener back = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goBack();
			}
		};

		btnBak.addActionListener(back);
		btnBak.setEnabled(false);
		btnBak.setPreferredSize(new Dimension(100, 30));

		ActionListener forward = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goForward();
			}
		};
		btnFwd.addActionListener(forward);
		btnFwd.setEnabled(false);
		btnFwd.setPreferredSize(new Dimension(100, 30));
		
		final ActionListener analysisListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.performAnalysis(getCurrentDocument());
			}
		};
		
		analysis.addActionListener(analysisListener);

		progressBar.setPreferredSize(new Dimension(150, 18));
		progressBar.setStringPainted(true);

		JPanel topBar = new JPanel(new BorderLayout());
		JPanel urlBar = new JPanel(new FlowLayout());
		JPanel logicBar = new JPanel(new FlowLayout());
		// topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		urlBar.add(btnBak);
		urlBar.add(btnFwd);
		urlBar.add(txtURL);
		urlBar.add(btnGo);
		
		textField.setEditable(false);
		logicBar.add(textField);
		logicBar.add(analysis);
		topBar.add(urlBar, BorderLayout.NORTH);
		topBar.add(logicBar, BorderLayout.SOUTH);

		JPanel statusBar = new JPanel(new BorderLayout(5, 0));
		statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		statusBar.add(lblStatus, BorderLayout.CENTER);
		statusBar.add(progressBar, BorderLayout.EAST);

		panel.add(topBar, BorderLayout.NORTH);
		panel.add(jfxPanel, BorderLayout.CENTER);
		panel.add(statusBar, BorderLayout.SOUTH);

		getContentPane().add(panel);

		setPreferredSize(new Dimension(1024, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		
		txtURL.setPreferredSize(new Dimension(700, 20));
		txtURL.setEnabled(false);
	}

	private boolean canGoBack() {
		return engine.getHistory().getCurrentIndex() > 0;
	}

	@Override
	public String getCurrentUrl() {
		return engine.getLocation();
	}

	private boolean canGoForward() {
		final WebHistory history = engine.getHistory();
		return history.getCurrentIndex() < history.getEntries().size() - 1;
	}

	private void createScene() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebView view = new WebView();
				engine = view.getEngine();
				
				engine.titleProperty().addListener(
						new ChangeListener<String>() {
							@Override
							public void changed(
									ObservableValue<? extends String> observable,
									String oldValue, final String newValue) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										SimpleSwingBrowser.this
												.setTitle(newValue);
									}
								});
							}
						});

				engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
					@Override
					public void handle(final WebEvent<String> event) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								lblStatus.setText(event.getData());
							}
						});
					}
				});

				engine.locationProperty().addListener(
						new ChangeListener<String>() {
							@Override
							public void changed(
									ObservableValue<? extends String> ov,
									String oldValue, final String newValue) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										txtURL.setText(newValue);
									}
								});
							}
						});

				engine.getLoadWorker().stateProperty()
						.addListener(new ChangeListener<State>() {
							@Override
							public void changed(
									ObservableValue<? extends State> ov,
									State oldState, State newState) {
								if (newState == State.SUCCEEDED) {
									getContentPane().setVisible(true);
									if (canGoBack()) {
										btnBak.setEnabled(true);
									} else {
										btnBak.setEnabled(false);
									}

									if (canGoForward()) {
										btnFwd.setEnabled(true);
									} else {
										btnFwd.setEnabled(false);
									}
								}
							}

						});

				engine.getLoadWorker().workDoneProperty()
						.addListener(new ChangeListener<Number>() {
							@Override
							public void changed(
									ObservableValue<? extends Number> observableValue,
									Number oldValue, final Number newValue) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										progressBar.setValue(newValue
												.intValue());
										if (progressBar.getValue() >= 95) {
											presenter.loadSuccessed();
										}
									}
								});
							}
						});

				engine.getLoadWorker().exceptionProperty()
						.addListener(new ChangeListener<Throwable>() {

							public void changed(
									ObservableValue<? extends Throwable> o,
									Throwable old, final Throwable value) {
								if (engine.getLoadWorker().getState() == FAILED) {
									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											JOptionPane
													.showMessageDialog(
															panel,
															(value != null) ? engine
																	.getLocation()
																	+ "\n"
																	+ value.getMessage()
																	: engine.getLocation()
																			+ "\nUnexpected error.",
															"Loading error...",
															JOptionPane.ERROR_MESSAGE);
										}
									});
								}
							}
						});

				mainScene  = new Scene(view);
				jfxPanel.setScene(mainScene);
				engine.setUserAgent(TAtoGoogleUtils.CHROME_USER_AGENT);
			}
		});
	}

	@Override
	public void loadURL(final String url) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String tmp = toURL(url);

				if (tmp == null) {
					tmp = toURL("http://" + url);
				}
				engine.load(tmp);
			}
		});
	}

	private static String toURL(String str) {
		try {
			return new URL(str).toExternalForm();
		} catch (MalformedURLException exception) {
			return null;
		}
	}

	private String goBack() {
		final WebHistory history = engine.getHistory();
		ObservableList<WebHistory.Entry> entryList = history.getEntries();
		final int currentIndex = history.getCurrentIndex();
		// Out("currentIndex = "+currentIndex);
		// Out(entryList.toString().replace("],","]\n"));
		
		Platform.runLater(new Runnable() {
			public void run() {
				history.go(currentIndex > 0 ? -1 : 0);
			}
		});
		
		return entryList
				.get(currentIndex > 0 ? currentIndex - 1 : currentIndex)
				.getUrl();
	}

	private String goForward() {
		final WebHistory history = engine.getHistory();
		final ObservableList<WebHistory.Entry> entryList = history.getEntries();
		final int currentIndex = history.getCurrentIndex();

		Platform.runLater(new Runnable() {
			public void run() {
				history.go(currentIndex < entryList.size() - 1 ? 1 : 0);
			}
		});
		
		return entryList.get(
				currentIndex < entryList.size() - 1 ? currentIndex + 1
						: currentIndex).getUrl();
	}

	@Override
	public void bindPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Document getCurrentDocument() {
		return engine.getDocument();
	}

	@Override
	public void loadGoogleMap(final String formGoogleMapUrl) {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	engine.loadContent(formGoogleMapUrl);
	        }
	   });
	}
}