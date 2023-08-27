package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application{
	Scene scene;
	BorderPane bp;
	GridPane gp;
	FlowPane fp;
	
	Label nameLbl, priceLbl, typeLbl;
	public TextField nameFld;
	public Spinner<Integer> priceSpin;
	public ComboBox<String> typeCb;
	public Button insertBtn, updateBtn, deleteBtn;
	public TableView<MenuList> menuTable;
	
	public void initialize() {
		bp = new BorderPane();
		gp = new GridPane();
		fp = new FlowPane();
		
		nameLbl = new Label("Menu Name");
		priceLbl = new Label("Menu Price");
		typeLbl = new Label("Menu Type");
		nameFld = new TextField();
		priceSpin = new Spinner<>(15000, 80000, 15000, 500);
		typeCb = new ComboBox<>();
		typeCb.setItems(FXCollections.observableArrayList("Main Course", "Snack / Appetizer", "Dessert", "Drink"));
		
		insertBtn = new Button("Insert Menu");
		updateBtn = new Button("Update Menu");
		deleteBtn = new Button("Delete Menu");
		
		menuTable = new TableView<>();
		
		scene = new Scene(bp, 400, 500);
	}
	
	public void layout() {
		insertBtn.setMaxSize(100, 10);
		updateBtn.setMaxSize(100, 10);
		deleteBtn.setMaxSize(100, 10);
		typeCb.setMaxSize(200, 10);
		fp.setHgap(20);
		gp.add(nameLbl, 0, 0);
		gp.add(nameFld, 1, 0);
		gp.add(priceLbl, 0, 1);
		gp.add(priceSpin, 1, 1);
		gp.add(typeLbl, 0, 2);
		gp.add(typeCb, 1, 2);
		gp.add(insertBtn, 2, 0);
		gp.add(updateBtn, 2, 1);
		gp.add(deleteBtn, 2, 2);
		
		bp.setTop(fp);
		bp.setCenter(menuTable);
		bp.setBottom(gp);
		
		gp.setHgap(10);
		gp.setVgap(10);
	}
	
	public void setTable() {
		TableColumn<MenuList, String> idCol = new TableColumn<MenuList, String>("Menu ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("menuId"));
		TableColumn<MenuList, String> nameCol = new TableColumn<MenuList, String>("Menu Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("menuName"));
		TableColumn<MenuList, Integer> priceCol = new TableColumn<MenuList, Integer>("Menu Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("menuPrice"));
		TableColumn<MenuList, String> typeCol = new TableColumn<MenuList, String>("Menu Type");
		typeCol.setCellValueFactory(new PropertyValueFactory<>("menuType"));
		menuTable.getColumns().addAll(idCol, nameCol, priceCol, typeCol);
	}
	
	public String genId() {
		String query = "SELECT * FROM menu";
		Connect con = new Connect();
		ResultSet rs = con.execQuery(query);
		
		String menuId = "";
		try {
			while (rs.next()) {
				menuId = rs.getString("menuId");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int count = Integer.parseInt(menuId.substring(2));
		return String.format("MN%03d", count+1);
		
	}
	
	public void viewDB() {
		String query = "SELECT * FROM menu";
		Connect con = new Connect();
		ResultSet rs = con.execQuery(query);
		
		try {
			while (rs.next()) {
				String menuId = rs.getString("menuId");
				String menuName = rs.getString("menuName");
				int menuPrice = rs.getInt("menuPrice");
				String menuType = rs.getString("menuType");
				
				MenuList menuList = new MenuList(menuId, menuName, menuPrice, menuType);
				menuTable.getItems().add(menuList);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void refresh() {
		menuTable.getItems().clear();
		viewDB();
	}
	
	public void buttonAct() {
		
		insertBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				String query = "INSERT INTO menu VALUES('"+genId()+"', '"+nameFld.getText()+"', '"+priceSpin.getValue()+"', '"+typeCb.getValue()+"')";
				Connect con = new Connect();
				con.execUpdate(query);
				refresh();
			}
		});
		
		menuTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MenuList>() {

			@Override
			public void changed(ObservableValue<? extends MenuList> observable, MenuList oldValue, MenuList newValue) {
				// TODO Auto-generated method stub
				if (newValue != null) {
					String menuName = newValue.getMenuName();
					int menuPrice = newValue.getMenuPrice();
					String menuType = newValue.getMenuType();
					
					nameFld.setText(menuName);
					priceSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15000, 80000, menuPrice, 500));
					typeCb.setValue(menuType);
					
				}
			}
		});
		
		updateBtn.setOnAction(x->{
			MenuList selectedMenu = menuTable.getSelectionModel().getSelectedItem();
			
			if (selectedMenu != null) {
				String query = "UPDATE `menu` \r\n"
								+ "SET `menuName`=?, `menuPrice`=?, `menuType`=? \r\n"
								+ "WHERE `menuId` = ?"; 
				
				Connect con = new Connect();
				PreparedStatement ps = con.preparedStatement(query);
				try {
					ps.setString(1, nameFld.getText());
					ps.setInt(2, priceSpin.getValue());
					ps.setString(3, typeCb.getValue());
					ps.setString(4, selectedMenu.getMenuId());
					ps.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				refresh();
			}
		});
		
		deleteBtn.setOnAction(x->{
			MenuList selectedMenu = menuTable.getSelectionModel().getSelectedItem();
			if (selectedMenu != null) {
				String query = String.format("DELETE FROM menu WHERE menuId = '%s'", 
								selectedMenu.getMenuId());
			Connect con = new Connect();
			con.execUpdate(query);
			refresh();
			}
			
		});
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		initialize();
		layout();
		setTable();
		viewDB();
		buttonAct();
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Manage Menu");
		primaryStage.show();
	}

}
