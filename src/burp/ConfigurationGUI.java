package burp;

import java.awt.event.*;
import javax.swing.*;
/**
 * @author bohluli
 */
public class ConfigurationGUI extends javax.swing.JPanel {

    private JLabel algLable, sKeyLabel, ivLabel;
    private JTextField sKeyField , ivField;
    private JButton submitsKey, submitIV;
    private JComboBox algsComboBox;

    public ConfigurationGUI() {
        algLable = new JLabel("Encryption Algorithim:");
        sKeyLabel = new JLabel("Secret Key:");
        ivLabel = new JLabel("IV:");
        sKeyField = new JTextField();
        ivField = new JTextField();
        submitsKey = new JButton("Submit Secret Key");
        submitIV = new JButton("Submit IV");
        String[] algs = {"AES", "DES"};
        algsComboBox = new JComboBox(algs);

        submitsKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aesRawKey = sKeyField.getText();
                BurpExtender.setSecretKey(aesRawKey);
            }
        });

        submitIV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String iv = ivField.getText();
                BurpExtender.setIv(iv);
            }
        });

        algsComboBox.addItemListener(new ItemChangeListener());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(algLable)
                                .addComponent(sKeyLabel)
                                .addComponent(ivLabel))

                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(algsComboBox, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                                .addComponent(sKeyField, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                                .addComponent(ivField, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(submitsKey)
                                .addComponent(submitIV)));
        layout.linkSize(SwingConstants.HORIZONTAL, submitsKey, submitIV);


        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(algLable)
                        .addComponent(algsComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(sKeyLabel)
                        .addComponent(sKeyField)
                        .addComponent(submitsKey))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(ivLabel)
                        .addComponent(ivField)
                        .addComponent(submitIV)));
    }

    class ItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object item = event.getItem();
                switch (item.toString()) {
                    case "AES":
                        BurpExtender.setEncryptionAlg("AES");
                        break;
                    case "DES":
                        BurpExtender.setEncryptionAlg("DES");
                        break;
                }
            }
        }
    }

}
