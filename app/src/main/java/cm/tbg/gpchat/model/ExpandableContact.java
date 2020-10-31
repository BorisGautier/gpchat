package cm.tbg.gpchat.model;

import android.os.Parcelable;

import cm.tbg.gpchat.model.realms.PhoneNumber;
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup;

import io.realm.RealmList;

/**
 * Created by Devlomi on 15/01/2018.
 */

//expandable contact
//make user selects which numbers want to send for the contact
public class ExpandableContact extends MultiCheckExpandableGroup implements Parcelable {


    public ExpandableContact(String contactName, RealmList<PhoneNumber> phoneNumbers) {
        super(contactName, phoneNumbers);

    }


}
