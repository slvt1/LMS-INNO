public class CurrentSession {
    public static User user;
    public static Patron editUser;
    public static Document editDocument;

    public static void CurrentWork(){
        if(Database.isLibrarian(user.id)){
            EventManager em = new EventManager();
            em.checkRequest();
        }
    }
}
