package pdx.cs410J.hui2;

import com.google.common.annotations.VisibleForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>PhoneBill</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple dictionary of words
 * and their definitions.
 */
public class PhoneBillServlet extends HttpServlet
{
    static final String WORD_PARAMETER = "word";
    static final String DEFINITION_PARAMETER = "definition";

    private final Map<String, String> dictionary = new HashMap<>();
    private final Map<String, PhoneBill> collectionOfCalls = new HashMap<String, PhoneBill>();
    Collection<PhoneCall> phoneCalls = null;
    private PhoneBill bill = null;

    /**
     * Handles an HTTP GET request from a client by writing the definition of the
     * word specified in the "word" HTTP parameter to the HTTP response.  If the
     * "word" parameter is not specified, all of the entries in the dictionary
     * are written to the HTTP response.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );
        String customer = getParameter( "customer", request );
        if (customer == null) {
            missingRequiredParameter( response, "customer" );
            return;
        }
        PrintWriter pw = response.getWriter();
        if(collectionOfCalls.get(customer) == null)
        {
            System.out.println("Customer does not exist in server");
            pw.println("Customer does not exist in server");
        }
        else
        {
            phoneCalls = collectionOfCalls.get(customer).getPhoneCalls();
            pw.println("Customer Name    " + "Caller's Phone number   " + "Callee's phone number         " + "Start Time                  " +
                    "End Time                " + "Duration (hh:mm)");
            for(PhoneCall call: phoneCalls)
            {
                pw.println("     "+customer + "            "+ call.getCaller()+"            "+call.getCallee()+ "         " +
                        call.getStartTimeString()+"          "+call.getEndTimeString()+"             "+
                        call.getDuration(call.getStartTime(),call.getEndTime()));
            }
            pw.flush();
        }

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Handles an HTTP POST request by storing the dictionary entry for the
     * "word" and "definition" request parameters.  It writes the dictionary
     * entry to the HTTP response.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );
        String customer = getParameter( "customer", request );
        if (customer == null) {
            missingRequiredParameter( response, "customer" );
            return;
        }
        String startTime = getParameter( "startTime", request );
        if (startTime == null) {
            missingRequiredParameter( response, "startTime" );
            return;
        }
        String endTime = getParameter( "endTime", request );
        if (endTime == null) {
            missingRequiredParameter( response, "endTime" );
            return;
        }

        String caller = getParameter( "caller", request );
        String callee = getParameter( "callee", request );
        PrintWriter pw = response.getWriter();
        if(caller == null && callee == null)
        {
            if(collectionOfCalls.get(customer) == null)
            {
                System.out.println("Customer does not exist in server");
                pw.println("Customer does not exist in server");
            }
            else
            {
                phoneCalls = collectionOfCalls.get(customer).getPhoneCalls();
                pw.println("Customer Name    " + "Caller's Phone number   " + "Callee's phone number         " + "Start Time                  " +
                        "End Time                " + "Duration (hh:mm)");
                for(PhoneCall call: phoneCalls) {
                    if(compareDate(startTime, endTime, call.getStartTimeString()))
                    {
                        pw.println("     " + customer + "            " + call.getCaller() + "            " + call.getCallee() + "         " +
                                call.getStartTimeString() + "          " + call.getEndTimeString() + "             " +
                                call.getDuration(call.getStartTime(), call.getEndTime()));
                    }
                }
            }
            pw.flush();
            return;
        }

        if (caller == null) {
            missingRequiredParameter( response, "caller" );
            return;
        }
        if (callee == null) {
            missingRequiredParameter( response, "callee" );
            return;
        }

        if(collectionOfCalls != null && collectionOfCalls.get(customer) != null) {
            bill = collectionOfCalls.get(customer);
            bill.addPhoneCall(new PhoneCall(caller, callee, startTime, endTime));
            collectionOfCalls.put(customer, bill);
            System.out.println("Adding new phoneCall to existing customer");
        }
        else
        {
            collectionOfCalls.put(customer, new PhoneBill(customer, new PhoneCall(caller, callee, startTime, endTime)));
            System.out.println("Adding new customer");
        }

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Handles an HTTP DELETE request by removing all dictionary entries.  This
     * behavior is exposed for testing purposes only.  It's probably not
     * something that you'd want a real application to expose.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        this.dictionary.clear();

        PrintWriter pw = response.getWriter();
        pw.println(Messages.allDictionaryEntriesDeleted());
        pw.flush();

        response.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
            throws IOException
    {
        String message = Messages.missingRequiredParameter(parameterName);
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || "".equals(value)) {
            return null;

        } else {
            return value;
        }
    }

    public static boolean compareDate(String startDate, String endDate, String startCollection)
    {
        SimpleDateFormat startFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        Date start = null;
        Date end = null;
        Date startCol = null;
        long duration1;
        long duration2;
        try {
            start = startFormat.parse(startDate);
            end = startFormat.parse(endDate);
            startCol = startFormat.parse(startCollection);
            duration1 = startCol.getTime() - start.getTime();
            duration2 = end.getTime() - startCol.getTime();
            if(duration1 < 0)
                return false;
            if(duration2 < 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return true;
    }

}
