import javax.print.Doc;
import javax.print.attribute.standard.DocumentName;
import java.net.StandardSocketOptions;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Database {

//    private static final String url = "jdbc:mysql://127.0.0.1:3306/mydbtest?useSSL=false";
//    private static final String user = "admin";
//    private static final String password = "FJ`;62LfOTVZoM2+;3Qo983_zq9iGix9S107pi6)|CzU2`rdVRZD7?5a65sM;|6'54FE\\w9t4Ph~=";

            private static final String url = "jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7234613?useSSL=false";
            private static final String user = "sql7234613";
            private static final String password = "hgnvQfb4Kk";

    //private static final String password = "333999333tima";
//    String user = "root";
//    String password = "enaca2225";
//    String url = "jdbc:mysql://localhost:3306/project_new?useSSL=false";
//    String password = "123123123Aa";
//    String url = "jdbc:mysql://localhost:3306/db?useSSL=false";

    public static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static PreparedStatement preparedStatement;

    /**
     * common constructor
     */
    public Database(){
        preparedStatement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        }catch (Exception ex){
            System.out.println("error creating db: " + ex.toString());
        }
    }

    /**
     * search document in database and return its existing
     * @return boolean
     */
    public static int isDocumentExist(Document document){
        int localId = -1;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select id_av_materials, id_books, id_journals, id from documents");

            while (resultSet.next()){
                int id=0;
                if(resultSet.getInt("id_av_materials") != 0){
                    id = resultSet.getInt("id_av_materials");
;                }else if(resultSet.getInt("id_books") != 0){
                    id = resultSet.getInt("id_books");
                }else if(resultSet.getInt("id_journals") != 0){
                    id = resultSet.getInt("id_journals");
                }
                if(document.localId == id){
                    localId = id;
                }

            }

        }catch (Exception ex){
            System.out.println("error isDocExist: " + ex.toString());
        }

        return localId;
    }

    /**
     * check document's existing
     * @return id document
     * @throws SQLException
     */
    public static int isDocumentExistByType(String type, Document document) throws SQLException{

        if(type.equals("journal")) {
            Journal journal = (Journal) document;

            String query = "select title, author, issue, editor, keywords, reference, id from " + type;

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            resultSet.next();
            if (resultSet.getString(1).equals(journal.name) && resultSet.getString(2).equals(journal.authors.toString())
                    && resultSet.getString(3).equals(journal.issue) && resultSet.getString(4).equals(journal.editor)
                    && resultSet.getInt(5) == journal.price && resultSet.getString(6).equals(journal.keywords.toString())
                    && resultSet.getBoolean(7) == journal.isReference) {
                return resultSet.getInt(8);
            } else {
                return -1;
            }
        }else if(type.equals("book")){
            Book book = (Book) document;

            String query = "select * from " + type;

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                if (resultSet.getString(2).equals(book.name) && resultSet.getString(3).equals(book.authors.toString())
                        && resultSet.getString(4).equals(book.publisher) && resultSet.getString(5).equals(book.edition)
                        && resultSet.getInt(6) == book.publishYear && resultSet.getInt(7) == book.price
                        && resultSet.getString(8).equals(book.keywords.toString()) && resultSet.getBoolean(10) == book.isReference) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        }else if(type.equals("avmaterial")) {
            return -1;
        }

        return -1;
    }

    /**
     * our document took or not
     * @return boolean
     */
    public static boolean isDocumentActive(String type, Document document){
        try {
            String query = "select * from documents ";
            boolean isExist;
            resultSet = statement.executeQuery(query);

            if(type.equals("book")){
                query += "where id_books=" + Integer.toString(document.id);
            }else if (type.equals("journal")){
                query += "where id_books=" + Integer.toString(document.id);
            }else if(type.equals("avmaterial")){
                query += "where id_books=" + Integer.toString(document.id);
            }

            return resultSet.next() && resultSet.getBoolean(7);

        }catch (Exception e){
            System.out.println("Error in checking active doc " + e.toString());
            return false;
        }
    }

    /**
     * another implementation of isDocumentActive
     * @return boolean
     */
    public static boolean isDocumentActiveById(int id, String type){
        try {
            String query = "select * from documents where " + type + "=" + Integer.toString(id);
            resultSet = statement.executeQuery(query);
            resultSet.next();

            return false;

        }catch (Exception e){
            System.out.println("Error isDocumentActiveById " + e.toString());
            return false;
        }
    }

    /**
     * check correct authorization
     * @param username phoneNumber
     * @param pass password
     * @return if is correct or nor
     */
    public static boolean isCorrectAuthorization(String username, String pass){
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT phonenumber, password FROM users");
            while (resultSet.next()){
                if(resultSet.getString(1).equals(username) && resultSet.getString(2).equals(pass)){
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            System.out.println("error: " + e.toString());
        }
        return false;
    }

    /**
     * find patron by phone number
     * @param login
     * @return correct patron
     */
    public static Patron getPatronByNumber(String login){
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from users where phoneNumber=" + login);
            rs.next();

            Patron patron = new Patron(rs.getString(2), rs.getString(7),
                    rs.getString(3), rs.getString(4), rs.getBoolean(6), rs.getInt(5),  Patron.getCorrectPatronType(rs.getString("type")), rs.getBoolean("isActive"));

            patron.id = rs.getInt("id");

            return patron;
        }catch (Exception e){
            System.out.println("Error in getting user by login " + e.toString());
            return new Patron();
        }
    }

    /**
     * @return all documents
     */
    public static ArrayList<Document> getAllDocuments(){
        ArrayList<Document> users = new ArrayList<>();

        try {
            String query = "select * from documents order by id";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);


            while (resultSet.next()){
                Document currentDoc = null;
                String findInCurrentDBQuery = "select * from ";
                Statement tconnection = connection.createStatement();

                if(resultSet.getInt(2) != 0){
                    findInCurrentDBQuery += "av_materials where id=" + Integer.toString(resultSet.getInt(2));
                    ResultSet res = tconnection.executeQuery(findInCurrentDBQuery);
                    res.next();
                    currentDoc = createAVMaterialByResultSet(res, resultSet.getInt("id"), resultSet.getString("location"));
                    currentDoc.localId = res.getInt("id");
                    currentDoc.type = DocumentType.av_material;
                }else if(resultSet.getInt(3) != 0){
                    findInCurrentDBQuery += "books where id="+Integer.toString(resultSet.getInt(3));
                    ResultSet res = tconnection.executeQuery(findInCurrentDBQuery);
                    res.next();
                    currentDoc = createBookByResultSet(res, resultSet.getInt("id"), resultSet.getString("location"));
                    currentDoc.localId = res.getInt("id");
                    currentDoc.type = DocumentType.book;
                }else if(resultSet.getInt(4) != 0){
                    findInCurrentDBQuery += "journals where id="+Integer.toString(resultSet.getInt(4));
                    ResultSet res = tconnection.executeQuery(findInCurrentDBQuery);
                    res.next();
                    currentDoc = createJournalByResultSet(res, resultSet.getInt("id"), resultSet.getString("location"));
                    currentDoc.localId = res.getInt("id");
                    currentDoc.type = DocumentType.journal;
                }
                users.add(currentDoc);
            }

        }catch (Exception e){
            System.out.println("Error in getAllDocuments " + e.toString());
        }

        return users;
    }

    /**
     * @return documents' id to correct finding document
     */
    public static ArrayList<Integer> getAllDocumentsIDs(){
        ArrayList<Integer> ids = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select id from documents order by id");
            while (resultSet.next()){
                ids.add(resultSet.getInt(1));
            }
        }catch (Exception e){
            System.out.println("Error getAllDocumentsIDs " + e.toString());
        }

        return ids;
    }

    public static ArrayList<Document> getUserDocuments(Patron patron){
        ArrayList<Document> documents = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from booking order by id");

            while (resultSet.next()){
                if(resultSet.getInt("user_id") == patron.id){
                    Document document = null;

                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("select * from documents where id=" + resultSet.getInt("document_id"));
                    if(rs.next()) {
                        String query = "select * from ";
                        if (rs.getInt("id_av_materials") != 0) {
                            query += "av_materials where id=" + Integer.toString(rs.getInt("id_av_materials"));

                            Statement tst = connection.createStatement();
                            ResultSet trs = tst.executeQuery(query);
                            trs.next();

                            document = createAVMaterialByResultSet(trs, rs.getInt("id"), rs.getString("location"));
                            document.localId = rs.getInt("id_av_materials");
                        } else if (rs.getInt("id_books") != 0) {
                            query += "books where id=" + Integer.toString(rs.getInt("id_books"));

                            Statement tst = connection.createStatement();
                            ResultSet trs = tst.executeQuery(query);
                            trs.next();

                            document = createBookByResultSet(trs, rs.getInt("id"), rs.getString("location"));
                            document.localId = rs.getInt("id_books");

                        } else if (rs.getInt("id_journals") != 0) {
                            query += "journals where id=" + Integer.toString(rs.getInt("id_journals"));

                            Statement tst = connection.createStatement();
                            ResultSet trs = tst.executeQuery(query);
                            trs.next();

                            document = createJournalByResultSet(trs, rs.getInt("id"), rs.getString("location"));
                            document.localId = rs.getInt("id_journals");
                        }

                        documents.add(document);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("Error in getAllUserDocuments: " + e.toString());
        }

        return documents;
    }

    /**
     * Create exemplar of class AVmaterial using resultSet
     * @param rs ResultSer for creating new AVmaterial
     * @param id Global ID of AVmaterial which is saved in table with main information in Database
     * @param location Location of new AVmaterial
     * @return Created exemplar
     */

    private static AVmaterial createAVMaterialByResultSet(ResultSet rs, int id, String location){
        AVmaterial aVmaterial = null;
        try {
            aVmaterial = new AVmaterial(rs.getString("title"), new ArrayList<String>(Arrays.asList(rs.getString("author").split(", "))),
                    rs.getInt("cost"), new ArrayList<String>(Arrays.asList(rs.getString("keywords").split(", "))),
                    rs.getBoolean("reference"), true, location);
            aVmaterial.id = id;
            aVmaterial.localId = rs.getInt("id");

        }catch (Exception e){
            System.out.println("Error in createAVMaterialByResultSet: " + e.toString());
        }

        return aVmaterial;
    }

    /**
     * Create exemplar of class Book using resultSet
     * @param rs ResultSer for creating new book
     * @param id Global ID of book which is saved in table with main information in Database
     * @param location Location of new book
     * @return Created exemplar
     */

    private static Book createBookByResultSet(ResultSet rs, int id, String location){
        Book book = null;
        try {
            book = new Book(rs.getString("title"), new ArrayList<String>(Arrays.asList(rs.getString("author").split(", "))),
                    rs.getInt("cost"), new ArrayList<String>(Arrays.asList(rs.getString("keywords").split(", "))),
                    rs.getBoolean("reference"), rs.getString("publisher"), rs.getString("edition"),
                    rs.getInt("publish_year"), rs.getBoolean("isBestSeller"), location, true);
            book.id = id;
            book.localId = rs.getInt("id");
        }catch (Exception e){
            System.out.println("Error in createBookByResultSet: " + e.toString());
        }

        return book;
    }

    /**
     * Create exemplar of class journal using resultSet
     * @param rs ResultSer for creating new journal
     * @param id Global ID of journal which is saved in table with main information in Database
     * @param location Location of new journal
     * @return Created exemplar
     */

    private static Journal createJournalByResultSet(ResultSet rs, int id, String location){
        Journal journal = null;
        try {
            journal = new Journal(rs.getString("title"), new ArrayList<String>(Arrays.asList(rs.getString("author").split(", "))),
                    rs.getInt("cost"), new ArrayList<String>(Arrays.asList(rs.getString("keywords").split(", "))),
                    rs.getBoolean("reference"), "-1", rs.getString("issue"), rs.getString("editor"),true, location);
            journal.id = id;
            journal.localId = rs.getInt("id");
        }catch (Exception e){
            System.out.println("Error in createJournalByResultSet: " + e.toString());
        }

        return journal;
    }

    /**
     * Return number of current document's copies in database
     * @param document Document which number of copies is needed
     * @return Number of copies
     */

    public static int getAmountOfCurrentDocument(Document document){
        int ans = -1;
        try{
            Statement st = connection.createStatement();
            String type = Document.getParsedType(document.type);
            String query = "select number from " + type + " where id = "+Integer.toString(document.localId);
            ResultSet rs = st.executeQuery(query);
            rs.next();
            ans = rs.getInt("number");

        }catch (Exception e){
            System.out.println("Error in getAmountOfCurrentDocument: " + e.toString());
        }

        return ans;
    }

    /**
     * Return number of current book's copies in database
     * @param book Book which number of copies is needed
     * @return Number of copies
     */

    public static int getAmountOfCurrentBook(Book book){
        int ans = -1;
        try {
            Statement st  = connection.createStatement();
            String query = "select number from books where id ="+Integer.toString(book.localId);
            ResultSet rs = st.executeQuery(query);
            rs.next();

            ans =  rs.getInt("number");

        }catch (Exception e){
            System.out.println("Error in getAmountOfCurrentBook: " + e.toString());
        }

        return ans;
    }

    /**
     * Return number of current journal's copies in database
     * @param journal Journal which number of copies is needed
     * @return Number of copies
     */

    public static int getAmountOfCurrentJournal(Journal journal){
        int ans = -1;
        try {
            Statement st = connection.createStatement();
            String query = "select number from journals where id ="+Integer.toString(getCorrectIdInLocalDatabase(journal.id));
            ResultSet rs = st.executeQuery(query);
            rs.next();

            ans =  rs.getInt("number");

        }catch (Exception e){
            System.out.println("Error in getAmountOfCurrentJournal: " + e.toString());
        }

        return ans;
    }

    /**
     * Return number of current AVmaterial's copies in database
     * @param aVmaterial AVmateriak which number of copies is needed
     * @return Number of copies
     */

    public static int getAmountOfCurrentAvmaterial(AVmaterial aVmaterial){
        int ansv = -1;
        try {
            statement = connection.createStatement();
            String query = "select number from av_materials where id ="+Integer.toString(getCorrectIdInLocalDatabase(aVmaterial.id));
            resultSet = statement.executeQuery(query);
            resultSet.next();

            ansv =  resultSet.getInt("number");

        }catch (Exception e){
            System.out.println("Error in getAmountOfCurrentAvmaterial: " + e.toString());
        }

        return ansv;
    }

    /**
     * Return document using global id
     * @param id Global id of document which is saved in table with main information in Database
     * @return Exemplar of class Document
     */

    public static Document getDocumentById(int id){
        String query = "select * from documents where id=" + Integer.toString(id);
        Document document = null;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            int localId = -1;

            Statement localStatment = connection.createStatement();
            ResultSet localResultSet;

            if(resultSet.getInt("id_av_materials") != 0){
                String localQuery = "select * from av_materials where id="+Integer.toString(resultSet.getInt("id_av_materials"));

                localResultSet = localStatment.executeQuery(localQuery);
                localResultSet.next();

                document = (AVmaterial)createAVMaterialByResultSet(localResultSet, resultSet.getInt("id"), resultSet.getString("location"));
                document.localId = resultSet.getInt("id_av_materials");
            }else if(resultSet.getInt("id_books") != 0){
                String localQuery = "select * from books where id="+Integer.toString(resultSet.getInt("id_books"));

                localResultSet = localStatment.executeQuery(localQuery);
                localResultSet.next();

                document = (Book)createBookByResultSet(localResultSet, resultSet.getInt("id"), resultSet.getString("location"));
                document.localId = resultSet.getInt("id_books");
            }else if(resultSet.getInt("id_journals") != 0){
                String localQuery = "select * from journals where id="+Integer.toString(resultSet.getInt("id_journals"));

                localResultSet = localStatment.executeQuery(localQuery);
                localResultSet.next();

                document = (Journal)createJournalByResultSet(localResultSet, resultSet.getInt("id"), resultSet.getString("location"));
                document.localId = resultSet.getInt("id_journals");
            }

        }catch (Exception e){
            System.out.println("Error in getDocumentById: " + e.toString());
        }

        return document;
    }

    /**
     * Return local id of document
     * @param documentId Global id of document which is saved in table with main information in Database
     * @return Local id of document which is saved in table with all information in Database
     */

    public static int getCorrectIdInLocalDatabase(int documentId){
        int id = -1;

        try{
            statement = connection.createStatement();
            String query = "select * from documents where id=" + Integer.toString(documentId);
            resultSet = statement.executeQuery(query);
            resultSet.next();

            if(resultSet.getInt("id_av_materials")!=0){
                id = resultSet.getInt("id_av_materials");
            }else if(resultSet.getInt("id_books")!= 0){
                id = resultSet.getInt("id_books");
            }else if(resultSet.getInt("id_journals") != 0){
                id = resultSet.getInt("id_journals");
            }

        }catch (Exception e){
            System.out.println("Error in getCorrectIdInLocalDatabase: " + e.toString());
        }

        return id;
    }

    /**
     * Return exemplar of class Patron using id from database
     * @param id Id of necessary patron from database
     * @return Exemplar of class Patron
     */

    public static Patron getPatronById(int id){
        String query = "select * from users where id="+Integer.toString(id);
        Patron user = null;
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()) {

                user = new Patron(rs.getString("name"), rs.getString("password"), rs.getString("phoneNumber"),
                        rs.getString("address"), rs.getBoolean("isFacultyMember"), rs.getInt("debt"), Patron.getCorrectPatronType(rs.getString("type")), rs.getBoolean("isActive"));
                user.id = id;
            }
        }catch (Exception e){
            System.out.println("Database get_user_by_id: " + e.toString());
        }

        return user;
    }

    /**
     * Return exemplar of class Librarian using id from database
     * @param id Id of necessary librarian from database
     * @return Exemplar of class Librarian
     */

    public static Librarian getLibrarianById(int id){
        String query = "select * from users where id="+Integer.toString(id);
        Librarian user = null;
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()) {

                user = new Librarian(rs.getString("name"), rs.getString("password"), rs.getString("phoneNumber"),
                        rs.getString("address"), Librarian.getCorrectLibrarianType(rs.getString("type")));
                user.id = id;
            }
        }catch (Exception e){
            System.out.println("Database get_user_by_id: " + e.toString());
        }

        return user;
    }

    /**
     * Check is librarian or not current user using id from database
     * @param id Id of necessary user from database
     * @return True for librarian
     */

    public static boolean isLibrarian(int id){
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE id =" + id);
            resultSet.next();
            return resultSet.getBoolean("isLibrarian");

        } catch (SQLException e) {
            System.out.println("Error is in isLibrarian method: " + e.toString());
        }
        return false;
    }

    /**
     * Check is librarian with privileges #1 or not current user using id from database
     * @param id Id of necessary user from database
     * @return True for librarian with privileges #1
     */

    public static boolean isLibrarianPriv1(int id){
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE id =" + id);
            resultSet.next();
            String type = resultSet.getString("type");
            if(Librarian.getCorrectLibrarianType(type).equals(LibrarianType.Priv1)||
                    Librarian.getCorrectLibrarianType(type).equals(LibrarianType.Priv2)||
                    Librarian.getCorrectLibrarianType(type).equals(LibrarianType.Priv3)||
                    type.equals("admin")){
                return true;
            }
        }catch (Exception e){
            System.out.println("Error in isLibrarianPriv1: "+ e.toString());
        }
        return false;
    }

    /**
     * Check is librarian with privileges #2 or not current user using id from database
     * @param id Id of necessary user from database
     * @return True for librarian with privileges #2
     */

    public static boolean isLibrarianPriv2(int id){
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE id =" + id);
            resultSet.next();
            String type = resultSet.getString("type");
            if(Librarian.getCorrectLibrarianType(type).equals(LibrarianType.Priv2)||
                    Librarian.getCorrectLibrarianType(type).equals(LibrarianType.Priv3)||
                    type.equals("admin")){
                return true;
            }
        }catch (Exception e){
            System.out.println("Error in isLibrarianPriv2: "+ e.toString());
        }
        return false;
    }

    /**
     * Check is librarian with privileges #3 or not current user using id from database
     * @param id Id of necessary user from database
     * @return True for librarian with privileges #3
     */

    public static boolean isLibrarianPriv3(int id){
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE id =" + id);
            resultSet.next();
            String type = resultSet.getString("type");
            if(Librarian.getCorrectLibrarianType(type).equals(LibrarianType.Priv3)||type.equals("admin")){
                return true;
            }
        }catch (Exception e){
            System.out.println("Error in isLibrarianPriv3: "+ e.toString());
        }
        return false;
    }

    /**
     * Check is admin or not current user using id from database
     * @param id Id of necessary user from database
     * @return True for admin
     */

    public static boolean isAdmin(int id){
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE id =" + id);
            resultSet.next();
            if(resultSet.getString("type").equals("admin")){
                return true;
            }
        }catch (Exception e){
            System.out.println("Error in isAdmin: "+ e.toString());
        }
        return false;
    }

    /**
     * Return array with all requests from users for checking out or returning documents
     */

    public static ArrayList<LibTask> getAllLibTasks(){
        String query = "select * from libtasks order by id";
        ArrayList<LibTask> ans = new ArrayList<>();
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int userid = resultSet.getInt("id_user");
                String libType = resultSet.getString("type");
                ans.add(new LibTask(getDocumentById(resultSet.getInt("id_document")), getPatronById(userid), libType, true));
            }
        }catch (Exception e){
            System.out.println("Error in database, getAllLibTasks: " + e.toString());
        }

        return ans;
    }

    /**
     * Return request from user for checking out or returning documents using resultSet
     * @param rs ResultSet
     * @return Exemplar of class LibTasks
     */

    public static LibTask getLibtaskByResultSet(ResultSet rs){
        LibTask libTask = null;
        try{
            int userid = rs.getInt("id_user");
            String libType = rs.getString("type");
            int id = rs.getInt("id");

            libTask = new LibTask(getDocumentById(rs.getInt("id_document")), getPatronById(userid), libType, true);
            libTask.id = id;
        }catch (Exception e){
            System.out.println("Error in database, getLibtaskByResultSet: " + e.toString());
        }
        return libTask;
    }

    /**
     * Check is request document or not
     * @param document Document which is needed to check
     * @return True for request document
     */

    public static boolean isRequestDocument(Document document){
        try {
            resultSet = statement.executeQuery("SELECT Count(*) AS total FROM request WHERE id_document ="+document.id);
            resultSet.next();
            return resultSet.getInt("total")>0;
        } catch (SQLException e) {
            System.out.println("Error in isRequestDocument: "+ e.toString());
        }
        return false;
    }

    /**
     * Return array with all original documents in the system
     */

    public static ArrayList<Pair<Document, Integer>> getAllDocumentsWithoutCopies(){
        ArrayList<Pair<Document, Integer>> documents = new ArrayList<>();
        try{
            String query = "select * from books order by id";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                Book book = createBookByResultSet(rs, -1, "Its dont important");
                book.localId = rs.getInt("id");
                documents.add(new Pair<Document, Integer>(book, rs.getInt("number")));
            }
            query = "select * from journals order by id";
            rs = st.executeQuery(query);
            while (rs.next()){
                Journal journal = createJournalByResultSet(rs, -1, "Its dont important");
                journal.localId = rs.getInt("id");
                documents.add(new Pair<Document, Integer>(journal, rs.getInt("number")));
            }
            query = "select * from av_materials order by id";
            rs = st.executeQuery(query);
            while (rs.next()){
                AVmaterial aVmaterial = createAVMaterialByResultSet(rs, -1, "Its dont important");
                aVmaterial.localId = rs.getInt("id");
                documents.add(new Pair<Document, Integer>(aVmaterial, rs.getInt("number")));
            }
        }catch (Exception e){
            System.out.println("Error in database, getAllDocumentsWithoutCopies: " + e.toString());
        }

        return documents;
    }

    /**
     * Retun array with all requests from users
     */

    public static ArrayList<UserRequest> getAllRequests(){
        ArrayList<UserRequest> requests = new ArrayList<>();
        try{
            String query = "select * from request order by id";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()){
                int docId = rs.getInt("id_document");
                requests.add(new UserRequest(getPatronById(rs.getInt("id_user")), getDocumentById(docId)));
            }
        }catch (Exception e){
            System.out.println("Error in database, getAllRequests: " + e.toString());
        }

        return requests;
    }

    /**
     * Return array of all checked out documents with users who check out them
     */

    public static ArrayList<Pair<Document, Patron>> getAllDocumentsWithUsers(){
        ArrayList<Pair<Document, Patron>> ans = new ArrayList<>();
        try{
            String query = "select * from booking order by id";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                ans.add(new Pair<Document, Patron>(getDocumentById(rs.getInt("document_id")), getPatronById(userId)));
            }
        }catch (Exception e){
            System.out.println("Error in database, getAllDocumentsWithUsers: " + e.toString());
        }

        return ans;
    }

    /**
     * Return array with all patrons in the system
     */

    public static ArrayList<Patron> getAllPatrons(){
        ArrayList<Patron> ans = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select id from users where isLibrarian = 0");

            while (rs.next()){
                ans.add(getPatronById(rs.getInt("id")));
            }

        }catch (Exception e){
            System.out.println("Error in getAllPatrons. db: " + e.toString());
        }
        return ans;
    }

    /**
     * Return date of returning necessary document
     * @param document Document which return date is needed
     * @return Date of returning document
     */

    public static String getDocumentReturnDate(Document document){
        String returnDate = "";
        try {
            Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT returnTime FROM booking WHERE document_id =" + document.id);
            while (rs.next()){
                returnDate = rs.getDate(1).toString();
            }

        } catch (SQLException e) {
            System.out.println("Error in getDocumentReturnDate: "+ e.toString());
        }
        return returnDate;
    }

    /**
     * Delete all information from necessary table in database
     * @param tableName Name of table which is needed to clean
     */

    public static void DeleteAllInTable(String tableName){
        try{
            ArrayList<Integer> ids = getAllTableIds(tableName);
            Statement st = connection.createStatement();
            for (int i = 0; i < ids.size(); i++) {
                st.executeUpdate("DELETE FROM " + tableName + " where id = " + ids.get(i).toString());
            }
        }catch (Exception e){
            System.out.println("Error in db, deleteAllInTable: " + e.toString());
        }
    }

    /**
     * Return array with all ids from necessary table in database
     * @param tableName Name of table which ids is needed
     */

    public static ArrayList<Integer> getAllTableIds(String tableName){
        ArrayList<Integer> ans = new ArrayList<>();

        try{
            String query = "select id from " + tableName + " order by id";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()){
                ans.add(rs.getInt("id"));
            }
        }catch (Exception e){
            System.out.println("Error in db, getAllTableIds: " + e.toString());
        }

        return ans;
    }

    /**
     * Method for executing SQL query
     * @param query String with SQL query which is needed to execute
     */

    public static void ExecuteQuery(String query){
        try{
            Statement st = connection.createStatement();
            st.executeUpdate(query);
        }catch (Exception e){
            System.out.println("Error in db, executeQuery: " + e.toString());

        }
    }

    /**
     * Select rows from database
     * @param query String with SQL query
     * @return ResultSet which from we can get information
     */

    public static ResultSet SelectFromDB(String query){
        ResultSet rs = null;
        try{
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        }catch (Exception e){
            System.out.println("Error in db, selectFromDB: " + e.toString());
        }
        return rs;
    }

    /**
     * Return id of necessary document
     * @param document Document which id is needed
     */

    public static int getFirstDocumentWithLocID(Document document){
        int ans = -1;
        try{
            String type = "id_"+Document.getParsedType(document.type);
            String query = "select * from documents where " + type + "=" + Integer.toString(document.localId);
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            rs.next();
            ans = rs.getInt("id");
        }catch (Exception e){
            System.out.println("Error in db, getFirstDocumentWithLocID: " + e.toString());
        }
        return ans;
    }

    /**
     * Check availability of necessary document's queue
     * @param document Document which queue is needed to check
     * @return True for having a queue
     */

    public static boolean hasQueue(Document document){
        boolean hasQueue = false;
        try {
            ResultSet resultSet = Database.SelectFromDB("SELECT*FROM libtasks WHERE id_document = "+ document.id);
            while (resultSet.next()){
                if (resultSet.getInt("queue") > -1){
                    hasQueue = true;
                    break;
                }
            }

        }catch (Exception e){
            System.out.println("Error in hasQueue: "+ e.toString());
        }
        return hasQueue;
    }

    /**
     * Return array with users which is in queue of necessary document
     * @param document Document which queue is needed
     */

    public static ArrayList<Patron> getDocumentQueue(Document document){
        ArrayList<Patron> patrons = new ArrayList<>();
        try{
            ResultSet rs = SelectFromDB("select * from libtasks where type = 'checkout' and id_document = " + Integer.toString(document.id) + " order by id");
            while (rs.next()){
                patrons.add(getPatronById(rs.getInt("id_user")));
            }
        }catch (Exception e){
            System.out.println("Error in getDocumentQueue: "+ e.toString());
        }
        return patrons;
    }

    /**
     * Check possibility to renew necessary document
     * @param patron User who want to renew document
     * @param document Document which is renewed
     */

    public static boolean isCanRenew(Patron patron, Document document){
        boolean isCanRenew = false;
        try{
            //Get line from Booking
            statement.executeQuery("SELECT*FROM booking WHERE document_id = '" + document.id + "'");

            //Check can we renew book
            boolean isRenew = false;
            ResultSet rec = statement.getResultSet();
            if (rec.next()) {
                isRenew = rec.getBoolean("is_renew");
            }

            //Get line from Users
            String typeUser = "";
            statement.executeQuery("SELECT type FROM users WHERE id = "+ patron.id);
            rec = statement.getResultSet();
            while (rec.next()){
                typeUser = rec.getString("type");
            }
            if (!isRenew){
                isCanRenew = true;
            }

        }catch (Exception e){
            System.out.println("Error in isCanRenew: "+ e.toString());
        }
        return isCanRenew;
    }

    /**
     * Return librarian using number from database
     * @param login String with number from database which is used as login
     * @return Exemplar of class Librarian
     */

    public static Librarian getLibrarianByNumber(String login){
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from users where phoneNumber=" + login);
            rs.next();

            Librarian librarian = new Librarian(rs.getString("name"), rs.getString("password"), rs.getString("phoneNumber"),
                    rs.getString("address"), Librarian.getCorrectLibrarianType(rs.getString("type")));

            librarian.id = rs.getInt("id");

            return librarian;
        }catch (Exception e){
            System.out.println("Error in getting user by login " + e.toString());
            return new Librarian();
        }
    }

    /**
     * Find document in database
     * @param searchGoal String with necessary search goal
     * @param colomn Name of column in database's table where we search
     * @return Array with documents depending on search criteria
     */

    public static ArrayList<Document> findDocuments(String searchGoal, String colomn){
        ArrayList <Document> docs = new ArrayList<>();
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from books where " + colomn +" = '" + searchGoal + "';");
            while (rs.next()){
                Statement nst = connection.createStatement();
                ResultSet nrs = nst.executeQuery("select * from documents where id_books = " + Integer.toString(rs.getInt("id")));

                while(nrs.next()) {
                    docs.add(createBookByResultSet(rs, nrs.getInt("id"), ""));
                }
            }
            rs.close();

            rs = st.executeQuery("select * from journals where " + colomn +" = '" + searchGoal + "';");
            while (rs.next()){
                Statement nst = connection.createStatement();
                ResultSet nrs = nst.executeQuery("select * from documents where id_journals = " + Integer.toString(rs.getInt("id")));

                while(nrs.next()) {
                    docs.add(createJournalByResultSet(rs, nrs.getInt("id"), ""));
                }
            }
            rs.close();

            rs = st.executeQuery("select * from av_materials where " + colomn +" = '" + searchGoal + "';");
            while (rs.next()){
                Statement nst = connection.createStatement();
                ResultSet nrs = nst.executeQuery("select * from documents where id_av_materials = " + Integer.toString(rs.getInt("id")));

                while(nrs.next()) {
                    docs.add(createAVMaterialByResultSet(rs, nrs.getInt("id"), ""));
                }
            }
            rs.close();
        }catch (Exception e){
            System.out.println("Error in findDocument: " + e.toString());
        }

        return docs;
    }

    /**
     * Find patron in database
     * @param searchGoal String with necessary search goal
     * @param colomn Name of column in database's table where we search
     * @return Array with patrons depending on search criteria
     */

    public static Patron findPatronBy(String searchGoal, String colomn){
        Patron patron = null;
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from users where " + colomn + " = '" + searchGoal + "';");
            rs.next();
            patron = getPatronById(rs.getInt("id"));
        }catch (Exception e){
            System.out.println("Error in findPatronBy: " + e.toString());
        }
        return patron;
    }

    /**
     * Delete all information from database and create admin
     */

    public static void DeleteAllFromDBAndCreateAdmin(){
        try{
            String[] tables = {"av_materials",  "books", "journal_articles", "journals", "libtasks", "request", "users", "documents", "booking"};
            for (int i = 0; i < tables.length; i++) {
                DeleteAllInTable(tables[i]);
            }
            Database.ExecuteQuery("INSERT INTO `users` (`id`, `name`, `phoneNumber`, `address`, `debt`, `isFacultyMember`, `password`, `isLibrarian`, `type`) VALUES ('1', 'All cash', '1', '1', '0', b'0', '1', b'1', 'admin');");

            System.out.println("All deleted");
        }catch (Exception e){
            System.out.println("Error in DeleteAllFromDBAndCreateLibrarian: " + e.toString());
        }
    }

    /**
     * Send outstanding request
     * @param document Document on which librarian send outstanding request
     * @param librarian Id of librarian who want to send request to check his privileges
     */

    public static void sendOutstandingRequest(Document document, Librarian librarian) {
        try {
            String logText = "outstanding request on document "+ document.name +": ";
            if(librarian.type.equals(LibrarianType.Priv2)){
                logText+="accepted";
                ResultSet resultSet = Database.SelectFromDB("SELECT*FROM libtasks WHERE id_document = " + Integer.toString(document.id) + " and type = 'checkout'");
                Integer id_user;
                Integer id_document;
                boolean mark = true;
                while (resultSet.next()) {
                    id_user = resultSet.getInt("id_user");
                    id_document = resultSet.getInt("id_document");
                    if (id_user != null && id_document != null) {
                        Logging.CreateLog("removed from queue", librarian.id);
                        Database.ExecuteQuery("INSERT INTO request SET id_user = " + id_user + ", id_document = " + id_document + ", message = '" + RequestsText.removed_queue_en + "'");
                    } else {
                        mark = false;
                    }
                }
                if (mark) {
                    Database.ExecuteQuery("DELETE FROM libtasks WHERE id_document = " + Integer.toString(document.id) + " and type = 'checkout'");
                    Logging.CreateLog("waiting list for document " + document.name + " deleted", librarian.id);

                    resultSet = Database.SelectFromDB("SELECT*FROM booking WHERE document_id = " + Integer.toString(document.id));
                    while (resultSet.next()) {
                        Database.ExecuteQuery("INSERT INTO request SET id_user = " + resultSet.getInt("user_id") + ", id_document = " + resultSet.getInt("document_id") + ", message = '" + RequestsText.return_book_en + "'");
                        Logging.CreateLog("notified to return book", librarian.id);
                    }
                }

                ArrayList<Integer> documentIds = new ArrayList<>();
                ResultSet rs = Database.SelectFromDB("select * from documents where id_" + document.type.toString() + "s = " + Integer.toString(document.localId));
                while (rs.next()) {
                    documentIds.add(rs.getInt("id"));
                }

                Statement st = Database.connection.createStatement();

                java.util.Date date = new java.util.Date();
                if (CurrentSession.setDate != 0L)
                    date.setTime(CurrentSession.setDate);
                java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

                for (int i = 0; i < documentIds.size(); i++) {
                    st.executeUpdate("UPDATE booking set returnTime = '" + timestamp + "', is_renew = '" + 1 + "' WHERE document_id = '" + documentIds.get(i) + "'");
                }
            }else{
                logText+="request was denied.";
            }
            Logging.CreateLog(logText, librarian.id);

        } catch (Exception e) {
            System.out.println("Error in sendOutstandingRequest: " + e.toString());
        }
    }

    /**
     * Upgrade user to librarian or make librarian with higher privileges
     * @param user User which we want to upgrade
     * @param type String with type with which we want new user
     * @param idLibrarian Id librarian to check his privileges
     */

    public static void upgradeToLibrarian(User user, String type, int idLibrarian) {
        if(Database.isAdmin(idLibrarian)) {
            try {
                if (user instanceof Patron) {
                    if (Database.getUserDocuments((Patron) user).isEmpty()) {
                        Database.ExecuteQuery("UPDATE users set isFacultyMember = 0, isLibrarian = 1, type = '" + type + "', isActive = 0 WHERE id = '" + user.id + "'");
                    }
                    else{
                        System.out.println("Error in upgradeToLibrarian: user has documents");
                    }
                } else {
                    Database.ExecuteQuery("UPDATE users set type = '" + type + "' WHERE id = '" + user.id + "'");
                }
            } catch (Exception e) {
                System.out.println("Error in upgradeToLibrarian: " + e.toString());
            }
        }
        else
        {
            System.out.println("Error in upgradeToLibrarian: user doesn't have access");
        }
    }

    /**
     * Search document in database
     * @param goal String with goal of searching
     * @param colomn Name of table's column where we search
     * @return Array with documents depending on searching criteria
     */

    public static ArrayList<String> searchInDocuments(String goal, String colomn){
        ArrayList <String> docs = new ArrayList<>();
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * from books");

            ArrayList<Pair<String, String>> names = new ArrayList<>();
            while (rs.next()){
                names.add(new Pair<>(rs.getString("title"), rs.getString(colomn)));
            }

            if(goal.contains("AND")){
                String[] goals = goal.split(" AND ");
                goals[0] = goals[0].substring(1, goals[0].length());
                for (int i = 0; i < names.size(); i++) {
                    boolean flag = true;
                    for (int j = 0; j < goals.length; j++) {
                        if (!names.get(i).second.contains(goals[j])) {
                            flag = false;
                        }
                    }
                    if(flag){
                        docs.add(names.get(i).first);
                    }

                }
            }else if(goal.contains("OR")){
                String[] goals = goal.split(" OR ");
                goals[0] = goals[0].substring(1, goals[0].length());
                for (int i = 0; i < names.size(); i++) {
                    boolean flal = false;
                    for (int j = 0; j < goals.length; j++) {
                        if(names.get(i).second.contains(goals[j])){
                            flal=true;
                        }
                    }
                    if(flal){
                        docs.add(names.get(i).first);
                    }
                }
            }else {
                for (int i = 0; i < names.size(); i++) {
                    if (names.get(i).second.contains(goal)) {
                        docs.add(names.get(i).first);
                    }
                }
            }


        }catch (Exception e){
            System.out.println("Error in searchInDocuments: " + e.toString());
        }

        return docs;
    }

}


