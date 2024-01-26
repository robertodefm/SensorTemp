open module pt.tpsi.ad.pi4j {
    // Pi4J Modules
    requires com.pi4j;
    requires com.pi4j.library.pigpio;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.plugin.raspberrypi;
    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    // Logging
    requires java.logging;

    // PicoCLI Modules
    requires info.picocli;

    // AWT
    requires java.desktop;
}
