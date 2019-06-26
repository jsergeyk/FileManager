package ua.itea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.*;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

/**
 * @author Kalchenko Serhii
 */
public class MyController{
	
	private Locale currentLocale=new Locale("en","EN");
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private Logger logger;
	
	@FXML
	private MenuBar mainMenu;
	
	@FXML
	private Pane pnlBrowser;

	@FXML
	private Label lbl1;
	
	@FXML
	private Label lbl2;
	
	@FXML
	private Label lbl3;

    @FXML
    private void exitApp(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML
    private void changeLangEn(ActionEvent event) {
        currentLocale=new Locale("en","EN");
        changeLang();
    }
    
    @FXML
    private void changeLangRu(ActionEvent event) {
        currentLocale=new Locale("ru","RU");
        changeLang();
    }
    
    @FXML
    private void changeLangUa(ActionEvent event) {
        currentLocale=new Locale("ua","UA");
        changeLang();
    }
    
	/**
	 * Инициализация
	 */
	public void initData() {
		//System.out.print("my init");		
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;		
	}
	
	/**
	 * @return the pnlBrowser
	 */
	public Pane getPnlBrowser() {
		return pnlBrowser;
	}
	
	/*
	 * Изменение языка
	 */
	private void changeLang() {		
		ResourceBundle messages = ResourceBundle.getBundle("Bundle",currentLocale);
		System.out.println(pnlBrowser == null);
		ObservableList<Menu> menus = mainMenu.getMenus();
		Menu fileMenu = menus.get(0);
		fileMenu.setText(messages.getString("file"));
		ObservableList<MenuItem> fileSubMenu = fileMenu.getItems();
		fileSubMenu.get(0).setText(messages.getString("exit"));
		Menu langMenu = menus.get(1);
		langMenu.setText(messages.getString("lang"));
		Menu helpMenu = menus.get(2);
		helpMenu.setText(messages.getString("help"));
		
		lbl1.setText(messages.getString("lbl1"));
		lbl2.setText(messages.getString("lbl2"));
		lbl3.setText(messages.getString("lbl3"));
				
		logger.log(Level.INFO, formatter.format(LocalDateTime.now()) + " " + currentLocale + " " +
				lbl1.getText() + " : " + lbl2.getText() + " : " + lbl3.getText());
	}
}
