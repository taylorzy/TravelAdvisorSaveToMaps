package ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;

import business.logic.AnalysisAttractions;
import shared.TAtoGoogleUtils;

public class SimpleSwingBrowserPresenter implements
		SimpleSwingBrowserDisplay.Presenter {

	private final AnalysisAttractions analysisAttractions;

	private final SimpleSwingBrowserDisplay view;
	private boolean waitingForLoad = false;
	private boolean waitingForLogin = false;

	private Iterator<String> attractionUrlIter = null;
	public SimpleSwingBrowserPresenter(SimpleSwingBrowserDisplay view) {
		analysisAttractions = new AnalysisAttractions();
		view.bindPresenter(this);
		this.view = view;
	}

	/**
	 * 
	 * @param width
	 * @param height
	 * @param addr
	 * @return
	 */
	@Override
	public String formGoogleMapUrl(int width, int height, String addr) {
		final StringBuffer sb = new StringBuffer();
		sb.append(TAtoGoogleUtils.IFRAME_TAG).append(TAtoGoogleUtils.WIDTH_STR + width + " ")
				.append(TAtoGoogleUtils.HEIGHT_STR + height + " ").append(TAtoGoogleUtils.NO_BORDER)
				.append(TAtoGoogleUtils.SRC).append(addr.replaceAll(" ", "+")).append("'")
				.append(TAtoGoogleUtils.END_TAG);
		return sb.toString();
	}

	@Override
	public void performAnalysis(final Document document) {
		analysisAttractions.analysisAttractionPlaces(document);
		final Map<String, String> urls = analysisAttractions.getUrls();
		final Collection<String> values = urls.values();
		if (values.size() > 0) {
			attractionUrlIter = values.iterator();
			view.loadURL(TAtoGoogleUtils.BASE_URL + attractionUrlIter.next());
			waitingForLoad = true;
		}

	}

	private void searchAddressesInGoogle() {
		final List<String> addresses = analysisAttractions.getAddresses();
		if (addresses.size() > 0) {
			final Iterator<String> googleMapIter = addresses.iterator();
			final Timer timer = new Timer();
			final TimerTask timerTask = new TimerTask() {
				
				@Override
				public void run() {
					if(googleMapIter.hasNext()) {
						view.loadGoogleMap(formGoogleMapUrl(600, 450, googleMapIter.next()));
					}else{
						cancel();
					}
				}
			};
			timer.schedule(timerTask , 0, 10000);
		}
	}

	@Override
	public void loadSuccessed() {
		if (waitingForLoad) {
			final Document currentDocument = view.getCurrentDocument();
			if (currentDocument != null) {
				analysisAttractions
						.analysisAttractionAddresses(currentDocument);
			}
			if (attractionUrlIter.hasNext()) {
				view.loadURL(TAtoGoogleUtils.BASE_URL + attractionUrlIter.next());
			} else {
				waitingForLoad = false;
				gotoGoogleLoginPage();
			}
		} else if(waitingForLogin && view.getCurrentUrl().contains("person")) {
			waitingForLogin = false;
			searchAddressesInGoogle();
		}
	}

	/**
	 * 
	 */
	private void gotoGoogleLoginPage() {
		waitingForLogin = true;
		view.loadURL(TAtoGoogleUtils.GOOGLE_LOGIN);
	}
}
