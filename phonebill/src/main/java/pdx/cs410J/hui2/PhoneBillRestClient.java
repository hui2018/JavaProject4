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

    public PhoneBillRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    public Response printAllKeysAndValues() throws IOException {
        return get(this.url);
    }

    public Response getSearchValues(PhoneBill bill)
    {

        return null;
    }

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
    public Response printAll(String customer)
    {
        try {
            return get(this.url, "customer", customer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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
