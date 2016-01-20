package com.example.susha.sensor_collect.Sensors;


        import java.text.SimpleDateFormat;
        import java.util.Date;

/**
 * Created by Sakuya Izayoi on 3/24/2015.
 */
class inertiaData {

    private String sensor;
    private String[] values;
    private Date timestamp;

    public inertiaData () {
        timestamp = new Date();
        values = new String[3];
    }

    public inertiaData (float ... log) {
        values = new String[3];
        values[0] = Float.toString(log[0]);
        values[1] = Float.toString(log[1]);
        values[2] = Float.toString(log[2]);
        sensor = "null";
    }

    public inertiaData (String type, Date time, float ... log) {
        values = new String[3];
        values[0] = Float.toString(log[0]);
        values[1] = Float.toString(log[1]);
        values[2] = Float.toString(log[2]);
        sensor = type;
        timestamp = time;
    }

    public inertiaData (String ... log) {
        values = new String[3];
        values[0] = log[0];
        values[1] = log[1];
        values[2] = log[2];
        sensor = "null";
    }

    public inertiaData (String type, Date time, String ... log) {
        values = new String[3];
        values[0] = log[0];
        values[1] = log[1];
        values[2] = log[2];
        sensor = type;
        timestamp = time;
    }

    public void setSensorType(String type) {
        sensor = type;
    }

    public String getSensorType() {
        return sensor;
    }

    public void setTimestamp(Date time) {
        timestamp = time;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder export = new StringBuilder();
        SimpleDateFormat convert = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:SSSS");
        convert.format(timestamp);
        export.append("Time : "); export.append(convert);
        export.append("\t\"").append(sensor);
        export.append("\tX(1): "); export.append(values[0]);
        export.append("\tY(2): "); export.append(values[1]);
        export.append("\tZ(3): "); export.append(values[2]);
        export.append("\n");


        return export.toString();
    }
}
