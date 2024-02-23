package org.murraybridgebunyips.common.personalitycore;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.murraybridgebunyips.bunyipslib.BunyipsSubsystem;
import org.murraybridgebunyips.bunyipslib.DualServos;
import org.murraybridgebunyips.bunyipslib.NullSafety;
import org.murraybridgebunyips.common.personalitycore.submodules.PersonalityCoreClawMover;
import org.murraybridgebunyips.common.personalitycore.submodules.PersonalityCoreClawRotator;
import org.murraybridgebunyips.common.personalitycore.submodules.PersonalityCoreHook;
import org.murraybridgebunyips.common.personalitycore.submodules.PersonalityCoreManagementRail;

/**
 * Overhead class that handles a single instantiation of other PersonalityCore components.
 * Allows a one-level chain pattern to be used to call methods on the submodules.
 *
 * @author Lucas Bubner, 2023
 * @noinspection UnusedReturnValue
 */
//@Config
public class PersonalityCoreArm extends BunyipsSubsystem {
    // Servo values for the dual claw at the front
    public static double LEFT_CLAW_OPEN = 1.0;
    public static double RIGHT_CLAW_OPEN;
    public static double LEFT_CLAW_CLOSED;
    public static double RIGHT_CLAW_CLOSED = 1.0;
    private PersonalityCoreClawMover clawMover;
    private PersonalityCoreClawRotator clawRotator;
    private PersonalityCoreHook hook;
    private PersonalityCoreManagementRail managementRail;
    private DualServos claws;

    public PersonalityCoreArm(CRServo pixelMotion, Servo pixelAlignment, Servo suspenderHook, DcMotorEx suspenderActuator, Servo leftPixel, Servo rightPixel) {
        if (NullSafety.assertComponentArgs(PersonalityCoreClawMover.class, pixelMotion))
            clawMover = new PersonalityCoreClawMover(pixelMotion);
        if (NullSafety.assertComponentArgs(PersonalityCoreClawRotator.class, pixelAlignment))
            clawRotator = new PersonalityCoreClawRotator(pixelAlignment);
        if (NullSafety.assertComponentArgs(PersonalityCoreHook.class, suspenderHook))
            hook = new PersonalityCoreHook(suspenderHook);
        if (NullSafety.assertComponentArgs(PersonalityCoreManagementRail.class, suspenderActuator))
            managementRail = new PersonalityCoreManagementRail(suspenderActuator);
        if (NullSafety.assertComponentArgs(DualServos.class, leftPixel, rightPixel))
            claws = new DualServos(leftPixel, rightPixel, LEFT_CLAW_CLOSED, LEFT_CLAW_OPEN, RIGHT_CLAW_CLOSED, RIGHT_CLAW_OPEN);
    }

    /**
     * Directly access the claw mover submodule.
     *
     * @return PersonalityCoreClawMover
     */
    public PersonalityCoreClawMover getClawMover() {
        return clawMover;
    }

    /**
     * Actuate the horizontal claw mover using the controller.
     *
     * @param y gamepad.stick_y
     * @return PersonalityCoreClawMover
     */
    public PersonalityCoreClawMover actuateClawMoverUsingController(double y) {
        clawMover.actuateUsingController(y);
        return clawMover;
    }

    /**
     * Set the claw mover power directly.
     *
     * @param power power to set
     * @return PersonalityCoreClawMover
     */
    public PersonalityCoreClawMover setClawMoverPower(double power) {
        clawMover.setPower(power);
        return clawMover;
    }

    /**
     * Run the claw mover for a given number of seconds at a given power.
     * Requires constant update() calls to be made.
     *
     * @param seconds seconds to run for
     * @param power   power to run at
     * @return PersonalityCoreClawMover
     */
    public PersonalityCoreClawMover runClawMoverFor(double seconds, double power) {
        clawMover.runFor(seconds, power);
        return clawMover;
    }

    public PersonalityCoreClawMover actuateClawMoverUsingDpad(boolean up, boolean down) {
        clawMover.actuateUsingDpad(up, down);
        return clawMover;
    }

    /**
     * Directly access the claw rotator submodule.
     *
     * @return PersonalityCoreClawRotator
     */
    public PersonalityCoreClawRotator getClawRotator() {
        return clawRotator;
    }

    /**
     * Face the claw towards the 30 degree board.
     *
     * @return PersonalityCoreClawRotator
     */
    public PersonalityCoreClawRotator faceClawToBoard() {
        clawRotator.faceBoard();
        return clawRotator;
    }

    /**
     * Face the claw towards the ground.
     *
     * @return PersonalityCoreClawRotator
     */
    public PersonalityCoreClawRotator faceClawToGround() {
        clawRotator.faceGround();
        return clawRotator;
    }

