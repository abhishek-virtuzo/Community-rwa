package virtuzo.abhishek.community.realm;

import java.util.ArrayList;

import io.realm.RealmResults;
import virtuzo.abhishek.community.AppController;
import virtuzo.abhishek.community.adapter.Panchang;
import virtuzo.abhishek.community.model.BloodGroup;
import virtuzo.abhishek.community.model.Complaint;
import virtuzo.abhishek.community.model.Contact;
import virtuzo.abhishek.community.model.ContactCategory;
import virtuzo.abhishek.community.model.ContactPerson;
import virtuzo.abhishek.community.model.Message;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.model.PaymentLink;
import virtuzo.abhishek.community.model.Resident;
import virtuzo.abhishek.community.model.ResidentBlock;

/**
 * Created by Abhishek Aggarwal on 7/30/2018.
 */

public class RealmHelper {

    public static RealmHelper instance;

    public static RealmHelper getInstance() {
        if (instance == null) {
            instance = new RealmHelper();
        }
        return instance;
    }

    public ArrayList<ContactCategory> getContactCategories() {
        ArrayList<ContactCategory> contactCategories;
        RealmResults<ContactCategory> contactCategoriesRealm = AppController.realm.where(ContactCategory.class).findAll();
        contactCategories = (ArrayList<ContactCategory>) AppController.realm.copyFromRealm(contactCategoriesRealm);
        return contactCategories;
    }

    public ArrayList<ContactCategory> getContactCategories(int ID) {
        ArrayList<ContactCategory> contactCategories;
        RealmResults<ContactCategory> contactCategoriesRealm = AppController.realm.where(ContactCategory.class).equalTo("ParentID", ID).findAll();
        contactCategories = (ArrayList<ContactCategory>) AppController.realm.copyFromRealm(contactCategoriesRealm);
        return contactCategories;
    }

    public ArrayList<ContactPerson> getContactPeople(int CategoryId) {
        ArrayList<ContactPerson> contactPeople;
        RealmResults<ContactPerson> contactPeopleRealm = AppController.realm.where(ContactPerson.class).equalTo("CategoryId", CategoryId).findAll();
        contactPeople = (ArrayList<ContactPerson>) AppController.realm.copyFromRealm(contactPeopleRealm);
        return contactPeople;
    }

    public ContactPerson getContactPerson(int ContactID) {
        ContactPerson contactPersonRealm = AppController.realm.where(ContactPerson.class).equalTo("ContactID", ContactID).findFirst();
        if (contactPersonRealm == null) {
            return null;
        }
        ContactPerson contactPerson = (ContactPerson) AppController.realm.copyFromRealm(contactPersonRealm);
        return contactPerson;
    }

    public ArrayList<OfficeBearer> getOfficeBearers() {
        ArrayList<OfficeBearer> officeBearers;
        RealmResults<OfficeBearer> officeBearersRealm = AppController.realm.where(OfficeBearer.class).findAll();
        officeBearers = (ArrayList<OfficeBearer>) AppController.realm.copyFromRealm(officeBearersRealm);
        return officeBearers;
    }

    public OfficeBearer getOfficeBearer(int ID) {
        OfficeBearer officeBearerRealm = AppController.realm.where(OfficeBearer.class).equalTo("ID", ID).findFirst();
        if (officeBearerRealm == null) {
            return null;
        }
        OfficeBearer officeBearer = (OfficeBearer) AppController.realm.copyFromRealm(officeBearerRealm);
        return officeBearer;
    }

    public ArrayList<Message> getMessages() {
        ArrayList<Message> messages;
        RealmResults<Message> messagesRealm = AppController.realm.where(Message.class).findAll();
        messages = (ArrayList<Message>) AppController.realm.copyFromRealm(messagesRealm);
        return messages;
    }

    public Message getMessage(int ID) {
        Message messageRealm = AppController.realm.where(Message.class).equalTo("ID", ID).findFirst();
        if (messageRealm == null) {
            return null;
        }
        Message message = (Message) AppController.realm.copyFromRealm(messageRealm);
        return message;
    }

