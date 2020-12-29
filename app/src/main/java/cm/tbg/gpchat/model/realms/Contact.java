package cm.tbg.gpchat.model.realms;

import android.os.Parcel;
import android.os.Parcelable;


public class Contact implements Parcelable {
    private String phoneNumber, name;

    public Contact(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    protected Contact(Parcel in) {
        phoneNumber = in.readString();
        name = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public cm.tbg.gpchat.model.realms.Contact createFromParcel(Parcel in) {
            return new cm.tbg.gpchat.model.realms.Contact(in);
        }

        @Override
        public cm.tbg.gpchat.model.realms.Contact[] newArray(int size) {
            return new cm.tbg.gpchat.model.realms.Contact[size];
        }
    };

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof cm.tbg.gpchat.model.realms.Contact)) return false;
        cm.tbg.gpchat.model.realms.Contact contact = (cm.tbg.gpchat.model.realms.Contact) o;
        return getPhoneNumber().equals(contact.getPhoneNumber());

    }

    @Override
    public int hashCode() {
        return getPhoneNumber().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(phoneNumber);
        parcel.writeString(name);
    }
}
