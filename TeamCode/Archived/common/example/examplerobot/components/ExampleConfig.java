package org.murraybridgebunyips.bunyipslib.example.examplerobot.components;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.InchesPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.RadiansPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.murraybridgebunyips.bunyipslib.RobotConfig;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.DriveConstants;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.TankCoefficients;

/**
 * Example code for a robot configuration class under BunyipsOpMode ecosystem.
 */
// Extend the `RobotConfig` class when making a new config, as shown below.
public class ExampleConfig extends RobotConfig {
    // Here you will need to declare all of your instance variables, such as motors, servos, etc.
    // For example, if you have a motor called `leftMotor`, you will need to declare it like this:
    /**
     * Specification of the port this device is on.
     */
    public DcMotorEx leftFrontMotor;
    /**
     * Specification of the port this device is on.
     */
    public DcMotorEx leftBackMotor;
    /**
     * Specification of the port this device is on.
     */
    public DcMotorEx rightFrontMotor;
    /**
     * Specification of the port this device is on.
     */
    public DcMotorEx rightBackMotor;
    /**
     * Specification of the port this device is on.
     */
    public DcMotorEx liftMotor;
    /**
     * Specification of the port this device is on.
     */
    public WebcamName webcam;
    /**
     * Specification of the port this device is on.
     */
    public IMU imu;
    // Put any other motors, servos, etc. here.
    // e.g. public Servo leftServo;
    //      public WebcamName webcam;

    // RoadRunner configuration
    /**
     * RoadRunner drive constants.
     */
    public DriveConstants driveConstants;
    /**
     * RoadRunner TankDrive coefficients.
     */
    public TankCoefficients coefficients;
    // See Wheatley or GLaDOS for mecanum+deadwheel configurations

    // The onRuntime() function is required by RobotConfig to be implemented.
    // In the init function, you will initialise all your hardware with the handy getHardware()
    // function that is available for you to use.
    // This will map your instance variables (above) to the actual hardware you have named in
    // the Driver Station application.
    @Override
    protected void onRuntime() {
        // Find a Driver Station configuration name called 'left_motor' and assign it to the leftMotor instance variable
        // getHardware will return a type of HardwareDevice, and we can cast it to the type we want.
        // In this case, we want a DcMotor, so we cast it to a DcMotor.
        leftFrontMotor = getHardware("left_front_motor", DcMotorEx.class);
        // Repeat for all other hardware
        leftBackMotor = getHardware("left_back_motor", DcMotorEx.class);
        rightFrontMotor = getHardware("right_front_motor", DcMotorEx.class);
        rightBackMotor = getHardware("right_back_motor", DcMotorEx.class);
        liftMotor = getHardware("lift_motor", DcMotorEx.class);
        webcam = getHardware("webcam", WebcamName.class);

        // The reason we do it like this is that any errors will be caught and the program will not crash.
        // Additionally, telemetry will be sent to the Driver Station application to tell you what went wrong.

        // Perform any robot initialisation here, including IMU configuration, motor direction etc.
        // e.g leftMotor.setDirection(DcMotor.Direction.REVERSE);

        // Example IMU configuration
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        );
        imu.initialize(parameters);

        // These constants will need to be tuned for your robot
        driveConstants = new DriveConstants.Builder()
                .setTicksPerRev(0)
                .setMaxRPM(0)
                .setRunUsingEncoder(false)
                .setWheelRadius(Units.Inches.zero())
                .setGearRatio(0)
                .setTrackWidth(Units.Inches.zero())
                // ((MAX_RPM / 60) * GEAR_RATIO * WHEEL_RADIUS * 2 * Math.PI) * 0.85
                .setMaxVel(Units.InchesPerSecond.zero())
                .setMaxAccel(Units.InchesPerSecond.per(Units.Second).zero())
                .setMaxAngVel(Units.RadiansPerSecond.zero())
                .setMaxAngAccel(Units.RadiansPerSecond.per(Units.Second).zero())
                .setKV(0)
                .setKStatic(0)
                .setKA(0)
                .build();

        coefficients = new TankCoefficients.Builder()
                .build();

        // Do the same for your localizer if any (see GLaDOS)
    }
}
