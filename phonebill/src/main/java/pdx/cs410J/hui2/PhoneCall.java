package pdx.cs410J.hui2;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhoneCall extends AbstractPhoneCall implements Comparable<PhoneCall> {
  private String callerNumber;
  private String calleeNumber;
  private String startTime;
  private String endTime;
  public Date dateStartTime;
  public Date dateEndTime;
    /**
     * Constructor that takes in the phone number and time of the phone call
     * @param callerNumber  The phone number of the caller
     * @param calleeNumber  The phone number of the callee
     * @param startTime     The start date and time of the call
     * @param endTime       The end date and time of the call
     */
  PhoneCall(String callerNumber, String calleeNumber, String startTime, String endTime)
  {
    this.callerNumber = callerNumber;
    this.calleeNumber = calleeNumber;
    this.startTime = startTime;
    this.endTime = endTime;
    formatter(startTime, endTime);
  }
    /**
     *
     * @return it returns the caller's phone number
     */
  @Override
  public String getCaller() {
    return callerNumber;
  }

    /**
     *
     * @return it returns the callee's phone number
     */
  @Override
  public String getCallee() {
    return calleeNumber;
  }

    /**
     *
     * @return It returns the start date and time
     */
  @Override
  public String getStartTimeString() {
    return startTime;
  }

    /**
     *
     * @return It returns the end date and time
     */
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
}
