import java.awt.*;
import java.awt.event.*;
import javax.print.DocFlavor;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

public class TakeBook extends JFrame{
    private JList<String> allBooks;
    private JButton takingBook = new JButton("Take Book");

    /**
     * creating take book menu GUI
     */
    public TakeBook() {

        try {

            JFrame takeBook = new JFrame();
            takeBook.setBounds(100, 100, 250, 200);
            takeBook.setLocationRelativeTo(null);
            takeBook.setResizable(false);
            takeBook.setTitle("Take Book");
            takeBook.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            Container containerTB = takeBook.getContentPane();
            containerTB.setLayout(new BorderLayout());

            ArrayList<Integer> documentIds = Database.getAllDocumentsIDs();

            String[] columnNames = {"Document", "Amount"};
            ArrayList<Pair<Document, Integer>> requests = Database.getAllDocumentsWithoutCopies();
            Object[][] requestA = new Object[requests.size()][2];
            for(int i = 0; i < requestA.length; i++){
                requestA[i][1] = requests.get(i).second;
                requestA[i][0] = requests.get(i).first.name;
            }

            JTable table = new JTable(requestA, columnNames);
            JScrollPane listScroller = new JScrollPane(table);
            listScroller.setPreferredSize(new Dimension(100,100));
            containerTB.add(listScroller, BorderLayout.CENTER);

            takingBook.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = table.getSelectedRow();
                    if(index != -1){
                        int amountOfDays = -1;

                        String time = "";

                        EventManager eventManager = new EventManager();
                        eventManager.CreateQuery(new LibTask(new AVmaterial(documentIds.get(index)), (Patron)CurrentSession.user, "checkout"));
                        time = "Wait, your decision has been sent";

                        JOptionPane.showMessageDialog(null, time, "", JOptionPane.PLAIN_MESSAGE);

                        takeBook.setVisible(false);
                    } else{
                        String message = "Select a book!\n";
                        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });
            takingBook.setPreferredSize(new Dimension(250, 40));
            containerTB.add(takingBook, BorderLayout.SOUTH);
            takeBook.setVisible(true);
        }catch (Exception e){
            System.out.println("Error in takebook " + e.toString());
        }
    }

}
