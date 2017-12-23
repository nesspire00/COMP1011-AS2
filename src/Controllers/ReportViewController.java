package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportViewController implements Initializable {

    @FXML private LineChart profitLineChart;
    @FXML private NumberAxis profitAxis;
    @FXML private CategoryAxis monthAxis;
    @FXML private Spinner<Integer> yearSpinner;
    private XYChart.Series profitSeries;
    @FXML private Label popularityReportLabel;
    @FXML private Label mostRevenueLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        profitSeries = new XYChart.Series<>();

        try {
            populateFromDb(LocalDate.now().getYear());

        } catch (SQLException e) {
            System.err.println(e);
        }

        profitLineChart.getData().addAll(profitSeries);

        SpinnerValueFactory<Integer> yearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2000, LocalDate.now().getYear(), LocalDate.now().getYear());
        yearSpinner.setValueFactory(yearValueFactory);
        yearSpinner.setEditable(false);
    }

    public void backButtonClicked(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory");
    }

    public void showYearButtonClicked(ActionEvent event) throws SQLException {
        populateFromDb(yearSpinner.getValue());
    }

    public void populateFromDb(int year) throws SQLException {
        profitSeries.getData().clear();
        profitSeries.setName(Integer.toString(year));

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es/gc200348171?useSSL=false", "gc200348171", "YaTa2qzm");

            statement = conn.prepareStatement("SELECT MONTHNAME(saleDate), SUM(salePrice - storePrice) " +
                    "FROM sales " +
                    "WHERE YEAR(saleDate) = ? " +
                    "GROUP BY MONTH(saleDate);");
            statement.setInt(1, year);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                profitSeries.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getDouble(2)));
            }


            statement = conn.prepareStatement("SELECT itemId, brand, modelNo, COUNT(itemId) " +
                    "FROM sales INNER JOIN television ON sales.itemId = television.tvId " +
                    "GROUP BY itemId " +
                    "ORDER BY 4 DESC " +
                    "LIMIT 5; ");


            resultSet = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                sb.append(resultSet.getString(2) + "/" +
                        resultSet.getString(3) + "(" +
                        resultSet.getInt(1) + ") sold " +
                        resultSet.getInt(4) + " time(s)\n");
                sb.append("\n");
            }
            popularityReportLabel.setText(sb.toString());

            sb = null;

            sb = new StringBuilder();

            statement = conn.prepareStatement("SELECT itemId, brand, modelNo, MAX(salePrice - sales.storePrice) " +
                    "FROM sales INNER JOIN television ON sales.itemId = television.tvId " +
                    "GROUP BY itemId " +
                    "ORDER BY 4 DESC " +
                    "LIMIT 5;");


            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString(2) + "/" +
                        resultSet.getString(3) + "(" +
                        resultSet.getInt(1) + ") - " +
                        NumberFormat.getCurrencyInstance().format(BigDecimal.valueOf(resultSet.getDouble(4))) + "\n");
                sb.append("\n");
            }
            mostRevenueLabel.setText(sb.toString());

        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }
}
