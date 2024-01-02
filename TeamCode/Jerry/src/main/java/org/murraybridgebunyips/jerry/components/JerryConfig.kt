package org.murraybridgebunyips.jerry.components

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.murraybridgebunyips.bunyipslib.RobotConfig
import kotlin.properties.Delegates

/**
 * Jerry robot configuration and hardware declarations.
 */
class JerryConfig : RobotConfig() {
    var monitorID by Delegates.notNull<Int>()
    var webcam: WebcamName? = null
    var bl: DcMotorEx? = null
    var br: DcMotorEx? = null
    var fl: DcMotorEx? = null
    var fr: DcMotorEx? = null
    var claw: Servo? = null
    var arm1: DcMotorEx? = null
    var arm2: DcMotorEx? = null
    var imu: IMU? = null
    var limit: TouchSensor? = null

//    // Encoder configuration
//    val xDiameter = 50.0
//    val xTicksPerRev = 2400.0
//
//    // The Y encoder is the same as the X encoder
//    val yDiameter = xDiameter
//    val yTicksPerRev = xTicksPerRev

    val driveMotors: List<DcMotorEx?>
        get() = listOf(bl, br, fl, fr)

    val armComponents: List<HardwareDevice?>
        get() = listOf(arm1, arm2, claw, limit)

    override fun configureHardware() {
        bl = getHardware("Back Left", DcMotorEx::class.java) as? DcMotorEx
        br = getHardware("Back Right", DcMotorEx::class.java) as? DcMotorEx
        fl = getHardware("Front Left", DcMotorEx::class.java) as? DcMotorEx
        fr = getHardware("Front Right", DcMotorEx::class.java) as? DcMotorEx
        arm1 = getHardware("Arm Motor 1", DcMotorEx::class.java) as? DcMotorEx
        arm2 = getHardware("Arm Motor 2", DcMotorEx::class.java) as? DcMotorEx
        claw = getHardware("Claw Servo", Servo::class.java) as? Servo
        limit = getHardware("Arm Stop", TouchSensor::class.java) as? TouchSensor
        imu = getHardware("ch_imu", IMU::class.java) as? IMU
        webcam = getHardware("Webcam", WebcamName::class.java) as? WebcamName

        monitorID = hardwareMap!!.appContext.resources.getIdentifier(
            "cameraMonitorViewId", "id", hardwareMap!!.appContext.packageName
        )

        // Dead wheels were moved to other robots for future seasons in pairment with RoadRunner
        // Jerry is now a simple mecanum drive robot, and still relies on the older but reliable
        // CartesianMecanumDrive systems and manual handwritten movement generation tasks.
        // As such, dead wheel code has been commented out throughout the Jerry codebase.

        // Motor direction configuration
        fl?.direction = Direction.REVERSE
        fr?.direction = Direction.FORWARD
        bl?.direction = Direction.FORWARD
        br?.direction = Direction.REVERSE
        claw?.direction = Servo.Direction.FORWARD
        arm1?.direction = Direction.FORWARD
        arm2?.direction = Direction.REVERSE

        // Control Hub IMU configuration
        val parameters = IMU.Parameters(
            RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
            )
        )
        imu?.initialize(parameters)
    }
}