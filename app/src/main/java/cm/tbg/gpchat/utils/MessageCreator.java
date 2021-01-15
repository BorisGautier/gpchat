package cm.tbg.gpchat.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import cm.tbg.gpchat.model.ExpandableContact;
import cm.tbg.gpchat.model.constants.DownloadUploadStat;
import cm.tbg.gpchat.model.constants.MessageStat;
import cm.tbg.gpchat.model.constants.MessageType;
import cm.tbg.gpchat.model.realms.Message;
import cm.tbg.gpchat.model.realms.PhoneNumber;
import cm.tbg.gpchat.model.realms.QuotedMessage;
import cm.tbg.gpchat.model.realms.RealmContact;
import cm.tbg.gpchat.model.realms.RealmLocation;
import cm.tbg.gpchat.model.realms.User;
import cm.tbg.gpchat.placespicker.Place;
import cm.tbg.gpchat.utils.network.FireManager;
import com.google.android.gms.maps.model.LatLng;
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;



//this class will create a Message object with the needed properties
//it will also save the message to realm and save chat if not exists before

public class MessageCreator {
    private final User user;
    private final Context context;
    private final int type;
    private final String text;
    private final String path;
    private final boolean fromCamera;
    private final String duration;
    private final List<ExpandableContact> contacts;
    private final Place place;
    private final Message quotedMessage;

    private MessageCreator(Builder builder) {
        this.user = builder.user;
        this.context = builder.context;
        this.type = builder.type;
        this.text = builder.text;
        this.path = builder.path;
        this.fromCamera = builder.fromCamera;
        this.duration = builder.duration;
        this.contacts = builder.contacts;
        this.place = builder.place;
        this.quotedMessage = builder.quotedMessage;
    }


    private static Message createTextMessage(User user, String text) {
        String receiverUid = user.getUid();
        //messageId
        final String pushKey = FireConstants.messages.push().getKey();
        final Message message = new Message();
        message.setFromId(FireManager.getUid());
        message.setContent(text);
        message.setToId(receiverUid);
        message.setChatId(receiverUid);
        message.setType(MessageType.SENT_TEXT);
        //set the message time locally
        // this will replaced when sending to firebase database with the server time
        message.setTimestamp(String.valueOf(new Date().getTime()));
        //initial state is pending
        message.setMessageStat(MessageStat.PENDING);
        message.setMessageId(pushKey);

        if (user.isGroupBool())
            message.setGroup(true);


        return message;
    }

