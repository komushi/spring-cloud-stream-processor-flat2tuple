package io.pivotal.spring.cloud.stream.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by lei_xu on 6/11/16.
 */
@ConfigurationProperties
public class Flat2TupleProcessorProperties {
    // The cells for this query are squares of 500 m X 500 m
    private static final Double EDGE_LENGTH = new Double(500);
    // . The overall grid expands 150km south and 150km east from cell 1.1 with the cell 300.300 being the last cell in the grid.
    private static final Integer CELLS = 300;

    // Barryville - Center of the Cell_1_1
    private static final Double START_CENTER_LATITUDE = 41.474937;
    private static final Double START_CENTER_LONGITUDE = -74.913585;

    // The upper left corner of the Cell_1_1
    private static final Double START_POINT_LATITUDE = new Double(41.479428420783364);
    private static final Double START_POINT_LONGITUDE = new Double(-74.91958021476788);


    /**
     * The overall grid expands 150km south and 150km east from cell 1.1 with the cell 300.300 being the last cell in the grid.
     */
    private Integer cells;

    /**
     * The cells for this query are squares of 500 m X 500 m
     */
    private Double edgeLength;

    private Double startLatitude;
    private Double startLongitude;

    public Integer getCells() {
        if (this.cells == null) {
            return CELLS;
        }
        else {
            return this.cells;
        }
    }

    public void setCells(Integer cells) {
        this.cells = cells;
    }

    public void setEdgeLength(Double edgeLength) {
        this.edgeLength = edgeLength;
    }

    public Double getEdgeLength() {
        if (this.edgeLength == null) {
            return EDGE_LENGTH;
        }
        else {
            return this.edgeLength;
        }
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLatitude() {
        if (this.startLatitude == null) {
            return START_POINT_LATITUDE;
        }
        else {
            return this.startLatitude;
        }
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStartLongitude() {
        if (this.startLongitude == null) {
            return START_POINT_LONGITUDE;
        }
        else {
            return this.startLongitude;
        }
    }
}
