package com.example.javafxdailyrecords.controller;

import com.example.javafxdailyrecords.entity.DailyReport;
import com.example.javafxdailyrecords.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Objects;

public class ReportEditController {

    @FXML private Label dialogTitle;
    @FXML private DatePicker reportDatePicker;
    @FXML private Label week;
    @FXML private TextArea contentTextArea;
    @FXML private TextField remarkTextField;

    private boolean confirmed = false;
    private DailyReport currentReport;
    private Dialog<DailyReport> dialog;

    @FXML
    public void initialize() {
        LocalDate currentDay = LocalDate.now();
        reportDatePicker.setValue(currentDay);
        String original = week.getText();
        String chineseWeek = DateUtil.getChineseWeek(currentDay);
        week.setText(original + chineseWeek);
        bindDatePickEvents();
    }

    public void initData(DailyReport report) {
        currentReport = report;
        reportDatePicker.setValue(report.getReportDate());
        week.setText("星期：" + DateUtil.getChineseWeek(report.getReportDate()));
        contentTextArea.setText(report.getContent());
        remarkTextField.setText(report.getRemark() == null ? "" : report.getRemark());
    }

    public void setDialog(Dialog<DailyReport> dialog) {
        dialog = dialog;
        dialog.setResultConverter((dialogButton) -> {
            if (Objects.nonNull(dialogButton) && dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (!validateData()) {
                    return null;
                } else {
                    confirmed = true;
                    return buildReportData();
                }
            } else {
                confirmed = false;
                return null;
            }
        });
    }

    private DailyReport buildReportData() {
        if (currentReport == null) {
            currentReport = new DailyReport();
            currentReport.setUserId("1");
        }

        currentReport.setReportDate(reportDatePicker.getValue());
        currentReport.setWeek(DateUtil.getChineseWeek(reportDatePicker.getValue()));
        currentReport.setContent(contentTextArea.getText().trim());
        currentReport.setRemark(remarkTextField.getText().trim());
        return currentReport;
    }

    private boolean validateData() {
        if (reportDatePicker.getValue() == null) {
            showErrorAlert("请选择日报日期！");
            return false;
        } else if (contentTextArea.getText().trim().isEmpty()) {
            showErrorAlert("工作内容不能为空！");
            return false;
        } else {
            return true;
        }
    }

    private void bindDatePickEvents() {
        reportDatePicker.setOnAction((event) -> {
            String text = week.getText();
            String weekPrefix = text.substring(0, text.indexOf("：") + 1);
            week.setText(weekPrefix + DateUtil.getChineseWeek(reportDatePicker.getValue()));
        });
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("输入错误");
        alert.setHeaderText((String)null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Double.NEGATIVE_INFINITY);
        alert.showAndWait();
    }

    public DailyReport getReportData() {
        return currentReport;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
