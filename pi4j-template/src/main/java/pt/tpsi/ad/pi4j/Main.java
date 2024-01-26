package pt.tpsi.ad.pi4j;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class Main {

    private static final int PIN_BUTTON = 24; // PIN 18 = BCM 24
    private static final int PIN_LED = 22; // PIN 15 = BCM 22

    public static void main(String[] args) throws InterruptedException {

        var pi4j = Pi4J.newAutoContext();
        System.out.println("CREATED PI4J Context");

        // Shutdown Pi4J
        pi4j.shutdown();
        System.out.println("CLOSED PI4J Context");
    }

}
