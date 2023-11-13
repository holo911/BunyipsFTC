package org.firstinspires.ftc.teamcode.common.roadrunner.drive;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.common.roadrunner.trajectorysequence.TrajectorySequence;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for access methods in RoadRunner drive classes.
 *
 * @author Lucas Bubner, 2023
 */
public interface Drive {
    static TrajectoryVelocityConstraint getVelocityConstraint(double maxVel, double maxAngularVel, double trackWidth) {
        return new MinVelocityConstraint(Arrays.asList(
                new AngularVelocityConstraint(maxAngularVel),
                new MecanumVelocityConstraint(maxVel, trackWidth)
        ));
    }

    static TrajectoryAccelerationConstraint getAccelerationConstraint(double maxAccel) {
        return new ProfileAccelerationConstraint(maxAccel);
    }

    TrajectoryBuilder trajectoryBuilder(Pose2d startPose);

    TrajectoryBuilder trajectoryBuilder(Pose2d startPose, boolean reversed);

    TrajectoryBuilder trajectoryBuilder(Pose2d startPose, double startHeading);

    void turnAsync(double angle);

    void turn(double angle);

    void followTrajectoryAsync(Trajectory trajectory);

    void followTrajectory(Trajectory trajectory);

    void followTrajectorySequenceAsync(TrajectorySequence trajectorySequence);

    void followTrajectorySequence(TrajectorySequence trajectorySequence);

    Pose2d getLastError();

    void update();

    void waitForIdle();

    boolean isBusy();

    void setMode(DcMotor.RunMode runMode);

    void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior);

    void setPIDFCoefficients(DcMotor.RunMode runMode, PIDFCoefficients coefficients);

    void setWeightedDrivePower(Pose2d drivePower);

    List<Double> getWheelPositions();

    List<Double> getWheelVelocities();

    // Must be implemented manually due to different numbers of motors
    // void setMotorPowers(...);

    double getRawExternalHeading();

    Double getExternalHeadingVelocity();

    void setLocalizer(Localizer localizer);
}
