package controller;

import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.RoomSettingModel;
import models.RoomSettingTypeModel;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomSettingController implements Initializable {
    @FXML
    private TableView<RoomSettingModel> table;
    @FXML
    private TableColumn<RoomSettingModel,String> number;
    @FXML
    private TableColumn<RoomSettingModel,String>  type;
    @FXML
    private TableColumn<RoomSettingModel,String>  status;
    @FXML
    private TableColumn<RoomSettingModel, Integer>  floor1;
    @FXML
    private TableColumn<RoomSettingModel,String>  price;


    @FXML
    private TableView<RoomSettingTypeModel> table1;
    @FXML
    private TableColumn<RoomSettingTypeModel, Integer>  id;
    @FXML
    private TableColumn<RoomSettingTypeModel,String>  name;



    ObservableList<RoomSettingModel> oblist = FXCollections.observableArrayList();
    ObservableList<RoomSettingTypeModel> oblist1 = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT roomName,roomTypeName,roomStatus,roomFloor,roomPrice from Room R join RoomType RID on R.roomTypeID = RID.roomTypeID");
            while(rs.next()){
                oblist.add(new RoomSettingModel(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5)));
            }

        } catch (SQLException e) {
            Logger.getLogger(RoomSettingController.class.getName()).log(Level.SEVERE,null,e);
            e.printStackTrace();
        }
        try{
            DBConnect dbConnect = new DBConnect();
            dbConnect.readProperties();
            Connection conn = dbConnect.getDBConnection();
            ResultSet rs1 = conn.createStatement().executeQuery("select * from RoomType");
            while (rs1.next()){
                oblist1.add(new RoomSettingTypeModel(rs1.getInt(1),rs1.getString(2)));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        floor1.setCellValueFactory(new PropertyValueFactory<>("floor1"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));


        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));


        table.setItems(oblist);
        table1.setItems(oblist1);
    }
    @FXML
    public void clickItem(MouseEvent event)
    {
        System.out.println(event.getPoint());
        if (event.getClickCount() == 1)
        {
            System.out.println(table.getSelectionModel().getSelectedItem().getNumber());
            System.out.println(table.getSelectionModel().getSelectedItem().getType());
            System.out.println(table.getSelectionModel().getSelectedItem().getStatus());
        }
    }

}