    /**
     * Actuate the claw rotator (attached just above the claws) using the controller.
     *
     * @param y gamepad.stick_y
     * @return PersonalityCoreClawRotator
     */
    public PersonalityCoreClawRotator actuateClawRotatorUsingController(double y) {
        clawRotator.actuateUsingController(y);
        return clawRotator;
    }

    /**
     * Set the claw rotator position, 0==facing the ground, 1==facing the board.
     *
     * @param target power to set
     * @return PersonalityCoreClawRotator
     */
    public PersonalityCoreClawRotator setClawRotatorPosition(double target) {
        clawRotator.setPosition(target);
        return clawRotator;
    }

    /**
     * Set the claw rotator position, 0==facing the ground, 30==facing the board.
     *
     * @param degrees degrees to set
     * @return PersonalityCoreClawRotator
     */
    public PersonalityCoreClawRotator setClawRotatorDegrees(double degrees) {
        clawRotator.setDegrees(degrees);
        return clawRotator;
    }

    /**
     * Directly access the hook submodule.
     *
     * @return PersonalityCoreHook
     */
    public PersonalityCoreHook getHook() {
        return hook;
    }

    /**
     * Actuate the suspension hook using the controller. Will hold its position when the controller is released.
     *
     * @param y gamepad.stick_y
     * @return PersonalityCoreHook
     */
    public PersonalityCoreHook actuateHookUsingController(double y) {
        hook.actuateUsingController(y);
        return hook;
    }

    /**
     * Set the hook position directly, 0==stored, 1==deployed.
     *
     * @param y position to set
     * @return PersonalityCoreHook
     */
    public PersonalityCoreHook setHookPosition(double y) {
        hook.setPosition(y);
        return hook;
    }

    /**
     * Extend the hook to full rotation.
     *
     * @return PersonalityCoreHook
     */
    public PersonalityCoreHook extendHook() {
        hook.extend();
        return hook;
    }

    /**
     * Retract the hook to the safe position.
     *
     * @return PersonalityCoreHook
     */
    public PersonalityCoreHook retractHook() {
        hook.retract();
        return hook;
    }

    /**
     * Set the hook to the upright (slighly less than extended) position.
     *
     * @return PersonalityCoreHook
     */
    public PersonalityCoreHook uprightHook() {
        hook.upright();
        return hook;
    }

    /**
     * Directly access the management rail submodule.
     *
     * @return PersonalityCoreManagementRail
     */
    public PersonalityCoreManagementRail getManagementRail() {
        return managementRail;
    }

    /**
     * Actuate the management rail using the controller.
     *
     * @param y gamepad.stick_y
     * @return PersonalityCoreManagementRail
     */
    public PersonalityCoreManagementRail actuateManagementRailUsingController(double y) {
        managementRail.actuateUsingController(y);
        return managementRail;
    }

    /**
     * Set the management rail power directly.
     *
     * @param p power to set
     * @return PersonalityCoreManagementRail
     */
    public PersonalityCoreManagementRail setManagementRailPower(double p) {
        managementRail.setPower(p);
        return managementRail;
    }

    /**
     * Run the management rail for seconds.
     *
     * @param s seconds
     * @param p power
     */
    public PersonalityCoreManagementRail runManagementRailFor(double s, double p) {
        managementRail.runFor(s, p);
        return managementRail;
    }

    /**
     * Determine if the management rail is on an automatic runMangementRailFor
     *
     * @return boolean
     */
    public boolean isManagementRailBusy() {
        return managementRail.isBusy();
    }

    /**
     * Directly access the dual claw submodule.
     *
     * @return DualClaws
     */
    public DualServos getClaws() {
        return claws;
    }

    /**
     * Toggle the claw open/closed.
     *
     * @param side side to toggle
     * @return DualClaws
     */
    public DualServos toggleClaw(DualServos.ServoSide side) {
        claws.toggleServo(side);
        return claws;
    }

    /**
     * Open the claw.
     *
     * @param side side to open
     * @return DualClaws
     */
    public DualServos closeClaw(DualServos.ServoSide side) {
        claws.closeServo(side);
        return claws;
    }

    /**
     * Close the claw.
     *
     * @param side side to close
     * @return DualClaws
     */
    public DualServos openClaw(DualServos.ServoSide side) {
        claws.openServo(side);
        return claws;
    }

    /**
     * Send all stateful changes to all hardware and report telemetry.
     * No methods here will do anything unless this is called regularly.
     */
    public void update() {
        if (clawMover != null) clawMover.update();
        if (clawRotator != null) clawRotator.update();
        if (hook != null) hook.update();
        if (managementRail != null) managementRail.update();
        if (claws != null) claws.update();
    }
}
