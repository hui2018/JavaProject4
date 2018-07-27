package pdx.cs410J.hui2;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import java.util.Locale;


public class PhoneCall extends AbstractPhoneCall implements Comparable<PhoneCall> {
  String callerNumber;
  String calleeNumber;
  String startTime;
  String endTime;
  public Date dateStartTime;
  public Date dateEndTime;
  DateFormat ShortDateFormat;

  PhoneCall(String callerNumber, String calleeNumber, String startTime, String endTime)
  {
    this.callerNumber = callerNumber;
    this.calleeNumber = calleeNumber;
    this.startTime = startTime;
    this.endTime = endTime;
    formatter(startTime, endTime);
  }

  @Override
  public String getCaller() {
    return callerNumber;
  }

  @Override
  public String getCallee() {
    return calleeNumber;
  }

  @Override
  public String getStartTimeString() {
    return startTime;
  }

  @Override
  public String getEndTimeString() {
    return endTime;
  }
  @Override
  public Date getStartTime()
  {
    return dateStartTime;
  }
  @Override
  public Date getEndTime()
  {
    return dateEndTime;
  }

  public Date formatter(String startTime, String endTime){
    SimpleDateFormat startFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
    try {
      dateStartTime = startFormat.parse(startTime);
      dateEndTime = startFormat.parse(endTime);
    } catch (ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  @Override
  public int compareTo(PhoneCall o) {
    int startCompare = this.getStartTime().compareTo(o.getStartTime());
    if(startCompare != 0)
      return startCompare;
    int phoneCallerCompare = this.getCaller().compareTo(o.getCaller());
    if(phoneCallerCompare != 0)
      return phoneCallerCompare;
    return 0;
  }

  public String getDuration(Date startTime, Date endTime)
  {
    long duration;
    duration = endTime.getTime() -startTime.getTime();
    long minutes;
    minutes = duration / (60*1000)%60;
    long hour;
    hour = duration /(60*60*1000)%24;
    if(hour == 0)
      return ""+minutes;
    else
      return ""+hour+":"+minutes;
  }
}
