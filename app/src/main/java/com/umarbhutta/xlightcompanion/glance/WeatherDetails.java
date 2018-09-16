package com.umarbhutta.xlightcompanion.glance;

/**
 */
public class WeatherDetails {

    private String mLocation;
    private String mIcon;
    private double mTempF;
    private int mTempC;
    private double mApparentTempF;
    private int mApparentTempC;
    private int mHumidity;
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

    public int getTemp(final String unit) {
        if (unit == "fahrenheit") {
            return (int) mTempF;
        } else {
            return mTempC;
        }
    }

    public void setTemp(final double mTemp) {
        this.mTempF = mTemp;

        mTempC = (int) ((mTempF - 32.0) * (5.0 / 9.0) + 0.5);
    }

    public int getmHumidity() {
        return mHumidity;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setHumidity(final int humidity) {
        this.mHumidity = humidity;
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
