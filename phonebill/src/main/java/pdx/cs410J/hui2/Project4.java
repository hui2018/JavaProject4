package pdx.cs410J.hui2;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.PrintStream;
import java.net.UnknownServiceException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 */
public class Project4 {
    public static final String MISSING_ARGS = "Missing command line arguments";
    public static boolean printOpt = false; //only one
    public static boolean searchOpt = false; //only on search start time
    public static boolean printAll = false; //all of that one specific customer
    public static String hostName;
    public static String portString;

    /**
     * In the main function we are checking all of the parameter that are passed in are in the right format, the correct
     * number of arguments, and that start time is before end time.
     * @param args The array of arguments that are passed in from the command line.
     * @throws UnknownServiceException Throw an error if the connection to the server doesn't work
     */
    public static void main(String... args) throws UnknownServiceException {
        checkReadMe(args);

        ArrayList listOfArgs = new ArrayList<String>(Arrays.asList(args));
        listOfArgs = removeOption(listOfArgs);
        checkArgs(listOfArgs);

        String customerName = null;
        String callerNumber = null;
        String calleeNumber = null;
        String startTime = null;
        String endTime = null;
        boolean addOne = false;

        if(printAll)
        {
            customerName = (String) listOfArgs.get(0);
            checkName(customerName);
        }
        else if(searchOpt)
        {
            customerName = (String) listOfArgs.get(0);
            startTime = (String) listOfArgs.get(1) + " " + (String) listOfArgs.get(2) + " " + (String) listOfArgs.get(3);
            endTime = (String) listOfArgs.get(4) + " " + (String) listOfArgs.get(5) + " " + (String) listOfArgs.get(6);
            checkName(customerName);
            checkStartTime((String) listOfArgs.get(1),(String) listOfArgs.get(2));
            checkEndTime((String) listOfArgs.get(4),(String) listOfArgs.get(5));
            checkTimeLabel((String) listOfArgs.get(3),(String) listOfArgs.get(6));
            compareDate(startTime, endTime);

        }
        else
        {
            customerName = (String) listOfArgs.get(0);
            callerNumber = (String) listOfArgs.get(1);
            calleeNumber = (String) listOfArgs.get(2);
            startTime = (String) listOfArgs.get(3) + " " + (String) listOfArgs.get(4) + " " + (String) listOfArgs.get(5);
            endTime = (String) listOfArgs.get(6) + " " + (String) listOfArgs.get(7) + " " + (String) listOfArgs.get(8);

            checkStartTime((String) listOfArgs.get(3),(String) listOfArgs.get(4));
            checkEndTime((String) listOfArgs.get(6),(String) listOfArgs.get(7));
            checkName(customerName);
            checkCallerPhone(callerNumber);
            checkCalleePhone(calleeNumber);
            checkTimeLabel((String) listOfArgs.get(5),(String) listOfArgs.get(8));
            compareDate(startTime, endTime);
            addOne = true;
        }

        int port;
        try {
            port = Integer.parseInt( portString );
            
        } catch (NumberFormatException ex) {
            System.out.println("Port \"" + portString + "\" must be an integer");
            return;
        }

        PhoneBillRestClient client = new PhoneBillRestClient(hostName, port);

        HttpRequestHelper.Response response = null;
        if(addOne)
        {
            response = client.addNewCustomer(customerName, new PhoneCall(callerNumber,calleeNumber,
                    startTime, endTime));
            if(printOpt)
            {
                PhoneBill single = new PhoneBill(customerName, new PhoneCall(callerNumber, calleeNumber, startTime, endTime));
                ArrayList<PhoneCall> call = (ArrayList<PhoneCall>) single.getPhoneCalls();
                System.out.println("\nPrint option detected: \n");
                System.out.println("Customer Name    " + "Caller's Phone number   " + "Callee's phone number         " + "Start Time                  " +
                        "End Time                " + "Duration (dd:hh:mm)");
                System.out.println("     "+single.getCustomer() + "            " + call.get(0).getCaller() + "            " +
                        call.get(0).getCallee() + "         " + call.get(0).getStartTimeString() + "          " + call.get(0).getEndTimeString() +
                        "             " + call.get(0).getDuration(call.get(0).getStartTime(), call.get(0).getEndTime()));
            }
        }
        if(searchOpt)
        {
            //pass in the start and end time to search for
            response = client.printSearch(customerName, startTime, endTime);
            if(response == null)
                System.out.println("Cannot find the customer with given time");
            else
                System.out.println(response.getContent());
        }
        if(printAll)
        {
            //pass in customer to search for that specific customer to print out
            response = client.printAll(customerName);
            System.out.println(response.getContent());
        }
        System.out.println("\n");
        System.exit(0);
    }

