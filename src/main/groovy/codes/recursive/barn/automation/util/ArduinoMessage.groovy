package codes.recursive.barn.automation.util

class ArduinoMessage {

    public static int ARDUINO_MOTOR_O = 0
    public static int ARDUINO_MOTOR_1 = 1
    public static int ARDUINO_DOOR_0 = 10
    public static int ARDUINO_DOOR_1 = 11
    public static int ARDUINO_RELAY_0 = 20
    public static int ARDUINO_WATER_0 = 30
    public static int ARDUINO_CAMERA_0 = 40
    public static int ARDUINO_DOOR_LED_0 = 50
    public static int ARDUINO_DOOR_LED_1 = 51
    public static int ARDUINO_OPEN = 100
    public static int ARDUINO_CLOSE = 101
    public static int ARDUINO_ON = 200
    public static int ARDUINO_OFF = 201

    public static String MOTOR_O = 'MOTOR_O'
    public static String MOTOR_1 = 'MOTOR_1'
    public static String DOOR_0 = 'DOOR_0'
    public static String DOOR_1 = 'DOOR_1'
    public static String RELAY_0 = 'RELAY_0'
    public static String WATER_0 = 'WATER_0'
    public static String CAMERA_0 = 'CAMERA_0'
    public static String DOOR_LED_0 = 'DOOR_LED_0'
    public static String DOOR_LED_1 = 'DOOR_LED_1'
    public static String OPEN = 'OPEN'
    public static String CLOSE = 'CLOSE'
    public static String ON = 'ON'
    public static String OFF = 'OFF'

    int type
    String message

    ArduinoMessage(type, message) {
        this.type = type
        this.message = message
    }

    @Override
    String toString() {
        return "[type: ${type}, message: ${message}]"
    }

}
