package utils;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateTime implements Comparable<DateTime> {
    String timeStamp;
    LocalDateTime localDateTime;
    public static DateTimeFormatter timeStamp_24_Format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter dateTime_12Hformat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
    public static DateTimeFormatter databaseDateFormat = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
    public static DateTimeFormatter normalDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static DateTime from_timeStamp(String timeStamp){
        LocalDateTime localDateTime = LocalDateTime.parse(timeStamp.substring(0, 19), timeStamp_24_Format);
        return new DateTime(localDateTime);
    }

    public DateTime() {
        localDateTime = LocalDateTime.now();
    }
    public void add_to_date(int years,int months,int days){
        this.localDateTime=this.localDateTime.plusYears(years);
        this.localDateTime=this.localDateTime.plusMonths(months);
        this.localDateTime=this.localDateTime.plusDays(days);
    }


    public static DateTime getDateTimeFromDatePicker(DatePicker datePicker, LocalTime time) {

        return new DateTime(LocalDateTime.of(datePicker.getValue(), time));
    }
    public static DateTime fromDate(String date)
    {
        return new DateTime(LocalDateTime.of(LocalDate.parse(date,normalDateFormat),LocalTime.MIN));
    }
    public LocalDate getLocalDate() {
        return localDateTime.toLocalDate();
    }

    public DateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
    public void toMinTime()
    {
        this.localDateTime=LocalDateTime.of(this.localDateTime.toLocalDate(),LocalTime.MIN);

    }
    public void toMaxTime()
    {
        this.localDateTime=LocalDateTime.of(this.localDateTime.toLocalDate(),LocalTime.MAX);
    }

    public String datetime_12h() {
        return localDateTime.format(dateTime_12Hformat);
    }

    public String getTimeStamp() {
        return localDateTime.format(timeStamp_24_Format);
    }

    public String get_Date() {
        return localDateTime.toLocalDate().format(normalDateFormat);
    }
    public String getDatabaseDate() {
        return localDateTime.toLocalDate().format(databaseDateFormat);
    }

    public static DateTime getDateFromStrDateAndTime(String dataBaseDate, int hour, int minute, String PmOrAm) {
        String hourFormated;
        String minuteFormated;
        if (PmOrAm.equals("PM"))
            hour = hour + 12;
        if (hour < 10)
            hourFormated = "0" + Integer.toString(hour);
        else
            hourFormated = Integer.toString(hour);
        if (minute < 10)
            minuteFormated = "0" + Integer.toString(minute);
        else
            minuteFormated = Integer.toString(minute);

        String timeStamp = dataBaseDate + " " + hourFormated + ":" + minuteFormated + ":00";
        LocalDateTime localDateTime = LocalDateTime.parse(timeStamp, timeStamp_24_Format);
        return new DateTime(localDateTime);
    }


    public String dateCurrentToNow() {
        LocalDate now = LocalDate.now();
        LocalDate fromdate = localDateTime.toLocalDate();
        Period period = Period.between(fromdate, now);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        String output = "";
        if (days != 0)
            output += " " + "(ايام " + days + ")";
        if (months != 0)
            output += " " + "(شهور " + months + ")";
        if (years != 0)
            output += " " + "(سنين " + years + ")";

        //2020-04-10 16:55:47.002
        return output;
    }


    @Override
    public int compareTo(DateTime o) {
        return this.localDateTime.compareTo(o.localDateTime);
    }

    @Override
    public String toString() {
        return datetime_12h();
    }

}
