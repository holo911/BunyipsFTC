package org.murraybridgebunyips.bunyipslib.example.examplerobot.autonomous;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Millimeters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.drive.TankDrive;
import org.murraybridgebunyips.bunyipslib.example.examplerobot.components.ExampleConfig;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;

import java.util.Arrays;

/**
 * Example RoadRunner autonomous OpMode for a robot with a tank drive.
 */
public class ExampleRoadRunnerAutonomous extends AutonomousBunyipsOpMode implements RoadRunner {
    // This class is an extension of AutonomousBunyipsOpMode that allows integrated RoadRunner methods
    // Read ExampleAutonomous.java for more information on AutonomousBunyipsOpMode

    // Define configurations as normal
    private final ExampleConfig config = new ExampleConfig();
    private TankDrive drive;

    @Override
    protected void onInitialise() {
        config.init();
        // Configure your systems as normal...
        drive = new TankDrive(config.driveConstants, config.coefficients, config.imu, Arrays.asList(config.leftFrontMotor, config.leftBackMotor), Arrays.asList(config.rightBackMotor, config.rightFrontMotor));
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        // You have access to a range of RoadRunner methods here, primary one being makeTrajectory
//        makeTrajectory()
//                .lineToLinearHeading(...)
//                .splineTo(...)
//                .splineTo(...)
//                .splineTo(...)
//                .addTask();
        makeTrajectory(new Pose2d(0, 0, 0))
                .forward(1234, Units.Millimeters)
                .withPriority(PriorityLevel.FIRST)
                .addTask();
        makeTrajectory()
                .withPriority(PriorityLevel.LAST)
                .addTask();
        // These methods are syntactic sugar for the following:
//        addTask(new RoadRunnerTask(INFINITE_TIMEOUT, drive, drive.trajectoryBuilder().lineToLinearHeading(...).splineTo(...).splineTo(...).splineTo(...).build()));
        // where using FIRST priority will add the task to the front of the queue (addTaskFirst()), and
        // LAST priority will add the task to the back of the queue (addTaskLast())

        // It is recommended to use the syntactic sugar methods and this class with RoadRunner, as they are more readable and less error prone
        // See the definition of this interface to see all the methods available to you
    }

    // The major difference in this variant is that you delegate the drive class
    // to RoadRunner, so you don't need to manage the drive class yourself
    // This method mainly exists for the sake of avoiding headaches by forgetting to set the drive
    // instance, leading to runtime errors. This method will be called after onInitialise.
    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }
}
