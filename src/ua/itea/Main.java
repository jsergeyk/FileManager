package ua.itea;

import java.awt.Desktop;
import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

import javafx.application.Application;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

/**
 * @author Kalchenko Serhii
 *
 */
public class Main extends Application {
	
	private static Logger logger;
	private File[] data;
	private File parentFile;
	private ListView<File> listw;
	private Path pathFrom;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		
		Parent root;
		ContextMenu contextMenu;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/itea/MyScene.fxml"));
			root = loader.load();
			stage.setTitle("HomeWorkBySergeyK");
			stage.setScene(new Scene(root));
            stage.show();
            
            try(FileInputStream ins = new FileInputStream("log.config")){
                LogManager.getLogManager().readConfiguration(ins);
            logger = Logger.getLogger(Main.class.getName());        
            } catch (Exception e) {
    			e.printStackTrace();
    			logger.log(Level.SEVERE, "", e);
    		}
            
            MyController controller = loader.getController();
            //controller.initData();
            controller.setLogger(logger);
            
            Pane browser = controller.getPnlBrowser();            
            data = File.listRoots();
        	listw = new ListView<>(FXCollections.observableArrayList(data));
        	listw.setOnMouseClicked((e) -> openFolder(e));
        	listw.setOnKeyPressed((e) -> openFolder(e));
        	browser.getChildren().add(listw);
        	
        	MenuItem itemOpen = new MenuItem("�������");
        	itemOpen.setAccelerator(new KeyCodeCombination(KeyCode.ENTER));
        	itemOpen.setOnAction((e) -> exec());

        	Menu itemCreate = new Menu("�������");        	        	        	
        		MenuItem itemCreateDir = new MenuItem("�����");
        		itemCreateDir.setAccelerator(new KeyCodeCombination(KeyCode.INSERT));
        		itemCreateDir.setOnAction((e) -> create(true, 1));
        		MenuItem itemCreateFile = new MenuItem("����");
        		itemCreateFile.setAccelerator(new KeyCodeCombination(KeyCode.INSERT, KeyCombination.CONTROL_DOWN));
        		itemCreateFile.setOnAction((e) -> create(false, 1));
        		itemCreate.getItems().addAll(itemCreateDir, itemCreateFile);
        	
        	MenuItem itemCopy = new MenuItem("����������");
        	itemCopy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        	itemCopy.setOnAction((e) -> copy(e));
        	
        	MenuItem itemPaste = new MenuItem("��������");
        	itemPaste.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        	itemPaste.setOnAction((e) -> paste(e));
        	
        	MenuItem itemDel = new MenuItem("�������");
        	itemDel.setAccelerator(new KeyCodeCombination(KeyCode.DELETE)); 
        	itemDel.setOnAction((e) -> del(e));
        	
        	contextMenu = new ContextMenu(itemOpen, itemCreate, itemCopy, itemPaste, itemDel);
    		listw.setContextMenu(contextMenu);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/*
	 * ��������� ������� ������� ������� ����
	 * @param e �������
	 */
	private void openFolder(Event e) {		
		ObservableList<File> folders = listw.getItems();
		int idx =listw.getSelectionModel().getSelectedIndex();
		if (idx > -1 && 
				(e instanceof MouseEvent && ((MouseEvent) e).getClickCount() == 2	//������� ����
				|| e instanceof KeyEvent && ((KeyEvent)e).getCode() == KeyCode.ENTER)) {
			File file = folders.get(idx);
			if (file.isDirectory()) {
				if (file.getName().equals("..")) {
					if (parentFile.getParentFile() != null) {
						folders = FXCollections.observableArrayList(parentFile.getParentFile().listFiles());
						parentFile = parentFile.getParentFile();
						folders.add(0, new File(".."));
					}
					else {
						folders = FXCollections.observableArrayList(data);
						parentFile = null;
					}
				} else {
					parentFile = file;
					folders = FXCollections.observableArrayList(file.listFiles());
					folders.add(0, new File(".."));
				}				
				listw.setItems(folders);
				logger.log(Level.INFO, "�������� ��������: " 
						+ (!file.getName().equals("..") ? file : parentFile != null ? parentFile : "filesystem roots"));
			} else {
				exec();
			}
		}
	}	
	
