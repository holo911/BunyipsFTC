package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.WaitTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup;

/**
 * Transfers a grabbed sample from the claw to the vertical lift.
 *
 * @author Lachlan Paul, 2024
 */
public class TransferSample extends SequentialTaskGroup {
    /**
     * Sequence of events:<br>
     * 1. Close the claw rotator<br>
     * 2. Home the vertical arm<br>
     * 3. Close the basket rotator<br>
     * 4. Open the claw rotator<br>
     * 5. Move the horizontal arm slightly to avoid a conflict<br>
     * 6. Wait for 0.6 seconds<br>
     * 7. Open the claws<br>
     * 8. Wait for 0.3 seconds<br>
     * 9. Move the horizontal arm to 270 degrees and the vertical arm to 200 degrees, and set the claw rotator to 0.5<br>
     * 10. Close the claw rotator<br>
     * 11. Close the claws
     * @param verticalArm   the vertical arm
     * @param horizontalArm the horizontal arm
     * @param clawRotator   the claw rotator
     * @param basketRotator the basket that the sample is placed in
     * @param claws         the claws
     */
    public TransferSample(HoldableActuator verticalArm, HoldableActuator horizontalArm, Switch clawRotator, Switch basketRotator, DualServos claws) {
        super(
                clawRotator.tasks.close(),
                verticalArm.tasks.home(),
                basketRotator.tasks.close(),
                clawRotator.tasks.open(),
                horizontalArm.tasks.goTo(240).withTimeout(Seconds.of(2)),
                new WaitTask(0.6, Seconds),
                claws.tasks.openBoth(),
                new WaitTask(0.3, Seconds),
                new ParallelTaskGroup(
                        horizontalArm.tasks.goTo(270)
                                .withTimeout(Seconds.of(0.5))
                                .then(horizontalArm.tasks.home()),
                        verticalArm.tasks.goTo(200)
                                .withTimeout(Seconds.of(2)),
                        clawRotator.tasks.setClipped(0.5)
                ),
                clawRotator.tasks.close(),
                claws.tasks.closeBoth()
        );
    }
}
