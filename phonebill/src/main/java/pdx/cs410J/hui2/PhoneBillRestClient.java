package pdx.cs410J.hui2;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.URLConnection.guessContentTypeFromStream;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send dictionary entries.
 */
public class PhoneBillRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

    private final String url;

    /**
     * check the format of the host and port is correct
     * @param hostName  the host name of the server
     * @param port  the port number of the server
     */
    public PhoneBillRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * doPost function to add a new customer to post from the server
     * @param customer  the customer name
     * @param call  object of phone call
     * @return  If url doesn't exist return nothing
     */
    public Response addNewCustomer(String customer, PhoneCall call)
    {
        try {
            return post(this.url, "customer", customer, "caller", call.callerNumber, "callee", call.calleeNumber,
                    "startTime", call.getStartTimeString(), "endTime", call.getEndTimeString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Prints all of the customer that is requesting from the server
     * @param customer  the customer name that we are requesting
     * @return return nothing if the customer doesn't exist
     */
    public Response printAll(String customer)
    {
        try {
            return get(this.url, "customer", customer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Request all of the information from the server that is between the start and end time of a specific customer.
     * @param customer the customer name that is request for a search
     * @param startTime the start time that is request for a search
     * @param endTime the end time that is reqeust for a search
     * @return return nothing if there is nothing on the server
     */
    public Response printSearch(String customer, String startTime, String endTime)
    {
        try {
            return post(this.url, "customer", customer, "startTime", startTime, "endTime", endTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
