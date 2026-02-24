package com.example.javafxdailyrecords.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javafxdailyrecords.config.MyBatisPlusConfig;
import com.example.javafxdailyrecords.entity.DailyReport;
import com.example.javafxdailyrecords.mapper.DailyReportMapper;
import com.example.javafxdailyrecords.service.DailyReportService;
import com.example.javafxdailyrecords.util.CustomPage;
import com.example.javafxdailyrecords.util.DateUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DailyReportController {

    private static final Logger logger = Logger.getLogger(DailyReportController.class.getName());
    private DailyReportService dailyReportService;
    private DailyReportMapper reportMapper;
    @FXML
    private Button addReportBtn;
    @FXML
    private TableView<DailyReport> dailyReportTable;
    @FXML
    private TableColumn<DailyReport, Integer> indexColumn;
    @FXML
    private TableColumn<DailyReport, LocalDate> dateColumn;
    @FXML
    private TableColumn<DailyReport, String> weekColumn;
    @FXML
    private TableColumn<DailyReport, String> contentColumn;
    @FXML
    private TableColumn<DailyReport, String> remarkColumn;
    @FXML
    private TableColumn<DailyReport, Void> actionColumn;
    @FXML
    private Button generateWeekBtn;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML
    private TextField pageNumField;
    @FXML
    private Label totalPageLabel;
    @FXML
    private Label totalCountLabel;
    @FXML
    private ScrollPane tableScroll;
    @FXML
    private StackPane rootAnchor;

    private int currentPage = 1;
    private int pageSize = 7;
    private CustomPage<DailyReport> pageResult;

    @FXML
    public void initialize() {
        this.dailyReportService = new DailyReportService();
        this.reportMapper = MyBatisPlusConfig.getMapper(DailyReportMapper.class);
        if (Objects.isNull(reportMapper)) {
            throw new RuntimeException("获取DailyReportMapper失败，未注册到MybatisPlusMapperRegistry");
        } else {
            // 延迟初始化tableScroll监听（解决空指针）
            rootAnchor.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // 绑定根容器到Scene大小
                    rootAnchor.prefWidthProperty().bind(newScene.widthProperty());
                    rootAnchor.prefHeightProperty().bind(newScene.heightProperty());

                    // 初始化表格高度监听（此时tableScroll已注入完成）
                    initTableHeightListener();

                    // 强制刷新布局
                    rootAnchor.requestLayout();
                }
            });

            initBasicStyles();
            initTableColumns();
            loadDailyReportByPage(currentPage, pageSize);
            bindPageEvents();
            bindButtonEvents();
            logger.log(Level.INFO, "主控制器初始化完成");
        }
    }

    private void initBasicStyles() {
        addReportBtn.setOnMouseEntered((event) ->
                addReportBtn.setStyle("""
                        -fx-background-color: #66b1ff;
                        -fx-text-fill: white;
                        -fx-font-family: 'Microsoft YaHei';
                        -fx-font-size: 14px;
                        -fx-background-radius: 6px;
                        -fx-padding: 8px 20px;
                        -fx-cursor: hand;
                        -fx-border-width: 0;
                        """));
        addReportBtn.setOnMouseExited(e ->
                addReportBtn.setStyle("""
                        -fx-background-color: #409eff;
                        -fx-text-fill: white;
                        -fx-font-family: 'Microsoft YaHei';
                        -fx-font-size: 14px;
                        -fx-background-radius: 6px;
                        -fx-padding: 8px 20px;
                        -fx-cursor: hand;
                        -fx-border-width: 0;
                        """));
        generateWeekBtn.setOnMouseEntered(e ->
                generateWeekBtn.setStyle("""
                        -fx-background-color: #85ce61;
                        -fx-text-fill: white;
                        -fx-font-family: 'Microsoft YaHei';
                        -fx-font-size: 14px;
                        -fx-background-radius: 6px;
                        -fx-padding: 8px 20px;
                        -fx-cursor: hand;
                        -fx-border-width: 0;
                        """));
        generateWeekBtn.setOnMouseExited(e ->
                generateWeekBtn.setStyle("""
                        -fx-background-color: #67c23a;
                        -fx-text-fill: white;
                        -fx-font-family: 'Microsoft YaHei';
                        -fx-font-size: 14px;
                        -fx-background-radius: 6px;
                        -fx-padding: 8px 20px;
                        -fx-cursor: hand;
                        -fx-border-width: 0;
                        """));
        dailyReportTable.setRowFactory(tv -> new TableRow<>() {
            protected void updateItem(DailyReport dailyReport, boolean empty) {
                super.updateItem(dailyReport, empty);
                if (!empty && !Objects.isNull(dailyReport)) {
                    setStyle(getIndex() % 2 == 0 ?
                            "-fx-background-color: #fafafa; -fx-font-family: 'Microsoft YaHei'; -fx-font-size: 13px;" :
                            "-fx-background-color: white; -fx-font-family: 'Microsoft YaHei'; -fx-font-size: 13px;");
                    if (isSelected()) {
                        setStyle("-fx-background-color: #e6f7ff; -fx-font-family: 'Microsoft YaHei'; -fx-font-size: 13px;");
                    }
                } else {
                    setStyle("");
                }

            }
        });
    }

    private void initTableColumns() {
        indexColumn.setCellValueFactory((cellData) -> {
            int index = dailyReportTable.getItems().indexOf(cellData.getValue()) + 1;
            return (new SimpleIntegerProperty(index)).asObject();
        });
        indexColumn.setPrefWidth(60);
        indexColumn.setStyle("-fx-alignment: CENTER;");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        dateColumn.setPrefWidth(120);
        dateColumn.setStyle("-fx-alignment: CENTER;");

        weekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        weekColumn.setPrefWidth(60);
        weekColumn.setStyle("-fx-alignment: CENTER;");

        remarkColumn.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkColumn.setPrefWidth(150);
        remarkColumn.setStyle("-fx-alignment: CENTER;");

        actionColumn.setPrefWidth(140);
        actionColumn.setStyle("-fx-alignment: CENTER;");

        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentColumn.setPrefWidth(300);
        contentColumn.setCellFactory((column) -> new TableCell<>() {
            private final TextArea textArea = new TextArea();

            {
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setStyle("""
                        -fx-background-color: transparent;
                        -fx-border-width: 0;
                        -fx-font-family: 'Microsoft YaHei';
                        -fx-font-size: 13px;
                        -fx-padding: 5px;
                        -fx-text-fill: #303133;
                        """);
                textArea.setFocusTraversable(false);
                // 文本区域宽度绑定单元格，解决缩放截断
                textArea.prefWidthProperty().bind(this.widthProperty().subtract(10));
            }

            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && !Objects.isNull(item)) {
                    textArea.setText(item);
                    setGraphic(textArea);
                    TableRow<DailyReport> row = getTableRow();
                    if (row != null) {
                        if (row.isSelected()) {
                            textArea.setStyle("""
                                    -fx-background-color: transparent;
                                    -fx-border-width: 0;
                                    -fx-font-family: 'Microsoft YaHei';
                                    -fx-font-size: 13px;
                                    -fx-padding: 5px;
                                    -fx-text-fill: white;
                                    """);
                            setStyle("-fx-background-color: inherit;");
                        } else {
                            textArea.setStyle("""
                                    -fx-background-color: transparent;
                                    -fx-border-width: 0;
                                    -fx-font-family: 'Microsoft YaHei';
                                    -fx-font-size: 13px;
                                    -fx-padding: 5px;
                                    -fx-text-fill: #303133;
                                    """);
                            setStyle("-fx-background-color: inherit;");
                        }
                    }

                } else {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    setBackground(Background.EMPTY);
                }
            }
        });
        actionColumn.setCellFactory((column) -> new TableCell<>() {
            private final Button updateBtn = createStyledButton("更新", "#409eff", "#66b1ff");
            private final Button deleteBtn = createStyledButton("删除", "#f56c6c", "#f78989");
            private final HBox btnBox;

            {
                btnBox = new HBox(8, updateBtn, deleteBtn);
                btnBox.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER;");
                // 提前禁用按钮焦点，避免样式异常
                updateBtn.setFocusTraversable(false);
                deleteBtn.setFocusTraversable(false);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // 1、强制重置所有状态（核心！无论是否empty，先清空）
                setGraphic(null);
                setStyle("");
                setBackground(Background.EMPTY);
                // 清空事件绑定（避免复用导致的事件错乱）
                updateBtn.setOnAction(null);
                deleteBtn.setOnAction(null);
                // 2、仅当非空且有数据时，重新绑定并显示
                if (!empty) {
                    TableRow<DailyReport> row = getTableRow();
                    DailyReport rowData = row.getItem();
                    // 双重校验：row和row.getItem()都不为null
                    if (!Objects.isNull(row) && !Objects.isNull(rowData)) {
                        // 重新绑定事件
                        updateBtn.setOnAction((e) -> openEditDialog(rowData));
                        deleteBtn.setOnAction((e) -> confirmDelete(rowData));
                        // 显示按钮容器
                        setGraphic(btnBox);
                        // 继承行样式
                        setStyle("-fx-background-color: inherit;");
                    }
                }
            }
        });

        dailyReportTable.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #e6e6e6;
                -fx-border-width: 1px;
                -fx-border-radius: 6px;
                -fx-table-cell-border-color: transparent; // 隐藏单元格边框
                """);
        dailyReportTable.getStylesheets().add("data:text/css;charset=utf-8," + URLEncoder.encode("""
                .table-view .column-header {
                    -fx-background-color: #eef2f7;
                    -fx-border-color: transparent;
                }
                .table-view .column-header-background {
                    -fx-border-width: 0 0 1px 0;
                    -fx-border-color: #e6e6e6;
                }
                """, StandardCharsets.UTF_8));
    }

    // 初始化表格高度监听（单独抽离，避免空指针）
    private void initTableHeightListener() {
        if (tableScroll != null && dailyReportTable != null) {
            // 监听ScrollPane高度变化，适配表格高度
            tableScroll.heightProperty().addListener((obs, oldVal, newVal) -> {
                int dataCount = dailyReportTable.getItems() == null ? 0 : dailyReportTable.getItems().size();
                double tableHeight = dataCount * 80 + 30; // 80=行高，30=表头高度
                dailyReportTable.setPrefHeight(Math.min(tableHeight, newVal.doubleValue()));
            });

            // 初始设置表格高度
            double initHeight = tableScroll.getHeight() > 0 ? tableScroll.getHeight() : 600;
            dailyReportTable.setPrefHeight(initHeight);
        }
    }

    private void resetTableRowFactory() {
        dailyReportTable.setRowFactory((tv) -> {
            TableRow<DailyReport> row = new TableRow<>() {
                protected void updateItem(DailyReport item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        setPrefHeight(80);
                        if (isSelected()) {
                            setStyle("""
                                    -fx-font-family: 'Microsoft YaHei';
                                    -fx-font-size: 13px;
                                    -fx-background-color: -fx-selection-bar;  // 显式使用系统选中色
                                    -fx-text-fill: white; // 选中行文字变白，确保所有列内容可见
                                    """);
                        } else {
                            setStyle(getIndex() % 2 == 0 ? """
                                    -fx-background-color: #f8f9fa;
                                    -fx-font-family: 'Microsoft YaHei';
                                    -fx-font-size: 13px;
                                    """ :
                                    """
                                    -fx-background-color: white;
                                    -fx-font-family: 'Microsoft YaHei';
                                    -fx-font-size: 13px;
                                    """);
                        }
                    } else {
                        setStyle("");
                        setBackground(Background.EMPTY);
                        // 空行行高设为0，彻底隐藏
                        setPrefHeight(0);
                    }
                }
            };
            row.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && !row.isEmpty()) {
                    row.setStyle("""
                            -fx-font-family: 'Microsoft YaHei';
                            -fx-font-size: 13px;
                            -fx-background-color: -fx-selection-bar;
                            -fx-text-fill: white;
                            """);
                }
            });
            return row;
        });
    }

    private Button createStyledButton(String text, String normalColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-family: 'Microsoft YaHei';
                -fx-font-size: 12px;
                -fx-background-radius: 4px;
                -fx-padding: 4px 12px;
                -fx-cursor: hand;
                -fx-border-width: 0;
                -fx-effect: none;
                """, normalColor));

        btn.setOnMouseEntered(e -> btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-family: 'Microsoft YaHei';
                -fx-font-size: 12px;
                -fx-background-radius: 4px;
                -fx-padding: 4px 12px;
                -fx-cursor: hand;
                -fx-border-width: 0;
                -fx-effect: none;
                """, hoverColor)));

        btn.setOnMouseExited((e) ->
                btn.setStyle(String.format("""
                        -fx-background-color: %s;
                        fx-text-fill: white;
                        -fx-font-family: 'Microsoft YaHei';
                        -fx-font-size: 12px;
                        -fx-background-radius: 4px;
                        -fx-padding: 4px 12px;
                        -fx-cursor: hand;
                        -fx-border-width: 0;
                        -fx-effect: none;
                        """, normalColor)));
        btn.setFocusTraversable(false);
        return btn;
    }

    private void loadDailyReportByPage(int pageNum, int pageSize) {
        try {
            pageResult = dailyReportService.getDailyReportByPage(pageNum, pageSize);
            currentPage = pageResult.getPageNum();
            List<DailyReport> dataList = pageResult.getDataList();
            // 1. 清空表格原有数据（避免复用残留）
            dailyReportTable.getItems().clear();
            // 2. 刷新行工厂 重置所有TableCell
            resetTableRowFactory();
            dailyReportTable.setItems(FXCollections.observableArrayList(dataList));
            updatePageControls();
            logger.info(String.format("加载第%d页数据成功，共%d条，总记录数：%d", currentPage, dataList.size(), pageResult.getTotalCount()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "加载分页数据失败", e);
            showAlert(Alert.AlertType.ERROR, "加载失败", "加载日报数据失败：" + e.getMessage());
        }

    }

    private void updatePageControls() {
        pageNumField.setText(String.valueOf(currentPage));
        totalPageLabel.setText(String.valueOf(pageResult.getTotalPage()));
        totalCountLabel.setText(String.format("总记录数：%d条", pageResult.getTotalCount()));
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= pageResult.getTotalPage());
    }

    private void bindPageEvents() {
        prevPageBtn.setOnAction((e) -> {
            if (currentPage > 1) {
                loadDailyReportByPage(currentPage - 1, pageSize);
            }

        });
        nextPageBtn.setOnAction((e) -> {
            if (currentPage < pageResult.getTotalPage()) {
                loadDailyReportByPage(currentPage + 1, pageSize);
            }

        });
        pageNumField.setOnAction((e) -> {
            try {
                int targetPage = Integer.parseInt(pageNumField.getText().trim());
                if (targetPage < 1) {
                    targetPage = 1;
                }

                if (targetPage > pageResult.getTotalPage()) {
                    targetPage = pageResult.getTotalPage();
                }

                if (targetPage != currentPage) {
                    loadDailyReportByPage(targetPage, pageSize);
                }
            } catch (NumberFormatException var3) {
                showAlert(Alert.AlertType.ERROR, "输入错误", "请输入有效的页码数字！");
                pageNumField.setText(String.valueOf(currentPage));
            }

        });
    }

    private void bindButtonEvents() {
        addReportBtn.setOnAction((e) -> openEditDialog(null));
    }

    private void openEditDialog(DailyReport report) {
        try {
            String fxmlPath = "/com/example/javafxdailyrecords/fxml/addOrEditReport.fxml";
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new RuntimeException("FXML文件不存在：" + fxmlPath + "，请检查文件路径和名称是否正确！");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Dialog<DailyReport> dialog = new Dialog<>();
            dialog.setDialogPane(loader.load());
            ReportEditController editController = loader.getController();
            editController.setDialog(dialog);
            if (report != null) {
                editController.initData(report);
            }

            dialog.setTitle(report == null ? "新增" : "编辑");
            dialog.setResizable(false);
            dialog.showAndWait().ifPresent((savedReport) -> {
                boolean success;
                if (report == null) {
                    String chineseWeek = DateUtil.getChineseWeek(savedReport.getReportDate());
                    savedReport.setWeek(chineseWeek);
                    DailyReport exist = reportMapper.selectOne(new LambdaQueryWrapper<DailyReport>().eq(DailyReport::getReportDate, savedReport.getReportDate()));
                    if (!Objects.isNull(exist)) {
                        showAlert(Alert.AlertType.ERROR, "操作失败", "该日期已有日报！");
                        return;
                    }
                    success = reportMapper.insert(savedReport) > 0;
                } else {
                    savedReport.setId(report.getId());
                    String chineseWeek = DateUtil.getChineseWeek(savedReport.getReportDate());
                    savedReport.setWeek(chineseWeek);
                    success = reportMapper.updateById(savedReport) > 0;
                }

                if (success) {
                    loadDailyReportByPage(currentPage, pageSize);
                    showAlert(Alert.AlertType.INFORMATION, "操作成功", report == null ? "新增日报成功！" : "编辑日报成功！");
                } else {
                    showAlert(Alert.AlertType.ERROR, "操作失败", report == null ? "新增日报失败！" : "编辑日报失败！");
                }

            });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "加载弹窗失败：" + ex.getMessage(), ex);
            showAlert(Alert.AlertType.ERROR, "弹窗加载失败", "无法打开编辑窗口：" + ex.getMessage() + "\n请检查FXML文件路径和内容是否正确！");
        }

    }

    private void confirmDelete(DailyReport report) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText(String.format("确定要删除【%s】的日报吗？", report.getReportDate()));
        alert.getDialogPane().setMinHeight(Double.NEGATIVE_INFINITY);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = reportMapper.deleteById(report.getId()) > 0;
                if (success) {
                    loadDailyReportByPage(currentPage, pageSize);
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "日报已成功删除！");
                } else {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除日报时出错！");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "删除日报失败", e);
                showAlert(Alert.AlertType.ERROR, "删除失败", "删除日报时发生异常：" + e.getMessage());
            }
        }

    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Double.NEGATIVE_INFINITY);
        alert.showAndWait();
    }

}
