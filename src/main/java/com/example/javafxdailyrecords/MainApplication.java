package com.example.javafxdailyrecords;

import com.example.javafxdailyrecords.config.MyBatisPlusConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {

    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());

    static {
        try {
            MyBatisPlusConfig.getSqlSession();
            logger.log(Level.INFO,"✅ MyBatis-Plus预初始化成功");
        } catch (Exception e) {
            logger.log(Level.SEVERE,"❌ MyBatis-Plus预初始化失败：" + e.getMessage(), e);

        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafxdailyrecords/fxml/dailyReport.fxml"));
        Parent root = loader.load();

        stage.setTitle("Daily Records");
        stage.setScene(new Scene(root, 1200, 800));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public void stop() {
        MyBatisPlusConfig.close();
    }
}
