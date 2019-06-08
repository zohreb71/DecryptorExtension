package burp;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender, IMessageEditorTabFactory , ITab{

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private ConfigurationGUI configurationTabGui;
    private static String encryptionAlg;
    List headers;
    byte[] msgBody;
    private static String secretKey ;
    private static String iv ;
    private AES aes;
    private DES des;

    public static void setSecretKey(String key) {
        secretKey = key;
    }

    public static void setIv(String IV) {
        iv = IV;
    }

    public static void setEncryptionAlg(String encAlg) {
        BurpExtender.encryptionAlg = encAlg;
    }

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {

        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        this.callbacks.issueAlert("Decryptor Extension Loaded Successfully.");
        stdout = new PrintWriter(callbacks.getStdout(),true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                callbacks.setExtensionName("Decryptor Extension");
                callbacks.registerMessageEditorTabFactory(BurpExtender.this);
            }
        });
        configurationTabGui = new ConfigurationGUI();
        callbacks.addSuiteTab(this);

    }

    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
        return new Decryption(controller, editable);
    }

    @Override
    public String getTabCaption() {
        return "Decryptor Configuration";
    }

    @Override
    public Component getUiComponent() {
        return configurationTabGui;
    }

    class Decryption implements IMessageEditorTab {
        private boolean editable;
        private ITextEditor txtInput;
        private byte[] currentMessage;

        public Decryption(IMessageEditorController controller, boolean editable)
        {
            this.editable = editable;
            txtInput = callbacks.createTextEditor();
            txtInput.setEditable(editable);
        }

        @Override
        public String getTabCaption() {
            return "Decryption";
        }


        @Override
        public Component getUiComponent()
        {
            return txtInput.getComponent();
        }

        @Override
        public boolean isEnabled(byte[] content, boolean isRequest)
        {
            return true;
        }

        @Override
        public void setMessage(byte[] content, boolean isRequest){
            String newBody = "";
            String messageBody;
            IRequestInfo reqInfo = helpers.analyzeRequest(content);
            headers =  reqInfo.getHeaders();

            byte[] body = Arrays.copyOfRange(content,reqInfo.getBodyOffset(),content.length);
            messageBody = new String(body);
            
            try {
                newBody = decrypt(messageBody);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] body2 = newBody.getBytes();

            if (content == null)
            {
                txtInput.setText(null);
                txtInput.setEditable(false);
            }
            else
            {
                txtInput.setText(body2);
                txtInput.setEditable(editable);
            }

            currentMessage = content;

        }

        @Override
        public byte[] getMessage() {
            String res = "";
            if (txtInput.isTextModified()) {
                byte[] input = txtInput.getText();

                try {
                    res = encrypt(new String (input));
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                msgBody = res.getBytes();
                return helpers.buildHttpMessage(headers,msgBody);
            }
            else return currentMessage;
        }

        @Override
        public boolean isModified()
        {
            return txtInput.isTextModified();
        }

        @Override
        public byte[] getSelectedData()
        {
            return txtInput.getSelectedText();
        }
    }



    public String decrypt(String message) throws Exception {
        message = message.replace("\n","");
        switch (encryptionAlg) {
            case "AES":{
                aes = new AES (secretKey.getBytes(),iv);
                return aes.decrypt(message);
            }
            case "DES": {
                des = new DES(secretKey.getBytes());
                return des.decrypt(message);
            }
        }
        return "";
    }

    private String encrypt(String message) throws Exception {
        switch (encryptionAlg) {
            case "AES": {
                aes = new AES (secretKey.getBytes(),iv);
                aes.encrypt(message);
            }
            case "DES":{
                des = new DES(secretKey.getBytes());
                des.encrypt(message);
            }
        }
        return "";
    }

}
