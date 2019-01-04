package com.umarbhutta.xlightcompanion.glance;

/**
 */
public class WeatherDetails {

    private String mLocation;
    private String mIcon;
    private double mTempF;
    private int mTempC;
    private double minTempF;
    private int minTempC;
    private double maxTempF;
    private int maxTempC;
    private String summaryHour;
    private int humidity;
    private double mApparentTempF;
    private int mApparentTempC;
    private String summary;

    public WeatherDetails() {
        super();
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(final String location) {
        mLocation = location;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(final String mIcon) {
        this.mIcon = mIcon;
    }

    public void setMin(final double mTemp) {
        this.minTempF = mTemp;
        this.minTempC = (int) ((mTemp - 32.0) * (5.0 / 9.0) + 0.5);
    }

    public int getMin(final String unit) {
        if (unit == "fahrenheit") {
            return (int) this.minTempF;
        } else {
            return this.minTempC;
        }
    }

    public void setMax(final double mTemp) {
        this.maxTempF = mTemp;
        this.maxTempC = (int) ((mTemp - 32.0) * (5.0 / 9.0) + 0.5);
    }

    public int getMax(final String unit) {
        if (unit == "fahrenheit") {
            return (int) this.maxTempF;
        } else {
            return this.maxTempC;
        }
    }

    public void setHumidity(final double humidity) {
        this.humidity = (int) (humidity * 100);
    }

    public int getHumidity() {
        return this.humidity;
    }

    public void setSummaryHour(final String summary) {
        this.summaryHour = summary;
    }

    public String getSummaryHour() {
        return this.summaryHour;
    }


    public int getTemp(final String unit) {
        if (unit == "fahrenheit") {
            return (int) this.mTempF;
        } else {
            return this.mTempC;
        }
    }

    public void setTemp(final double mTemp) {
        this.mTempF = mTemp;

        this.mTempC = (int) ((this.mTempF - 32.0) * (5.0 / 9.0) + 0.5);
    }


    public String getSummary() {
        return this.summary;
    }


    public int getApparentTemp(final String unit) {
        if (unit == "fahrenheit") {
            return (int) mApparentTempF;
        } else {
            return mApparentTempC;
        }
    }

    public void setApparentTemp(final double mTemp) {
        mApparentTempF = mTemp;
        mApparentTempC = (int) ((mTempF - 32.0) * (5.0 / 9.0) + 0.5);
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }
}
