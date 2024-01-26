import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.methods.DirectMethodRequestOptions;
import com.microsoft.azure.sdk.iot.service.methods.DirectMethodsClient;
import org.example.Properities;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class RemoteControlTemp extends JFrame{

    private JTextArea jTextAreaLog;
    private JTextField jLabelStatus;
    private JButton jButtonTemp;
    private JButton jButtonAr;
    private JLabel jLabelTemp;
    private JLabel jLabelAr;
    private JPanel jPanelMain;
    private JSlider jSliderTempDesired;
    private JLabel jLabelDesiredTemp;
    private JButton jButtonClear;

    private DirectMethodsClient client;

    public RemoteControlTemp(String title){
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(jPanelMain);
        this.pack();
        client = new DirectMethodsClient(Properities.OWNER);


        jSliderTempDesired.setMinimum(16);
        jSliderTempDesired.setMaximum(24);
        jSliderTempDesired.setPaintTicks(true);
        jSliderTempDesired.setPaintLabels(true);
        jSliderTempDesired.setMajorTickSpacing(1);
        jSliderTempDesired.setMinorTickSpacing(1);
        jLabelDesiredTemp.setText("Temperatura desejada: " + jSliderTempDesired.getValue());


        jSliderTempDesired.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jLabelDesiredTemp.setText("Temperatura desejada: " + jSliderTempDesired.getValue());
            }
        });

        jButtonTemp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                DirectMethodRequestOptions options = DirectMethodRequestOptions.builder()
                        .payload(jSliderTempDesired.getValue())
                        .methodResponseTimeoutSeconds(5)
                        .methodConnectTimeoutSeconds(5)
                        .build();

                try {
                    client.invoke(Properities.DEVICEID, Properities.INVOKE_ID, "setTemp", options);
                } catch (IotHubException | IOException ex) {
                    throw new RuntimeException(ex);
                }

                jTextAreaLog.append("Temperatura desejada: " + jSliderTempDesired.getValue() + "\n");
            }
        });

        jButtonAr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (Objects.equals(jButtonAr.getText(), "DESLIGAR")){

                    DirectMethodRequestOptions options = DirectMethodRequestOptions.builder()
                            .payload(jSliderTempDesired.getValue())
                            .methodResponseTimeoutSeconds(30)
                            .methodConnectTimeoutSeconds(30)
                            .build();

                    try {
                        client.invoke(Properities.DEVICEID, Properities.INVOKE_ID, "desligar", options);
                    } catch (IotHubException | IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    jButtonAr.setText("LIGAR");
                    jLabelStatus.setText("DESLIGADO");
                    jTextAreaLog.append("Ar desligado \n");
                } else {

                    DirectMethodRequestOptions options = DirectMethodRequestOptions.builder()
                            .payload(jSliderTempDesired.getValue())
                            .methodResponseTimeoutSeconds(30)
                            .methodConnectTimeoutSeconds(30)
                            .build();

                    try {
                        client.invoke(Properities.DEVICEID, Properities.INVOKE_ID, "ligar", options);
                    } catch (IotHubException | IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    jButtonAr.setText("DESLIGAR");
                    jLabelStatus.setText("LIGADO");
                    jTextAreaLog.append("Ar ligado \n");
                }
            }
        });

        jButtonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextAreaLog.setText("");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new RemoteControlTemp("Remote Control Temperature");
        frame.setVisible(true);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