    /**
     * check the number of arguments that are being passed in
     * @param numArgs array of arguments passed in from the command line
     */
    public static void checkArgs(ArrayList numArgs) {
        if(searchOpt)
        {
            if(numArgs.size() == 0)
            {
                System.err.println("Missing command line arguments");
                System.exit(1);
            }
            else if (numArgs.size() > 7) {
                System.err.println("Too many arguments");
                System.exit(1);
            } else if (numArgs.size() != 7) {
                System.err.println("Not enough arguments");
                System.exit(1);
            }
            return;
        }
        if(printOpt)
        {
            if(numArgs.size() != 9)
            {
                System.err.println("Missing argument to print");
                System.exit(1);
            }
        }
        else {
            if (numArgs.size() == 0) {
                System.err.println("Missing command line arguments");
                System.exit(1);
            }
            else if(numArgs.size() == 1)
            {
                printAll = true;
            }
            else if (numArgs.size() > 9) {
                System.err.println("Too many arguments");
                System.exit(1);
            } else if (numArgs.size() != 9) {
                System.err.println("Not enough arguments");
                System.exit(1);
            }
        }
    }

    /**
     * Remove all of the "-" parameters then we return back the array list
     * @param listOfArgs array of parameter passed in from the command line
     * @return we are returning the array of arguments after we remove any of the "-" arguments
     */
    private static ArrayList removeOption(ArrayList listOfArgs) {
        if(listOfArgs.contains("-print"))
        {
            if(listOfArgs.contains("-search")) {
                System.err.println("Cannot have both search and print at the same time");
                System.exit(1);
            }
            listOfArgs.remove(listOfArgs.indexOf("-print"));
            printOpt = true;
        }
        if(!listOfArgs.contains("-host"))
        {
            if(!listOfArgs.contains("-port"))
            {
                System.out.println("Missing host and port");
                System.exit(1);
            }
        }
        if(!listOfArgs.contains("-port"))
        {
            if(!listOfArgs.contains("-host"))
            {
                System.out.println("Missing host and port");
                System.exit(1);
            }
        }
        if(listOfArgs.contains("-host"))
        {
            if(!listOfArgs.contains("-port"))
            {
                System.err.println("Missing port");
                System.exit(1);
            }
        }
        if(listOfArgs.contains("-port"))
        {
            if(!listOfArgs.contains("-host"))
            {
                System.err.println("Missing host");
                System.exit(1);
            }
        }
        if(listOfArgs.contains("-search"))
        {
            listOfArgs.remove(listOfArgs.indexOf("-search"));
            searchOpt = true;
        }
        int counter = 0;
        counter = listOfArgs.indexOf("-host");
        listOfArgs.remove(listOfArgs.indexOf("-host"));
        hostName = (String) listOfArgs.get(counter);
        listOfArgs.remove(listOfArgs.get(counter));

        counter = listOfArgs.indexOf("-port");
        listOfArgs.remove(listOfArgs.indexOf("-port"));
        portString = (String) listOfArgs.get(counter);
        listOfArgs.remove(listOfArgs.get(counter));
        return listOfArgs;
    }

