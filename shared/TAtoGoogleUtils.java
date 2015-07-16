package shared;

public class TAtoGoogleUtils {
	public static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
	
	public static final String IFRAME_TAG = "<iframe ";
	public static final String WIDTH_STR = "width=";
	public static final String HEIGHT_STR = "height=";
	public static final String NO_BORDER = "frameborder='0'";// style='border:0'
	public static final String SRC = "src='https://www.google.com/maps/embed/v1/place?key=AIzaSyB8iUUPvtVjeBmurDB69toSHn8l3x1754w&q=";
	public static final String END_TAG = "/>";
	

	public static String BASE_URL = "http://www.tripadvisor.com";
	public static String GOOGLE_LOGIN = "https://accounts.google.com/Login?hl=EN";
}