    public ArrayList<ResidentBlock> getResidentBlocks() {
        ArrayList<ResidentBlock> residentBlocks;
        RealmResults<ResidentBlock> residentBlocksRealm = AppController.realm.where(ResidentBlock.class).findAll();
        residentBlocks = (ArrayList<ResidentBlock>) AppController.realm.copyFromRealm(residentBlocksRealm);
        return residentBlocks;
    }

    public ResidentBlock getResidentBlock(int ID) {
        ResidentBlock residentBlockRealm = AppController.realm.where(ResidentBlock.class).equalTo("ID", ID).findFirst();
        if (residentBlockRealm == null) {
            return null;
        }
        ResidentBlock residentBlock = (ResidentBlock) AppController.realm.copyFromRealm(residentBlockRealm);
        return residentBlock;
    }

    public ArrayList<Resident> getResidents(int ResidentBlockID) {
        ArrayList<Resident> residents;
        RealmResults<Resident> residentsRealm = AppController.realm.where(Resident.class).equalTo("ResidentBlockID", ResidentBlockID).sort("ResidentName").findAll();
        residents = (ArrayList<Resident>) AppController.realm.copyFromRealm(residentsRealm);
        return residents;
    }

    public ArrayList<Resident> getResidents() {
        ArrayList<Resident> residents;
        RealmResults<Resident> residentsRealm = AppController.realm.where(Resident.class).sort("ResidentBlockID").findAll();
        residents = (ArrayList<Resident>) AppController.realm.copyFromRealm(residentsRealm);
        return residents;
    }

    public Resident getResident(int ID) {
        Resident residentRealm = AppController.realm.where(Resident.class).equalTo("ID", ID).findFirst();
        if (residentRealm == null) {
            return null;
        }
        Resident resident = (Resident) AppController.realm.copyFromRealm(residentRealm);
        return resident;
    }

    public ArrayList<Panchang> getPanchangs() {
        ArrayList<Panchang> panchangs;
        RealmResults<Panchang> panchangsRealm = AppController.realm.where(Panchang.class).findAll();
        panchangs = (ArrayList<Panchang>) AppController.realm.copyFromRealm(panchangsRealm);
        return panchangs;
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts;
        RealmResults<Contact> contactsRealm = AppController.realm.where(Contact.class).findAll();
        contacts = (ArrayList<Contact>) AppController.realm.copyFromRealm(contactsRealm);
        return contacts;
    }

    public ArrayList<BloodGroup> getBloodGroups() {
        ArrayList<BloodGroup> bloodGroups;
        RealmResults<BloodGroup> bloodGroupsRealm = AppController.realm.where(BloodGroup.class).findAll();
        bloodGroups = (ArrayList<BloodGroup>) AppController.realm.copyFromRealm(bloodGroupsRealm);
        return bloodGroups;
    }

    public ArrayList<Complaint> getUnsyncedComplaints() {
        ArrayList<Complaint> complaints;
        RealmResults<Complaint> bloodGroupsRealm = AppController.realm.where(Complaint.class).equalTo("Synced", 0).findAll();
        complaints = (ArrayList<Complaint>) AppController.realm.copyFromRealm(bloodGroupsRealm);
        return complaints;
    }

    public ArrayList<Complaint> getComplaints() {
        ArrayList<Complaint> complaints;
        RealmResults<Complaint> bloodGroupsRealm = AppController.realm.where(Complaint.class).findAll();
        complaints = (ArrayList<Complaint>) AppController.realm.copyFromRealm(bloodGroupsRealm);
        return complaints;
    }

    public ArrayList<PaymentLink> getPaymentLinks() {
        ArrayList<PaymentLink> paymentLinks;
        RealmResults<PaymentLink> paymentLinksRealm = AppController.realm.where(PaymentLink.class).findAll();
        paymentLinks = (ArrayList<PaymentLink>) AppController.realm.copyFromRealm(paymentLinksRealm);
        return paymentLinks;
    }

}
