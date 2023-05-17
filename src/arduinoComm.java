import jssc.*;

/**
 * Clase comunicación arduino
 */
public class arduinoComm {

    public void comm() {
        try {
            SerialPort puerto = new SerialPort("COM4");
            puerto.openPort();
            puerto.setParams(SerialPort.BAUDRATE_19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }
}