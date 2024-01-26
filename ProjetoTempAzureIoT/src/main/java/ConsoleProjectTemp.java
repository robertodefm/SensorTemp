import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.example.Properities;

import javax.swing.*;

public class ConsoleProjectTemp extends JFrame {
    private JTextArea JTextAreaLogs;
    private JButton jButtonClear;
    private JButton jButtonSave;
    private JPanel mainPanel;

    private com.microsoft.azure.sdk.iot.device.MessageCallback MessageCallback;

    protected static class IotHubConnectionStatusChangeCallbackLogger implements IotHubConnectionStatusChangeCallback
    {
        @Override
        public void onStatusChanged(ConnectionStatusChangeContext connectionStatusChangeContext)
        {
            IotHubConnectionStatus status = connectionStatusChangeContext.getNewStatus();
            IotHubConnectionStatusChangeReason statusChangeReason = connectionStatusChangeContext.getNewStatusReason();
            Throwable throwable = connectionStatusChangeContext.getCause();

            System.out.println();
            System.out.println("CONNECTION STATUS UPDATE: " + status);
            System.out.println("CONNECTION STATUS REASON: " + statusChangeReason);
            System.out.println("CONNECTION STATUS THROWABLE: " + (throwable == null ? "null" : throwable.getMessage()));
            System.out.println();

            if (throwable != null)
            {
                throwable.printStackTrace();
            }

            if (status == IotHubConnectionStatus.DISCONNECTED)
            {
                System.out.println("The connection was lost, and is not being re-established." +
                        " Look at provided exception for how to resolve this issue." +
                        " Cannot send messages until this issue is resolved, and you manually re-open the device client");
            }
            else if (status == IotHubConnectionStatus.DISCONNECTED_RETRYING)
            {
                System.out.println("The connection was lost, but is being re-established." +
                        " Can still send messages, but they won't be sent until the connection is re-established");
            }
            else if (status == IotHubConnectionStatus.CONNECTED)
            {
                System.out.println("The connection was successfully established. Can send messages.");
            }
        }
    }

    public ConsoleProjectTemp(String title) throws IotHubClientException{
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        JTextAreaLogs.setText("");

        MessageCallback = new com.microsoft.azure.sdk.iot.device.MessageCallback(){
            @Override
            public IotHubMessageResult onCloudToDeviceMessageReceived(Message msg, Object context)
            {
                JTextAreaLogs.append(new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET) + "\n");
                System.out.println(
                        "Received message with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

                return IotHubMessageResult.COMPLETE;
            }

        };
        IotHubClientProtocol protocol = IotHubClientProtocol.AMQPS;

        ModuleClient client = new ModuleClient(Properities.TELEMETRY_MODULE,protocol);

        System.out.println("Successfully created an IoT Hub client.");

        client.open(true);

        client.setMessageCallback(MessageCallback, null);

        System.out.println("Successfully set message callback.");

        client.setConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());

        client.open(true);

        System.out.println("Opened connection to IoT Hub. Messages sent to this device will now be received.");

        System.out.println("Press any key to exit...");


        jButtonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JTextAreaLogs.setText("");
            }
        });

        jButtonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                saveLogsToFile();
            }
        });

    }

    private void saveLogsToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("logs.txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

                writer.write(JTextAreaLogs.getText());

                JOptionPane.showMessageDialog(this, "Ficheiro guardado exitosamente.", "Guardar Ficheiro", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao guardar o ficheiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main (String[] args) throws IotHubClientException {
        JFrame frame = new ConsoleProjectTemp("ConsoleProjectTemp");
        frame.setVisible(true);


    }
}
