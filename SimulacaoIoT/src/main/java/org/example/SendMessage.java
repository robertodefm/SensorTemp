package org.example;



import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodPayload;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodResponse;
import com.microsoft.azure.sdk.iot.device.twin.MethodCallback;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.messaging.IotHubServiceClientProtocol;
import com.microsoft.azure.sdk.iot.service.messaging.Message;
import com.microsoft.azure.sdk.iot.service.messaging.MessagingClient;
import com.microsoft.azure.sdk.iot.service.messaging.*;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


public class SendMessage {

    private boolean active;
    private double desiredTemp;

    private MessagingClient messagingClient;

    private ModuleClient moduleClient;

    public SendMessage(){
        this.active=false;
        this.desiredTemp=20;
        messagingClient = new MessagingClient(Properities.OWNER, IotHubServiceClientProtocol.AMQPS);
        moduleClient = new ModuleClient(Properities.INVOKE_CONN, IotHubClientProtocol.AMQPS);
    }

    public double getDesiredTemp() {
        return desiredTemp;
    }

    public boolean getActive(){
        return active;
    }

    public void setDesiredTemp(double desiredTemp) {
        this.desiredTemp = desiredTemp;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



    public static void main(String[] args) throws IOException, InterruptedException, IotHubException, TimeoutException, IotHubClientException {

        SendMessage client = new SendMessage();
        System.out.println("Successfully created an IoT Hub client.");

        Timer timer = new Timer();

        client.messagingClient.open();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (client.getActive()){
                    String statusACC = "";
                    double temperature = 20 + Math.random() * 10;
                    double humidity = 30 + Math.random() * 20;
                    double desiredTemp = client.getDesiredTemp();

                    ZoneId zonaPortugal = ZoneId.of("Europe/Lisbon");

                    ZonedDateTime dataTime = ZonedDateTime.now(zonaPortugal);


                    int dia = dataTime.getDayOfMonth();
                    int hora = dataTime.getHour();
                    int minutos = dataTime.getMinute();
                    int segundos = dataTime.getSecond();

                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String dataFormateada = dataTime.format(formato);


                    if (temperature > desiredTemp) {
                        statusACC = "Ligado";
                    } else statusACC = "Desligado";


                    String msgStr = "{\"Temperatura atual \":"+ temperature +",\"Humedade atual\":"+ humidity +",\"Temperatura desejada\":"+desiredTemp+",\"Ar acondicionado\":"+statusACC+",\"Data\":"+dataFormateada+"}";

                    Message msg = new Message(msgStr);


                    try {
                        client.messagingClient.send(Properities.DEVICEID, Properities.TELEMETRY_ID, msg);
                    } catch (InterruptedException | IotHubException | TimeoutException e) {
                        return;
                    }
                }
            }
        }, 0, 3000);


        client.moduleClient.open(true);
        System.out.println("Successfully created an IoT Hub client.");

        try {
            System.out.println("Opened connection to IoT Hub.");


            client.moduleClient.subscribeToMethods(new MethodCallback() {
                @Override
                public DirectMethodResponse onMethodInvoked(String methodName, DirectMethodPayload methodData, Object context) {
                    if (methodName.equals("setTemp")){
                        client.setDesiredTemp(Double.parseDouble(methodData.getPayloadAsJsonString()));
                    }  else if (methodName.equals("ligar")) {
                        client.setActive(true);
                    } else if (methodName.equals("desligar")) {
                        client.setActive(false);
                    }
                    return new DirectMethodResponse(200, methodData);
                }
            }, null);
        } catch (IotHubClientException e) {
            System.out.println("Failed to subscribe to direct methods. Error code: " + e.getStatusCode());
            client.moduleClient.close();
            System.out.println("Shutting down...");
            return;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