    private static Message createImageMessage(User user, final String imagePath, boolean fromCamera) {

        final Message message = new Message();
        final String pushKey = FireConstants.messages.push().getKey();
        int type = MessageType.SENT_IMAGE;
        String receiverUid = user.getUid();

        //generate file in sent images folder
        File file = DirManager.generateFile(type);
        String fileExtension = Util.getFileExtensionFromPath(imagePath);

        if (fileExtension.equals("gif")) {
            try {
                FileUtils.copyFile(imagePath, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //compress image and copy it to the given file
            BitmapUtils.compressImage(imagePath, file);
        }
        //if this image is captured by the camera in our app
        // then we need to delete the captured image after copying it to another directory
        if (fromCamera) {
            //delete captured image from camera after compress it
            FileUtils.deleteFile(imagePath);
        }


        String filePath = file.getPath();

        //set the file size
        String fileSize = Util.getFileSizeFromLong(file.length(), true);

        message.setLocalPath(filePath);
        //blurred thumb image
        String thumb = BitmapUtils.decodeImage(filePath, true);
        message.setType(type);
        message.setFromId(FireManager.getUid());
        message.setToId(receiverUid);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setChatId(receiverUid);
        message.setMessageStat(MessageStat.PENDING);
        message.setDownloadUploadStat(DownloadUploadStat.LOADING);
        message.setMessageId(pushKey);
        message.setThumb(thumb);
        message.setMetadata(fileSize);

        if (user.isGroupBool())
            message.setGroup(true);

        ContentResolver resolver = MyApp.context().getContentResolver();

        Uri imageCollection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "new-image3" + ".jpg");
//        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DirManager.mainAppFolder()");

        Uri imageUri = resolver.insert(imageCollection, contentValues);
        try {
            OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            Bitmap bitmap = BitmapUtils.convertFileImageToBitmap(message.getLocalPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return message;
    }

    private static Message createVideoMessage(Context context, User user, final String path) {
        //REMINDER we do not copy the original file if the user chose a video from gallery because it may has a Big Size
        final Message message = new Message();
        final String pushKey = FireConstants.messages.push().getKey();
        File file = new File(path);
        //get video size
        String videoSize = Util.getFileSizeFromLong(file.length(), true);
        String receiverUid = user.getUid();


        //get raw image thumb bitmap
        Bitmap videoThumbBitmap = BitmapUtils.getThumbnailFromVideo(path);
        //generate blurred thumb to send it to other user
        String thumb = BitmapUtils.decodeImage(videoThumbBitmap);
        //generate normal video thumb without blur to show it in recyclerView
        String videoThumb = BitmapUtils.generateVideoThumbAsBase64(videoThumbBitmap);
        message.setLocalPath(path);
        message.setThumb(thumb);
        message.setVideoThumb(videoThumb);
        message.setMetadata(videoSize);
        //set video duration
        message.setMediaDuration(Util.getVideoLength(context, path));
        message.setType(MessageType.SENT_VIDEO);
        message.setFromId(FireManager.getUid());
        message.setToId(receiverUid);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setChatId(receiverUid);
        message.setMessageStat(MessageStat.PENDING);
        message.setDownloadUploadStat(DownloadUploadStat.LOADING);
        message.setMessageId(pushKey);

        if (user.isGroupBool())
            message.setGroup(true);


        return message;

    }

    private static Message createAudioMessage(User user, String filePath, String audioDuration) {
        String receiverUid = user.getUid();


        int type = MessageType.SENT_AUDIO;
        final Message message = new Message();
        final String pushKey = FireConstants.messages.push().getKey();

        File audioFile = new File(filePath);
        String fileSize = Util.getFileSizeFromLong(audioFile.length(), true);
        //get file extension
        String fileExtension = Util.getFileExtensionFromPath(filePath);
        File file = DirManager.generateAudioFile(type, fileExtension);

        try {
            //copy original file to this new path
            FileUtils.copyFile(audioFile, file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        message.setLocalPath(filePath);

        message.setType(type);
        message.setFromId(FireManager.getUid());
        message.setToId(receiverUid);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setChatId(receiverUid);
        message.setMessageStat(MessageStat.PENDING);
        message.setDownloadUploadStat(DownloadUploadStat.LOADING);
        message.setMessageId(pushKey);
        message.setMetadata(fileSize);
        message.setMediaDuration(audioDuration);

        if (user.isGroupBool())
            message.setGroup(true);


        return message;
    }

    private static Message createFileMessage(User user, final String filePath) {
        String receiverUid = user.getUid();
        File file = new File(filePath);

        int type = MessageType.SENT_FILE;
        final Message message = new Message();
        final String pushKey = FireConstants.messages.push().getKey();
        final String fileName = Util.getFileNameFromPath(filePath);
        String fileSize = Util.getFileSizeFromLong(file.length(), true);


        message.setLocalPath(filePath);

        message.setType(type);
        message.setFromId(FireManager.getUid());
        message.setToId(receiverUid);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setChatId(receiverUid);
        message.setMessageStat(MessageStat.PENDING);
        message.setDownloadUploadStat(DownloadUploadStat.LOADING);
        message.setMessageId(pushKey);
        message.setMetadata(fileName);
        message.setFileSize(fileSize);

        if (user.isGroupBool())
            message.setGroup(true);


        return message;
    }

    //create multiple contact messages since the user may select multiple contacts to send
    private static List<Message> createContactsMessages(List<ExpandableContact> selectedContacts, User user) {

        List<Message> messageList = new ArrayList<>();
        String receiverUid = user.getUid();

        for (MultiCheckExpandableGroup selectedContact : selectedContacts) {


            final String pushKey = FireConstants.messages.push().getKey();

            final Message message = new Message();
            message.setFromId(FireManager.getUid());
            //set the contact name as content
            message.setContent(selectedContact.getTitle());
            message.setToId(receiverUid);
            message.setChatId(receiverUid);
            message.setType(MessageType.SENT_CONTACT);
            message.setTimestamp(String.valueOf(new Date().getTime()));
            message.setMessageStat(MessageStat.PENDING);
            message.setMessageId(pushKey);


            if (user.isGroupBool())
                message.setGroup(true);

            //get contact numbers
            ArrayList<PhoneNumber> numbers = (ArrayList) selectedContact.getItems();

            RealmContact realmContact = new RealmContact(selectedContact.getTitle(), numbers);


            message.setContact(realmContact);


            messageList.add(message);

        }
        return messageList;
    }

    private static Message createVoiceMessage(User user, String path, String duration) {
        String receiverUid = user.getUid();
        final Message message = new Message();
        final String pushKey = FireConstants.messages.push().getKey();
        message.setLocalPath(path);
        message.setType(MessageType.SENT_VOICE_MESSAGE);
        message.setFromId(FireManager.getUid());
        message.setToId(receiverUid);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setChatId(receiverUid);
        message.setMessageStat(MessageStat.PENDING);
        message.setDownloadUploadStat(DownloadUploadStat.LOADING);
        message.setMessageId(pushKey);
        message.setMediaDuration(duration);

        if (user.isGroupBool())
            message.setGroup(true);


        return message;
    }

    private static Message createLocationMessage(User user, Place place) {
        String receiverUid = user.getUid();
        final Message message = new Message();

        final String pushKey = FireConstants.messages.push().getKey();

        String placeName = place.getName().toString();

        String addressName = place.getAddress().toString();
        LatLng latLng = place.getLatLng();
        message.setFromId(FireManager.getUid());
        message.setContent(placeName);
        message.setToId(receiverUid);
        message.setChatId(receiverUid);
        message.setType(MessageType.SENT_LOCATION);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setMessageStat(MessageStat.PENDING);
        message.setMessageId(pushKey);

        RealmLocation location = new RealmLocation(latLng.latitude, latLng.longitude, addressName, placeName);
        message.setLocation(location);

        if (user.isGroupBool())
            message.setGroup(true);


        return message;
    }

    public static Message createForwardedMessage(Message mMessage, User user, String fromId) {
        //clone the original message to modify some of its properties
        Message message = mMessage.getClonedMessage();


        String newMessageId = FireConstants.messages.push().getKey();
        //change messageId
        message.setMessageId(newMessageId);
        //change timestamp
        message.setTimestamp(String.valueOf(new Date().getTime()));
        message.setForwarded(true);
        //change fromId
        message.setFromId(fromId);
        //change toId
        message.setToId(user.getUid());

        message.setChatId(user.getUid());
        //convert received type to a sent type if needed
        message.setType(MessageType.convertReceivedToSent(message.getType()));
        message.setMessageStat(MessageStat.PENDING);
        message.setGroup(user.isGroupBool());

        //copy the file from the message to a New Path
        //this is because when the user deletes a message from a Chat
        //it will not affect the forwarded message
        // since it has a different path with a different file name
        if (message.getLocalPath() != null) {
            File forwardedFile = DirManager.generateFile(message.getType());
            try {
                FileUtils.copyFile(message.getLocalPath(), forwardedFile);
                message.setLocalPath(forwardedFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        RealmHelper.getInstance().saveObjectToRealm(message);
        RealmHelper.getInstance().saveChatIfNotExists(message, user);
        return message;
    }


    public static Builder builder(User user, int type) {
        return new Builder(user, type);
    }

    public User getUser() {
        return user;
    }

    public Context getContext() {
        return context;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getPath() {
        return path;
    }

    public boolean isFromCamera() {
        return fromCamera;
    }

    public String getDuration() {
        return duration;
    }

    public List<ExpandableContact> getContacts() {
        return contacts;
    }

    public Place getPlace() {
        return place;
    }

    public Message getQuotedMessage() {
        return quotedMessage;
    }

    public static class Builder {
        private User user;
        private Context context;
        private int type;
        private String text;
        private String path;
        private boolean fromCamera;
        private String duration;
        private List<ExpandableContact> contacts;
        private Place place;
        private Message quotedMessage;

        public Builder(User user, int type) {
            this.user = user;
            this.type = type;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder fromCamera(boolean fromCamera) {
            this.fromCamera = fromCamera;
            return this;
        }

        public Builder duration(String duration) {
            this.duration = duration;
            return this;
        }

        public Builder contacts(List<ExpandableContact> contacts) {
            this.contacts = contacts;
            return this;
        }

        public Builder place(Place place) {
            this.place = place;
            return this;
        }

        public Builder quotedMessage(Message quotedMessage) {
            this.quotedMessage = quotedMessage;
            return this;
        }

        public List<Message> buildContacts() {
            List<Message> contactsMessages = createContactsMessages(contacts, user);
            for (Message message : contactsMessages) {
                if (quotedMessage != null)
                    message.setQuotedMessage(QuotedMessage.messageToQuotedMessage(quotedMessage));

                //save chat if this the first message in this chat
                RealmHelper.getInstance().saveObjectToRealm(message);
                //save the message to realm
                RealmHelper.getInstance().saveChatIfNotExists(message, user);
            }
            return contactsMessages;
        }

        public Message build() {
            Message message = null;

            switch (type) {
                case MessageType.SENT_TEXT:
                    message = createTextMessage(user, text);
                    break;
                case MessageType.SENT_IMAGE:
                    message = createImageMessage(user, path, fromCamera);
                    break;

                case MessageType.SENT_VIDEO:
                    message = createVideoMessage(context, user, path);
                    break;

                case MessageType.SENT_AUDIO:
                    message = createAudioMessage(user, path, duration);
                    break;

                case MessageType.SENT_FILE:
                    message = createFileMessage(user, path);
                    break;

                case MessageType.SENT_VOICE_MESSAGE:
                    message = createVoiceMessage(user, path, duration);
                    break;

                case MessageType.SENT_LOCATION:
                    message = createLocationMessage(user, place);
                    break;

                case MessageType.REPLY_STATUS:

                    break;

            }
            if (message != null && quotedMessage != null) {
                QuotedMessage quotedMessageToSave = QuotedMessage.messageToQuotedMessage(this.quotedMessage);
                message.setQuotedMessage(quotedMessageToSave);
            }

            if (user.isBroadcastBool()) {
                message.setBroadcast(true);
                //copy the message to the user's chat also
                for (User user : user.getBroadcast().getUsers()) {
                    Message clonedMessage = message.cloneExactly();
                    clonedMessage.setChatId(user.getUid());
                    clonedMessage.setToId(user.getUid());
                    clonedMessage.setMessageId(message.getMessageId());
                    RealmHelper.getInstance().saveObjectToRealm(clonedMessage);
                    RealmHelper.getInstance().saveChatIfNotExists(clonedMessage, user);
                }
            }


            //save the message to realm
            RealmHelper.getInstance().saveObjectToRealm(message);

            //save chat if this the first message in this chat
            RealmHelper.getInstance().saveChatIfNotExists(message, user);


            return message;
        }
    }
}