	/**
	 * ������� �������
	 * @param isDirectory true - �������, false -����
	 * @param count ���������� � ��������/����� (����� ����� (2))
	 */
	private void create(boolean isDirectory, int count) {
		if (parentFile != null) {
			Path newPath = null;
			try {				
				if(isDirectory) {
					newPath = Paths.get(parentFile.getAbsolutePath() + System.getProperty("file.separator")
						+ "����� �����" + (count > 1 ? " (" + count + ")" : ""));
					Files.createDirectory(newPath);					
				} else {
					newPath = Paths.get(parentFile.getAbsolutePath() + System.getProperty("file.separator")
						+ "����� ��������� ��������" + (count > 1 ? " (" + count + ")": "")  + ".txt");
					Files.createFile(newPath);
				}
				logger.log(Level.INFO, "������ " + (isDirectory ? "������� " : "���� ") + newPath);
				//�������� ������
				ObservableList<File> files = FXCollections.observableArrayList(parentFile.listFiles());			
				files.add(0, new File(".."));
				listw.setItems(files);
			} catch (FileAlreadyExistsException e) {			
				create(isDirectory, ++count);
			} catch (IOException e) { 
				logger.log(Level.SEVERE, "�� ������� ������� " + (isDirectory ? "������� " : "���� ") + newPath, e);
			}
		}
	}
	
	/**
	 * ������� ���� ����������� �� ���������
	 */
	private void exec() {
		int idx =listw.getSelectionModel().getSelectedIndex();
		if (idx > -1) {	//������� ����
			File file = listw.getItems().get(idx);
			if (file.isFile()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(file);
				} catch (IOException ex) {
					logger.log(Level.WARNING, "������ �������� ����� " + file.getAbsolutePath(), ex);
				}
			}
		}
	}
	
	/**
	 * ����������� ����� � �����
	 * @param e �������
	 */
	private void copy(ActionEvent e) {
		int idx = listw.getSelectionModel().getSelectedIndex();
		if (idx > -1 && !listw.getItems().get(idx).equals(new File(".."))) {
			File copiedFile = listw.getItems().get(idx);
			if (copiedFile.isFile()) {
				try {
					pathFrom = Paths.get(copiedFile.getAbsolutePath());					
				} catch (Exception ex) {				
					logger.log(Level.SEVERE, "�� ������� ����������� ���� " + pathFrom.toAbsolutePath(), ex);
				};
			} else {
				pathFrom = null;
			}
		}
	}
	
	/**
	 * �������� �� ������ ����
	 * @param e �������
	 */
	private void paste(ActionEvent e) {
		if (pathFrom != null && parentFile != null) {
			Alert alert = null;
			Path pathTo = Paths.get(parentFile + System.getProperty("file.separator") + pathFrom.getFileName());			
			if (Files.exists(pathTo)) {
				alert = new Alert(AlertType.NONE, "���� " + pathTo.getFileName() + " ����������. �������� ����?", ButtonType.YES, ButtonType.NO);
				alert.showAndWait();
			}
			if ((!Files.exists(pathTo) || alert.getResult() == ButtonType.YES)
					&& Files.exists(pathFrom)) {
				try {
					Files.copy(pathFrom, pathTo, StandardCopyOption.REPLACE_EXISTING);
					logger.log(Level.INFO, pathFrom + " ������� ���������� � " + pathTo);
					//�������� ������
					ObservableList<File> files = FXCollections.observableArrayList(parentFile.listFiles());			
					files.add(0, new File(".."));
					listw.setItems(files);
				} catch (IOException ex) {
					logger.log(Level.SEVERE, "�� ������� �������� ���� " + pathFrom, ex);
				}
			}
		}
	}
	
	/**
	 * �������� �����/��������
	 * @param e ������� 
	 */
	private void del(ActionEvent e) {
		int idx = listw.getSelectionModel().getSelectedIndex();
		if (idx > -1 && !listw.getItems().get(idx).equals(new File(".."))) {			
			File file = listw.getItems().get(idx);
			Alert alert = new Alert(AlertType.NONE, "�� ������������� ������ ������� " + file.getAbsolutePath() + " ?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				try {
					Files.delete(Paths.get(file.getAbsolutePath()));
					listw.getItems().remove(idx);
					logger.log(Level.INFO, file.getAbsolutePath() + " ������");
				} catch (java.nio.file.DirectoryNotEmptyException ex) {
					logger.log(Level.WARNING, "���������� ������� �� ������ ������� " + file.getAbsolutePath());
				} catch (java.nio.file.AccessDeniedException ex) {
					logger.log(Level.WARNING, "���������� ������� " + file.getAbsolutePath() + ". ����� ������ �����������.");
				} catch (IOException ex) {
					logger.log(Level.WARNING, "���������� ������� " + file.getAbsolutePath());
				}

			}
		}
	}
}
