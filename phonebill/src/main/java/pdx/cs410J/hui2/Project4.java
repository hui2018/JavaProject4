package pdx.cs410J.hui2;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";
    public static boolean printOpt = false;
    public static boolean searchOpt = false;
    public static String hostName;
    public static String portString;
    public static void main(String... args) {
        checkReadMe(args);

        ArrayList listOfArgs = new ArrayList<String>(Arrays.asList(args));
        listOfArgs = removeOption(listOfArgs);
        checkArgs(listOfArgs);


        String word = null;
        String definition = null;
        //word = (String) listOfArgs.get(4);
        //definition = (String) listOfArgs.get(5);

        int port;
        try {
            port = Integer.parseInt( portString );
            
        } catch (NumberFormatException ex) {
            usage("Port \"" + portString + "\" must be an integer");
            return;
        }

        PhoneBillRestClient client = new PhoneBillRestClient(hostName, port);

        String message;
        try {
            if (word == null) {
                // Print all word/definition pairs
                Map<String, String> dictionary = client.getAllDictionaryEntries();
                StringWriter sw = new StringWriter();
                Messages.formatDictionaryEntries(new PrintWriter(sw, true), dictionary);
                message = sw.toString();

            } else if (definition == null) {
                // Print all dictionary entries
                message = Messages.formatDictionaryEntry(word, client.getDefinition(word));

            } else {
                // Post the word/definition pair
                client.addDictionaryEntry(word, definition);
                message = Messages.definedWordAs(word, definition);
            }

        } catch ( IOException ex ) {
            error("While contacting server: " + ex);
            return;
        }

        System.out.println(message);

        System.exit(0);
    }

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
        }
        else {
            if (numArgs.size() == 0) {
                System.err.println("Missing command line arguments");
                System.exit(1);
            } else if (numArgs.size() > 9) {
                System.err.println("Too many arguments");
                System.exit(1);
            } else if (numArgs.size() != 9) {
                System.err.println("Not enough arguments");
                System.exit(1);
            }
        }
    }

    private static ArrayList removeOption(ArrayList listOfArgs) {
        if(listOfArgs.contains("-print"))
        {
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
            int counter = 0;
            counter = listOfArgs.indexOf("-host");
            listOfArgs.remove(listOfArgs.indexOf("-host"));
            hostName = (String) listOfArgs.get(counter);
            listOfArgs.remove(listOfArgs.get(counter));

            counter = listOfArgs.indexOf("-port");
            listOfArgs.remove(listOfArgs.indexOf("-port"));
            portString = (String) listOfArgs.get(counter);
            listOfArgs.remove(listOfArgs.get(counter));
        }
        if(listOfArgs.contains("-port"))
        {
            if(!listOfArgs.contains("-host"))
            {
                System.err.println("Missing host");
                System.exit(1);
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
        }
        if(listOfArgs.contains("-search"))
        {
            listOfArgs.remove(listOfArgs.indexOf("-search"));
            searchOpt = true;
        }
        return listOfArgs;
    }

    /**
     * Makes sure that the give response has the expected HTTP status code
     * @param code The expected status code
     * @param response The response from the server
     */
    private static void checkResponseCode( int code, HttpRequestHelper.Response response )
    {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                                response.getCode(), response.getContent()));
        }
    }

    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     * @param message An error message to print
     */
    private static void usage( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port [word] [definition]");
        err.println("  host         Host of web server");
        err.println("  port         Port of web server");
        err.println("  word         Word in dictionary");
        err.println("  definition   Definition of word");
        err.println();
        err.println("This simple program posts words and their definitions");
        err.println("to the server.");
        err.println("If no definition is specified, then the word's definition");
        err.println("is printed.");
        err.println("If no word is specified, all dictionary entries are printed");
        err.println();

        System.exit(1);
    }

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
}