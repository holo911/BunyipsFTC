package org.murraybridgebunyips.proto;

import org.murraybridgebunyips.bunyipslib.RobotConfig;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.DegreesPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.MetersPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.MetersPerSecondPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Millimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.murraybridgebunyips.bunyipslib.Dbg;
import org.murraybridgebunyips.bunyipslib.Motor;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.DriveConstants;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.MecanumCoefficients;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.localizers.TwoWheelLocalizer;
import org.murraybridgebunyips.bunyipslib.roadrunner.util.Deadwheel;

/**
 * FTC 15215 INTO THE DEEP 2024-2025 robot configuration
 *
 * @author Lucas Bubner, 2024
 */
public class Proto extends RobotConfig {
    /**
     * Expansion 0: Front Left "fl"
     */
    public Motor frontLeft;
    /**
     * Expansion 1: Front Right "fr"
     */
    public Motor frontRight;
    /**
     * Expansion 2: Back Right "br"
     */
    public Motor backRight;
    /**
     * Expansion 3: Back Left "bl"
     */
    public Motor backLeft;
    /**
     * Control 3: Parallel Encoder "pe"
     */
    public Deadwheel parallelDeadwheel;
    /**
     * Control 2: Perpendicular Encoder "ppe"
     */
    public Deadwheel perpendicularDeadwheel;
    /**
     * Internally mounted on I2C C0 "imu"
     */
    public IMU imu;

    /**
     * RoadRunner drive constants
     */
    public DriveConstants driveConstants;
    /**
     * Dual deadwheel intrinsics
     */
    public TwoWheelLocalizer.Coefficients localizerCoefficients;
    /**
     * Mecanum coefficients
     */
    public MecanumCoefficients mecanumCoefficients;

    @Override
    protected void onRuntime() {
        // Base is from GLaDOS
        imu = getHardware("imu", IMU.class, (d) -> {
            boolean init = d.initialize(new IMU.Parameters(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                    )
            ));
            if (!init) Dbg.error("imu failed init");
        });
        frontLeft = getHardware("fl", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.FORWARD));
        frontRight = getHardware("fr", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backRight = getHardware("br", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));
        backLeft = getHardware("bl", Motor.class, (d) -> d.setDirection(DcMotorSimple.Direction.REVERSE));

        // REV Through Bore Encoders
        parallelDeadwheel = getHardware("pe", Deadwheel.class,
                (d) -> d.setDirection(Deadwheel.Direction.FORWARD));
        perpendicularDeadwheel = getHardware("ppe", Deadwheel.class,
                (d) -> d.setDirection(Deadwheel.Direction.FORWARD));
        localizerCoefficients = new TwoWheelLocalizer.Coefficients.Builder()
                .setTicksPerRev(8192)
                .setGearRatio(1)
                .setWheelRadius(Millimeters.of(60).divide(2))
                // TODO: calibrate
//                .setXMultiplier(100.0 / 134.5)
//                .setYMultiplier(100.0 / 134.5)
                .setParallelX(Inches.zero())
                .setParallelY(Inches.of(-2))
                .setPerpendicularX(Inches.one())
                .setPerpendicularY(Inches.of(2.5))
                .setOverflowCompensation(true)
                .build();

        // RoadRunner configuration
        // TODO: not tuned for robot construction due to weight
        driveConstants = new DriveConstants.Builder()
                .setTicksPerRev(28)
                .setMaxRPM(458)
                .setRunUsingEncoder(false)
                .setWheelRadius(Millimeters.of(75).divide(2))
                .setGearRatio(1.0 / 13.1)
                .setTrackWidth(Inches.of(20.5))
                .setMaxVel(MetersPerSecond.of(1.04))
                .setMaxAccel(MetersPerSecondPerSecond.of(1.04))
                .setMaxAngVel(DegreesPerSecond.of(175))
                .setMaxAngAccel(DegreesPerSecond.per(Second).of(175))
                .setKV(0.01395)
                .setKStatic(0.06311)
                .setKA(0.0015)
                .build();
        mecanumCoefficients = new MecanumCoefficients.Builder()
                // TODO: calibrate
                .setLateralMultiplier(60.0 / 59.846666)
                .setTranslationalPID(new PIDCoefficients(8, 0, 0))
                .setHeadingPID(new PIDCoefficients(10, 0, 0))
                .build();
    }
}