package org.ramki.bean;

import de.progra.charting.ChartEncoder;
import de.progra.charting.ChartUtilities;
import de.progra.charting.CoordSystem;
import de.progra.charting.DefaultChart;
import de.progra.charting.model.DefaultChartDataModel;
import de.progra.charting.model.DefaultDataSet;
import de.progra.charting.render.LineChartRenderer;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ramki
 */
@ManagedBean
@SessionScoped
public class DemoBean {

    int width = 640;
    int height = 480;
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    public DemoBean() {
        int[] quadr = {0, 1, 4, 9, 16, 25, 36};
        int[] exp = {1, 2, 4, 8, 16, 32, 64};
        double[] columns = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0};

        // Creating a data set array
        DefaultDataSet[] ds = new DefaultDataSet[3];

        // Filling all DataSets
        ds[0] = new DefaultDataSet(ChartUtilities.transformArray(new int[]{0, 6}),
                ChartUtilities.transformArray(new double[]{0.0, 6.0}),
                CoordSystem.FIRST_YAXIS,
                "Linear Growth");

        ds[1] = new DefaultDataSet(ChartUtilities.transformArray(quadr),
                ChartUtilities.transformArray(columns),
                CoordSystem.FIRST_YAXIS,
                "Quadratic Growth");

        ds[2] = new DefaultDataSet(ChartUtilities.transformArray(exp),
                ChartUtilities.transformArray(columns),
                CoordSystem.FIRST_YAXIS,
                "Exponential Growth");

        String title = "Growth Factor Comparison";



        DefaultChartDataModel data = new DefaultChartDataModel(ds);

        data.setAutoScale(true);

        DefaultChart c = new DefaultChart(data, title, DefaultChart.LINEAR_X_LINEAR_Y);

          c.addChartRenderer(new LineChartRenderer(c.getCoordSystem(),data), 1);

        c.setBounds(new Rectangle(0, 0, width, height));
        Graphics2D gd=bi.createGraphics();
        c.render(gd);

//        try {
//            ChartEncoder.createPNG(new FileOutputStream(System.getProperty("user.home") + "/fifth.png"), c);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    public String pdf() throws JRException, IOException {

       
        String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/Charts.jasper");

        Map parameter = new HashMap();
        parameter.put("chartImage", bi);

        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, parameter, new JREmptyDataSource(1));
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=report.pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        FacesContext.getCurrentInstance().responseComplete();

        return null;
    }
}
