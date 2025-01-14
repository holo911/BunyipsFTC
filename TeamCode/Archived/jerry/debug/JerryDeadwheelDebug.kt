package org.firstinspires.ftc.teamcode.jerry.debug

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.BunyipsOpMode
import org.firstinspires.ftc.teamcode.common.EncoderTracker
import org.firstinspires.ftc.teamcode.common.NullSafety
import org.firstinspires.ftc.teamcode.common.Odometer
import org.firstinspires.ftc.teamcode.common.RobotConfig
import org.firstinspires.ftc.teamcode.jerry.components.JerryConfig

/**
 * Debug opmode for deadwheel readouts.
 */
@TeleOp(name = "Deadwheel Debug")
class JerryDeadwheelDebug : BunyipsOpMode() {
    private var config = JerryConfig()
    private var x: Odometer? = null
    private var y: Odometer? = null

    override fun onInit() {
        config = RobotConfig.newConfig(this, config, hardwareMap) as JerryConfig
        if (NullSafety.assertNotNull(config.fl))
            x = Odometer(this, config.fl!!, config.xDiameter, config.xTicksPerRev)

        if (NullSafety.assertNotNull(config.fr))
            y = Odometer(this, config.fr!!, config.yDiameter, config.yTicksPerRev)

        x?.track()
        y?.track()
    }

    override fun activeLoop() {
        telemetry.add("X Encoder: ${x?.position(EncoderTracker.Scope.GLOBAL)}g, ${x?.position()}r")
        telemetry.add("Y Encoder: ${y?.position(EncoderTracker.Scope.GLOBAL)}g, ${y?.position()}r")
        telemetry.add("X MM: ${x?.travelledMM(EncoderTracker.Scope.GLOBAL)}g, ${x?.travelledMM()}r")
        telemetry.add("Y MM: ${y?.travelledMM(EncoderTracker.Scope.GLOBAL)}g, ${y?.travelledMM()}r")
    }
}