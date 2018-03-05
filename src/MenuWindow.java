import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;

public class MenuWindow extends JFrame {
    private JButton takeBook = new JButton("Take Book");
    private JButton myBooks = new JButton("My Books");
    private JButton messagesButton = new JButton("Messages");

    /**
     * creating menu window GUI
     */
    public MenuWindow(){
        JFrame menuWindow = new JFrame();
        menuWindow.setBounds(100, 100, 250, 150);
        menuWindow.setLocationRelativeTo(null);
        menuWindow.setResizable(false);
        menuWindow.setTitle("Sign Up");
        menuWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container containerM = menuWindow.getContentPane();
        containerM.setLayout(new GridLayout(3, 1, 2, 2));
        containerM.add(takeBook);
        containerM.add(myBooks);
        containerM.add(messagesButton);
        takeBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                TakeBook books = new TakeBook();
            }
        });
        myBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                MyBooksGUI myBooksGUI = new MyBooksGUI();
            }
        });
        messagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserMessagesGUI userMessagerGUI = new UserMessagesGUI();
            }
        });
        menuWindow.setVisible(true);
    }
}
