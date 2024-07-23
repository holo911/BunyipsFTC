package org.murraybridgebunyips.glados.autonomous.l4;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Direction;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.GetTriPositionContourTask;
import org.murraybridgebunyips.bunyipslib.tasks.groups.ParallelTaskGroup;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.bunyipslib.vision.processors.ColourThreshold;
import org.murraybridgebunyips.common.centerstage.vision.BlueTeamProp;
import org.murraybridgebunyips.common.centerstage.vision.RedTeamProp;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Place a purple pixel loaded on the left side of the arm onto the scanned Spike Mark and remain in place.
 */
@Config
@Autonomous(name = "Spike Mark Placer (Purple on Left, No Park)", group = "L4")
public class GLaDOSSpikeMarkPlacerAutonomous extends AutonomousBunyipsOpMode implements RoadRunner {
    /** extension/retraction ticks */
    public static int ARM_DELTA = 2000;
    /** angled spike mark, move forward initially, field tiles */
    public static double ANGLED_INITIAL_FORWARD_DIST_FT = 0.8;
    /** forward spike mark, move forward initially, field tiles */
    public static double M_FORWARD_INITIAL_FORWARD_DIST_FT = 0.7;
    /** forward spike mark, forward centimeters */
    public static double M_FORWARD_DIST_CM = 20;
    /** left spike mark, degrees turn */
    public static double M_LEFT_TURN_DEG = 40;
    /** right spike mark, degrees turn */
    public static double M_RIGHT_TURN_DEG = -40;

    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private DualDeadwheelMecanumDrive drive;
    private HoldableActuator arm;
    private DualServos claws;
    private Vision vision;
    private ColourThreshold teamProp;
    private GetTriPositionContourTask getTeamProp;
    private StartingPositions startingPosition;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(config.driveConstants, config.mecanumCoefficients, hardwareMap.voltageSensor, config.imu, config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel);
        arm = new HoldableActuator(config.arm).withMovingPower(0.5);
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 0.0, 1.0);
        vision = new Vision(config.webcam);

        getTeamProp = new GetTriPositionContourTask();
        setOpModes(StartingPositions.use());
        setInitTask(getTeamProp);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null) return;
        startingPosition = (StartingPositions) selectedOpMode.require();
        teamProp = startingPosition.isRed() ? new RedTeamProp() : new BlueTeamProp();

        vision.init(teamProp);
        vision.start(teamProp);
        getTeamProp.setProcessor(teamProp);
    }

    @Override
    protected void onStart() {
        Direction spikeMark = getTeamProp.getPosition();
        vision.stop(teamProp);

        addTask(new ParallelTaskGroup(
            arm.deltaTask(ARM_DELTA).withName("Extend Arm"),
            makeTrajectory()
                    .forward(spikeMark == Direction.FORWARD ? M_FORWARD_INITIAL_FORWARD_DIST_FT : ANGLED_INITIAL_FORWARD_DIST_FT, FieldTile)
                    .withName("Move Forward to Spike Marks")
                    .buildTask()
        ).withName("Move to Spike Marks"));

        switch (spikeMark) {
            case FORWARD:
                makeTrajectory()
                        .forward(M_FORWARD_DIST_CM, Centimeters)
                        .withName("Align to Center Mark")
                        .addTask();
                break;
            case LEFT:
                makeTrajectory()
                        .turn(M_LEFT_TURN_DEG, Degrees)
                        .withName("Rotate to Left Mark")
                        .addTask();
                break;
            case RIGHT:
                makeTrajectory()
                        .turn(M_RIGHT_TURN_DEG, Degrees)
                        .withName("Rotate to Right Mark")
                        .addTask();
                break;
        }

        addTask(claws.openTask(DualServos.ServoSide.LEFT).withName("Open Left Claw"));
        addTask(arm.deltaTask(-ARM_DELTA).withName("Retract Arm"));
    }

    @Override
    protected void onFinish() {
        vision.terminate();
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }
}
