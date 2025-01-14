package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.RoadRunnerDrive
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.tuning.RoadRunnerTuningOpMode
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

/**
 * For RoadRunner tuning.
 */
@TeleOp(name = "RoadRunner Tuning", group = "a")
class Tuning : RoadRunnerTuningOpMode() {
    override fun getDrive(): RoadRunnerDrive {
        val proto = Proto()
        proto.init(this)
        return proto.drive
    }
}
