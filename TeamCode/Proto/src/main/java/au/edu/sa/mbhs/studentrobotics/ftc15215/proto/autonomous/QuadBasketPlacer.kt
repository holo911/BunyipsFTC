package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.Mathf.degToRad
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueLeft
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redLeft
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.IdentityPoseMap
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

/**
 * 0+4 Autonomous for Neutral Samples and Preload.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "0+4 Quad Basket Placer, Left")
class QuadBasketPlacer : AutonomousBunyipsOpMode() {
    private val robot = Proto()

    override fun onInitialise() {
        robot.init()

        setOpModes(
            blueLeft().tile(2.0).backward(5 of Inches).rotate(90 of Degrees),
            redLeft().tile(2.0).backward(5 of Inches).rotate(90 of Degrees)
        )

        robot.clawLift.withTolerance(20, true)

        // TODO: localizer self test for auto? or just check manually
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.require() as StartingConfiguration.Position
        robot.drive.pose = startLocation.toFieldPose()

        val waypoints = listOf(
            Pose2d(37.0, 41.0, 310.0.degToRad()),
            Pose2d(53.64, 42.77, (-70.0).degToRad()),
            Pose2d(58.7, 41.85, (-55.0).degToRad()),
        )

        // Reset position of claw rotator to be upright, as it might be slanted backwards for preloading
        add(robot.clawRotator.tasks.open())
        // Navigate to the basket and lift up the vertical lift at the same time
        add(
            ParallelTaskGroup(
                robot.drive.makeTrajectory(if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap())
                    .strafeToLinearHeading(Vector2d(56.0, 56.0), heading = PI / 4)
                    .build(),
                robot.clawLift.tasks.goTo(750) timeout (3 of Seconds)
            )
        )
        // Angle claw rotator down and drop sample
        add(robot.clawRotator.tasks.setTo(0.2).forAtLeast(0.5, Seconds))
        add(robot.claws.tasks.openBoth().forAtLeast(0.1, Seconds))
        add(robot.clawRotator.tasks.open().forAtLeast(0.5, Seconds))

        for (i in waypoints.indices) {
            // Begin moving to the sample
            add(
                ParallelTaskGroup(
                    Task.defer {
                        robot.drive.makeTrajectory(if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap())
                            .strafeToLinearHeading(waypoints[i].position, heading = waypoints[i].heading)
                            .build()
                    },
                    robot.clawRotator.tasks.setTo(0.6).after(0.5, Seconds),
                    robot.clawLift.tasks.home(),
                )
            )
            // Yoink
            add(robot.clawRotator.tasks.close().forAtLeast(0.25, Seconds))
            add(robot.claws.tasks.closeBoth().forAtLeast(0.1, Seconds))
            add(robot.clawRotator.tasks.open())
            // Go back and place
            add(
                ParallelTaskGroup(
                    Task.defer {
                        val t =
                            robot.drive.makeTrajectory(if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap())
                        if (i < 1) {
                            t.strafeToLinearHeading(Vector2d(56.0, 56.0), heading = PI / 4)
                        } else {
                            t.setReversed(true)
                                .splineTo(Vector2d(50.0, 48.0), tangent = 125.0.degToRad())
                                .splineToLinearHeading(Vector2d(56.0, 56.0), heading = PI / 4, tangent = PI / 4)
                        }
                        t.build()
                    },
                    robot.clawLift.tasks.goTo(750) timeout (3 of Seconds)
                )
            )
            add(robot.clawRotator.tasks.setTo(0.2).forAtLeast(0.5, Seconds))
            add(robot.claws.tasks.openBoth().forAtLeast(0.1, Seconds))
            add(robot.clawRotator.tasks.open().forAtLeast(0.5, Seconds))
        }

//        add(
//            robot.drive.makeTrajectory(Pose2d(56.0, 56.0, PI / 4))
//            .turnTo(225.0, Degrees)
//            .splineTo(Vector2d(24.26, 11.25), Inches, 180.00, Degrees)
//            .build()
//            .with(
//                robot.clawLift.tasks.goTo(250) timeout (3 of Seconds),
//                robot.clawRotator.tasks.setTo(0.1)
//            )
//        )
    }
}