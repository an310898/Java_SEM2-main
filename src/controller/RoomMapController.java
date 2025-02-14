package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import dao.DBConnect;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import models.Room;
import models.SearchRoomSelectType;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.DoubleStream;

public class RoomMapController implements Initializable {
    @FXML
    private AnchorPane roomMapContainer;

    @FXML
    private JFXTabPane allRoomTabPane;

    @FXML
    private ScrollPane allRoomScrollPane;

    @FXML
    private HBox allRoomHBoxGrid;

    @FXML
    private GridPane gridAllRoom;

    @FXML
    private ScrollPane availableRoomScrollPane;

    @FXML
    private HBox availableRoomHBox;

    @FXML
    private GridPane gridAvailableRoom;

    @FXML
    private ScrollPane rentedRoomScrollPane;

    @FXML
    private HBox rentedRoomHBox;

    @FXML
    private GridPane gridRentedRoom;

    @FXML
    private ScrollPane dirtyRoomScrollPane;

    @FXML
    private HBox dirtyRoomHBox;

    @FXML
    private GridPane gridDirtyRoom;

    @FXML
    private JFXComboBox<SearchRoomSelectType> comboBoxSearchRoom;

    @FXML
    private JFXButton searchRoomBtn;

    public Integer col, row;

    private List<Room> rooms = new ArrayList<>();
    private List<Room> availableRooms = new ArrayList<>();
    private List<Room> rentedRooms = new ArrayList<>();
    private List<Room> dirtyRooms = new ArrayList<>();

    final double SPEED = 0.01;


    private String allRoomSql = "SELECT R.roomName, R.roomStatus, RT.roomTypeName FROM Room R JOIN RoomType RT ON R.roomTypeID = RT.roomTypeID";
    private String availableRoomSql = "SELECT R.roomName, R.roomStatus, RT.roomTypeName FROM Room R JOIN RoomType RT ON R.roomTypeID = RT.roomTypeID WHERE R.roomStatus = 'Available'";
    private String rentedRoomSql = "SELECT R.roomName, R.roomStatus, RT.roomTypeName FROM Room R JOIN RoomType RT ON R.roomTypeID = RT.roomTypeID WHERE R.roomStatus = 'Rented'";
    private String dirtyRoomSql = "SELECT R.roomName, R.roomStatus, RT.roomTypeName FROM Room R JOIN RoomType RT ON R.roomTypeID = RT.roomTypeID WHERE R.roomStatus = 'Dirty'";

    public List<Room> getData(List<Room> typeRoomToShow, String roomData) {
        typeRoomToShow = new ArrayList<>();
        Room room;
        DBConnect dbConnect = new DBConnect();
        dbConnect.readProperties();
        Connection conn = dbConnect.getDBConnection();

        ResultSet rs = null;
        Statement stm = null;

        try {

            stm = conn.createStatement();
            rs = stm.executeQuery(roomData);
            while (rs.next()) {
                room = new Room();
                room.setRoomName(rs.getString("roomName"));
                room.setRomeTypeName(rs.getString("roomTypeName"));
                room.setRoomStatus(rs.getString("roomStatus"));
                typeRoomToShow.add(room);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return typeRoomToShow;
    }

    private void showRoomToPane(ScrollPane scrollPane, HBox hBoxGrid, GridPane grid, List<Room> typeRoomToShow) {
        int column = 0;
        int row = 1;
        for (int i = 0; i < typeRoomToShow.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/resources/views/RoomView.fxml"));
            try {
                AnchorPane anchorPane = loader.load();
                RoomViewController roomViewController = loader.getController();
                roomViewController.setData(typeRoomToShow.get(i));

                if (column == 4) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //Binding width of children pane
        hBoxGrid.maxWidthProperty().bind(scrollPane.widthProperty().subtract(10));
        hBoxGrid.minWidthProperty().bind(scrollPane.widthProperty().subtract(10));
        scrollPane.maxWidthProperty().bind(allRoomTabPane.widthProperty());
        scrollPane.minWidthProperty().bind(allRoomTabPane.widthProperty());

        //Set Scroll Speed
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Search Type Select To Search
        comboBoxSearchRoom.setItems(FXCollections.observableArrayList(new SearchRoomSelectType("Floor"), new SearchRoomSelectType("Room name")));
        comboBoxSearchRoom.setConverter(new StringConverter<SearchRoomSelectType>() {
            @Override
            public String toString(SearchRoomSelectType searchRoomSelectType) {
                return searchRoomSelectType.getSearchType();
            }

            @Override
            public SearchRoomSelectType fromString(String s) {
                return null;
            }
        });
//        System.out.println(comboBoxSearchRoom.getValue());

        //Search button action
        searchRoomBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    System.out.println(comboBoxSearchRoom.getValue().getSearchType().toString());
                    rooms.removeAll(rooms);
                    showRoomToPane(allRoomScrollPane, allRoomHBoxGrid, gridAllRoom, rooms);
                } catch (NullPointerException e) {
                    System.out.println("null");
                }
            }
        });
        //All Room Tab
        rooms.addAll(getData(rooms, allRoomSql));
        showRoomToPane(allRoomScrollPane, allRoomHBoxGrid, gridAllRoom, rooms);

        //Available Room Tab
        availableRooms.addAll(getData(availableRooms, availableRoomSql));
        showRoomToPane(availableRoomScrollPane, availableRoomHBox, gridAvailableRoom, availableRooms);

        //Rented Room Tab
        rentedRooms.addAll(getData(rentedRooms, rentedRoomSql));
        showRoomToPane(rentedRoomScrollPane, rentedRoomHBox, gridRentedRoom, rentedRooms);

        //Dirty Room Tab
        dirtyRooms.addAll(getData(dirtyRooms, dirtyRoomSql));
        showRoomToPane(dirtyRoomScrollPane, dirtyRoomHBox, gridDirtyRoom, dirtyRooms);


    }


    public List<Room> getListAllRoom() {
        rooms = getData(rooms, allRoomSql);
        return rooms;
    }

    public List<Room> getListDirtyRoom() {
        rooms = getData(rooms, dirtyRoomSql);
        return rooms;
    }

//    public void selectTab() {
//        //Get tab name from roomview
//        FXMLLoader roomViewLoader = new FXMLLoader(getClass().getResource("/resources/views/RoomView.fxml"));
//        try {
//            Parent roomViewRoot = roomViewLoader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        RoomViewController roomViewcontroller = roomViewLoader.getController();
//        String gridRoomType = roomViewcontroller.getGridRoomType();
//        if (gridRoomType.equals("gridDirtyRoom")) {
//            allRoomTabPane.getSelectionModel().select(3);
//
//        }else{
//            allRoomTabPane.getSelectionModel().select(0);
//        }
//    }
}
