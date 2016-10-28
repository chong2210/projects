import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;

import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.common.collect.Table;

import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
*   Some of this class's methods and variables were used from Google's API examples
*   Links to:
*   Google Sheets:  https://developers.google.com/sheets/quickstart/java
*   Gmail:          https://developers.google.com/gmail/api/quickstart/java
*/
public class GmailQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS,
                    GmailScopes.GMAIL_MODIFY,
                    GmailScopes.GMAIL_READONLY,
                    GmailScopes.MAIL_GOOGLE_COM,
                    SheetsScopes.SPREADSHEETS_READONLY,
                    SheetsScopes.SPREADSHEETS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GmailQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    //Creates an ArrayList of CellData with each CellData being assigned the values date, time, and subject.
    public static ArrayList<CellData> createListOfCells(String date, String time, String subject) {
        ArrayList<CellData> currCellList = new ArrayList<>();   //Initialized array list of CellData

        CellData tempCell1 = new CellData();                    //Initialized CellData tempCell1
        CellData tempCell2 = new CellData();                    //Initialized CellData tempCell2
        CellData tempCell3 = new CellData();                    //Initialized CellData tempCell3

        //Sets tempCell1 to an implicit ExtendedValue object with string set to date and adds it to currCellList
        tempCell1.setUserEnteredValue((new ExtendedValue()).setStringValue(date));
        currCellList.add(tempCell1);

        //Sets tempCell2 to an implicit ExtendedValue object with string set to time and adds it to currCellList
        tempCell2.setUserEnteredValue((new ExtendedValue()).setStringValue(time));
        currCellList.add(tempCell2);

        //Sets tempCell3 to an implicit ExtendedValue object with string set to subject and adds it to currCellList
        tempCell3.setUserEnteredValue((new ExtendedValue()).setStringValue(subject));
        currCellList.add(tempCell3);

        return currCellList;
    }

    public static void main(String[] args) throws IOException {
        Gmail gmailService = getGmailService();         //Retrieves Gmail API service
        Sheets sheetsService = getSheetsService();      //Retrieves Google API Sheets service

        String user = "cheesemonkey300@gmail.com";      //Sets string to email address

        //Retrieves ListMessagesResponse of all messages in user's account
        ListMessagesResponse messagesResponse = gmailService.users().messages().list(user).execute();
        //Retrieves a List of Message objects
        List<Message> messages = messagesResponse.getMessages();
        //List of RowData for the Sheet
        ArrayList<RowData> messageData = new ArrayList<>();

        //Creates ArrayList of CellData for initial RowData entry and adds to messageData
        ArrayList<CellData> initCellList = createListOfCells("Date", "Time", "Subject");
        messageData.add((new RowData()).setValues(initCellList));

        //Checks for extract Messages from Gmail user account
        if (messages.size() <= 0){
            System.out.println("No messages found.");
        }
        else {
            //Iterates through messages and retrieves the message by using the list of message Ids in messages.
            //Then extracts the message's content through each message's payload headers.
            for (Message message : messages) {
                //Retrieves a Message object through an API call for a message ID
                Message messageContent = gmailService.users().messages().get(user, message.getId()).execute();

                //Retrieves the headers of messageContent which contains Subject, Date, etc
                List<MessagePartHeader> headers = messageContent.getPayload().getHeaders();

                //Temp variables for extracted variables
                String date = "";
                String time = "";
                String subject = "";

                //Iterates through headers and searches for the headers labeled: "Date" and "Subject", and assigns
                //them to their corresponding values.
                for(MessagePartHeader messagePartHeader : headers){
                    if (messagePartHeader.getName().equals("Date"))
                        date = messagePartHeader.getValue();
                    else if(messagePartHeader.getName().equals("Subject"))
                        subject = messagePartHeader.getValue();
                }

                Date currDate = new Date(date);                                         //Creates new Date object
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");         //SimpleDateFormat for date
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a zzz");   //SimpleDateFormat for time

                date = dateFormat.format(currDate);         //Assigns formatted date
                time = timeFormat.format(currDate);         //Assigns formatted time

                //Calls createListOfCells with date, time, and subject within it
                ArrayList<CellData> currCellList = createListOfCells(date, time, subject);
                //Adds currCellList to messageData by assigning it as value of an implicit RowData object
                messageData.add((new RowData()).setValues(currCellList));
            }
        }

        Spreadsheet spreadsheet = new Spreadsheet();            //Spreadsheet object to hold values for create
                                                                // spreadsheet API call

        ArrayList<GridData> gridData = new ArrayList<>();       //gridData a list to hold row data
        gridData.add((new GridData()).setRowData(messageData)); //Sets RowData of gridData to messageData

        Sheet sheet = new Sheet();                              //sheet to hold grid data
        sheet.setData(gridData);                                //sheet's data is set to gridData

        ArrayList<Sheet> sheets = new ArrayList<>();            //List of sheets to hold sheet objects for spreadsheet
        sheets.add(sheet);                                      //Adds sheet to sheet
        spreadsheet.setSheets(sheets);                          //sheets are added to the spreadsheet

        Date todayDate = new Date();                                        //Creates new Date object for today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");     //SimpleDateFormat for date

        //Sets the title of the spreadsheet
        spreadsheet.setProperties((new SpreadsheetProperties()).setTitle(dateFormat.format(todayDate)));

        //Sends spreadsheet through Sheets API to be created
        Spreadsheet createdSpreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();

        //Grabs latest row values from newly created spreadsheet
        ValueRange valueRange = sheetsService.spreadsheets().values().get(createdSpreadsheet.getSpreadsheetId(), "A2:C2").execute();

        BufferedWriter writer = null;
        try{
            File testFile = new File("TestFile.txt");               //Creates temp file

            writer = new BufferedWriter(new FileWriter(testFile));  //Makes a writer for that file name

            //Iterates through each grabbed value and adds value to testFile
            for (List<Object> v : valueRange.getValues()){
                for(Object o : v){
                    writer.write(o.toString() + ",");
                }
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            writer.close();
        }
    }

}
