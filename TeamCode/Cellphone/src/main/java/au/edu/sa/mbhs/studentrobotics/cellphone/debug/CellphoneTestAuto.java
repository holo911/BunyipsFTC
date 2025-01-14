package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Centimeters;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redLeft;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redRight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.WaitTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;
import au.edu.sa.mbhs.studentrobotics.cellphone.components.CellphoneConfig;

/**
 * Test batch of 100 WaitTasks.
 */
@Autonomous
public class CellphoneTestAuto extends AutonomousBunyipsOpMode {
    private final CellphoneConfig config = new CellphoneConfig();

    @Override
    protected void onInitialise() {
        config.init();
//        setOpModes("Test Auto", "Test Auto 2", "3,", "4", "5", "6", "7", "8", "9", "10", "18", "19", "20");
        setOpModes(
                redRight()
                        .tile(2.5),
                redLeft()
                        .translate(Centimeters.of(15)),
                blueRight().tile(4)
                        .backward(Centimeters.of(10))
                        .rotate(Degrees.of(45))
        );
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        assert selectedOpMode != null;
        telemetry.log(((StartingConfiguration.Position) selectedOpMode.require()).toFieldPose());
        telemetry.log(config.dummyMotor.getController());
        for (int i = 0; i < 100; i++) {
            add(new WaitTask(Milliseconds.of(100)).named("WaitTask no. " + i));
        }
        add(new ParallelTaskGroup(
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000)),
                new WaitTask(Milliseconds.of(20000))
        ));
    }
}
