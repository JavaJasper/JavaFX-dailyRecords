module com.example.javafxdailyrecords {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.logging;
    requires java.sql;

    requires mysql.connector.j;

    requires org.mybatis;
    requires com.baomidou.mybatis.plus.extension;
    requires com.baomidou.mybatis.plus.core;
    requires com.baomidou.mybatis.plus.annotation;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.javafxdailyrecords to javafx.fxml;
    opens com.example.javafxdailyrecords.controller to javafx.fxml;
    opens com.example.javafxdailyrecords.entity to org.mybatis, com.baomidou.mybatis.plus.core, javafx.base;
    opens com.example.javafxdailyrecords.mapper to org.mybatis, com.baomidou.mybatis.plus.core;
    opens com.example.javafxdailyrecords.config to org.mybatis, com.baomidou.mybatis.plus.core;
    exports com.example.javafxdailyrecords;
}