    /**
     * check the response code
     * @param code Check if the code is valid or not
     * @param response the response from the client from the server
     */
    private static void checkResponseCode( int code, HttpRequestHelper.Response response )
    {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                                response.getCode(), response.getContent()));
        }
    }

    /**
     * check the error of print
     * @param message the string with the incorrect parameter
     */
    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Check if there is a -readme, if there just print out the document and exit
     * @param args array of string passed in from the command line.
     */
    private static void checkReadMe(String[] args)
    {
        for(int i = 0; i<args.length; ++i)
        {
            if (args[i].equals("-README"))
            {
                readMe();
                System.exit(1);
            }
        }
    }

    /**
     * Print out the read me
     */
    private static void readMe()
    {
        System.out.println("Name: Hui Yu Sim \nProject: 4 A REST-ful Phone Bill Web Service\n\n" +
                "The Purpose of this programming assignment is to create a web service that\n" +
                "has customer name and consists of multiple phone calls.\n" +
                "The program will take in arguments from the command line and check if the arguments are correct.\n" +
                "Please follow the following steps to insure program will run correctly.\n");
        System.out.println("usage: java edu.pdx.cs410J.<login-id>.Project2 [options] <args>\n" +
                "args are (in this order):\n" +
                "\tcustomer           Person whose phone bill we’re modeling\n" +
                "\tcallerNumber       Phone number of caller\n" +
                "\tcalleeNumber       Phone number of person who was called\n" +
                "\tstartTime          Date and time call began\n" +
                "\tendTime            Date and time call ended\n" +
                "options are (options may appear in any order):\n" +
                "\t-host hostname     Host computer on which the server runs\n" +
                "\t-port port         Port on which the server is listening\n" +
                "\t-search            Phone calls should be searched for\n" +
                "\t-print             Prints a description of the new phone call\n" +
                "\t-README            Prints a README for this project and exits\n" +
                "Date and time should be in the format: mm/dd/yyyy hh:mm");
    }

    /**
     * Check that the customer name can only contain letters and no numbers or symbols.
     * The name will also allow double quote and a space so that it can have a full name
     * when entering the customer name.
     * @param customerName  Name of the customer stored in a string
     */
    private static void checkName(String customerName)
    {
        if(!customerName.matches("[a-z A-Z 0-9]+"))
        {
            System.err.println("Invalid customer name");
            System.exit(1);
        }
    }

    /**
     * We want to check if the phone number of caller is in the correct format
     * if the format is incorrect then we just want to tell the user that
     * the callee phone number is invalid.
     * @param phoneNumber caller's phone number that is stored in a string and needs to be
     *                    in a format of nnn-nnn-nnnn
     */
    private static void checkCallerPhone(String phoneNumber)
    {
        if(!phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}$"))
        {
            System.err.println("Invalid caller phone number");
            System.exit(1);
        }
    }

    /**
     * We want to check if the phone number of callee is in the correct format
     * if the format is incorrect then we just want to tell the user that
     * the callee phone number is invalid.
     * @param phoneNumber callee's phone number that is stored in a string and needs to be
     *                    in a format of nnn-nnn-nnnn
     */
    private static void checkCalleePhone(String phoneNumber)
    {
        if(!phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}$"))
        {
            System.err.println("Invalid callee phone number");
            System.exit(1);
        }
    }

    /**
     * In this function we are checking if the start date and start time is in the format that we want.
     * If the format is not what we want then we want to tell the user what the problem is
     * then exit out of the program.
     * @param startDate start date that is stored in a string in the format of nn/nn/nnnn
     * @param startTime start time that is stored in a string in the format of nn:nn
     * Where the "n" are integers.
     */
    private static void checkStartTime(String startDate, String startTime)
    {
        if(!startDate.matches("((0?[1-9])|(1?[012]))/(0?[1-9]|[12][0-9]|3[01])/[0-9]{2}([0-9]{2})"))
        {
            System.err.println("Invalid start date");
            System.exit(1);
        }
        else if(!startTime.matches("([0]?[1-9]|1[0-2]):[0-5][0-9]"))
        {
            System.err.println("Invalid start time");
            System.exit(1);
        }
    }

    /**
     * In this function we are checking if the end date and end time is in the format that we want.
     * If the format is not what we want then we want to tell the user what the problem is
     * then exit out of the program.
     * @param endDate end date that is stored in a string in the format of nn/nn/nnnn
     * @param endTime end time that is stored in a string in the format of nn:nn
     * Where the "n" are integers.
     */
    private static void checkEndTime(String endDate, String endTime)
    {
        if(!endDate.matches("((0?[1-9])|(1?[012]))/(0?[1-9]|[12][0-9]|3[01])/(19|20)([0-9]{2})"))
        {
            System.err.println("Invalid end date");
            System.exit(1);
        }
        else if(!endTime.matches("([0]?[1-9]|1[0-2]):[0-5][0-9]"))
        {
            System.err.println("Invalid end time");
            System.exit(1);
        }
    }
    /**
     * THis is to check that the time label can only be am or pm
     * @param startLabel  Start time's am or pm
     * @param endLabel  end time's am or pm
     */
    public static void checkTimeLabel(String startLabel, String endLabel)
    {
        if(!startLabel.contains("am"))
        {
            if(!startLabel.contains("pm"))
                System.out.println("1 Failed");
        }
        else if(!startLabel.contains("pm"))
        {
            if(!startLabel.contains("am"))
                System.err.println("2 failed");
        }
        else if (!endLabel.contains("am"))
        {
            if(!endLabel.contains("pm"))
                System.err.println("3 end failed");
        }
        else if(!endLabel.contains("pm"))
        {
            if(!endLabel.contains("am"))
                System.err.println("4 end failed");
        }
    }

    /**
     * Compare the date of start and end time
     * @param startDate the start date with correct format passed in from the command line
     * @param endDate the end date with the correct format passed in from the command line
     */
    public static void compareDate(String startDate, String endDate)
    {
        SimpleDateFormat startFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        Date start = null;
        Date end = null;
        long duration;
        try {
            start = startFormat.parse(startDate);
            end = startFormat.parse(endDate);
            duration = end.getTime() - start.getTime();
            if(duration < 0) {
                System.err.println("Start time is after end time, please modified the date/time");
                System.exit(1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}