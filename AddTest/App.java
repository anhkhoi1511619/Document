package com.example;
import com.example.data.StationError;
import com.example.base.AppFrame;
import com.example.log.LogCreator;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
      System.err.println("Check Error Tool");
      SwingUtilities.invokeLater(() -> {
	      AppFrame f = new AppFrame();
              f.setRunEvent((actionEvent) -> {
                  System.err.println("RUN");
		  //1
		  LogCreator logCreator1 = new LogCreator("./operation_log1.csv");
                  List<StationError> controllerLogs1 = logCreator1.create();
                  f.updateUIController1(controllerLogs1);
		  //2
		  LogCreator logCreator2 = new LogCreator("./operation_log2.csv");
                  List<StationError> controllerLogs2 = logCreator2.create();
                  f.updateUIController2(controllerLogs2);
		  //3
		  LogCreator logCreator3 = new LogCreator("./operation_log3.csv");
                  List<StationError> controllerLogs3 = logCreator3.create();
		  f.updateUIController3(controllerLogs3);
		  //4
		  LogCreator logCreator4 = new LogCreator("./operation_log4.csv");
                  List<StationError> controllerLogs4 = logCreator4.create();
		  f.updateUIController4(controllerLogs4);
              });
      });
    }
}
