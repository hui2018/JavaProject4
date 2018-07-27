package pdx.cs410J.hui2;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

/**
 * A phone bill class that contains the customer name and multiple phone calls
 */
public class PhoneBill extends AbstractPhoneBill{
    ArrayList<PhoneCall> phoneCalls;
    private String customer;
    ArrayList<PhoneCall> test = new ArrayList<>();

    PhoneBill(String customer, PhoneCall phoneCall)
    {
        this.customer = customer;
        phoneCalls = new ArrayList<PhoneCall>();
        addPhoneCall(phoneCall);
    }

    @Override
    public String getCustomer() {
        return customer;
    }


    @Override
    public void addPhoneCall(AbstractPhoneCall addCall) {
        phoneCalls.add((PhoneCall) addCall);

    }

    @Override
    public Collection getPhoneCalls() {
        Collections.sort(phoneCalls);
        return phoneCalls;
    }

}
