package org.murraybridgebunyips.bunyipslib.personalitycore;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.murraybridgebunyips.bunyipslib.BunyipsComponent;
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.DualClaws;
import org.murraybridgebunyips.bunyipslib.NullSafety;
import org.murraybridgebunyips.bunyipslib.personalitycore.submodules.PersonalityCoreClawMover;
import org.murraybridgebunyips.bunyipslib.personalitycore.submodules.PersonalityCoreClawRotator;
import org.murraybridgebunyips.bunyipslib.personalitycore.submodules.PersonalityCoreHook;
import org.murraybridgebunyips.bunyipslib.personalitycore.submodules.PersonalityCoreManagementRail;

/**
 * Overhead class that handles a single instantiation of other PersonalityCore components.
 * @author Lucas Bubner, 2023
 */
@Config
public class PersonalityCoreArm extends BunyipsComponent {
    private PersonalityCoreClawMover clawMover;
    private PersonalityCoreClawRotator clawRotator;
    private PersonalityCoreHook hook;
    private PersonalityCoreManagementRail managementRail;
    private DualClaws claws;

    public static double LEFT_CLAW_OPEN = 0.0;
    public static double RIGHT_CLAW_OPEN = 0.0;
    public static double LEFT_CLAW_CLOSED = 1.0;
    public static double RIGHT_CLAW_CLOSED = 1.0;

    public PersonalityCoreArm(@NonNull BunyipsOpMode opMode, CRServo pixelMotion, Servo pixelAlignment, Servo suspenderHook, DcMotorEx suspenderActuator, Servo leftPixel, Servo rightPixel) {
        super(opMode);
        if (NullSafety.assertComponentArgs(getOpMode(), PersonalityCoreClawMover.class, pixelMotion))
            clawMover = new PersonalityCoreClawMover(getOpMode(), pixelMotion);
        if (NullSafety.assertComponentArgs(getOpMode(), PersonalityCoreClawRotator.class, pixelAlignment))
            clawRotator = new PersonalityCoreClawRotator(getOpMode(), pixelAlignment);
        if (NullSafety.assertComponentArgs(getOpMode(), PersonalityCoreHook.class, suspenderHook))
            hook = new PersonalityCoreHook(getOpMode(), suspenderHook);
        if (NullSafety.assertComponentArgs(getOpMode(), PersonalityCoreManagementRail.class, suspenderActuator))
            managementRail = new PersonalityCoreManagementRail(getOpMode(), suspenderActuator);
        if (NullSafety.assertComponentArgs(getOpMode(), DualClaws.class, leftPixel, rightPixel))
            claws = new DualClaws(getOpMode(), leftPixel, rightPixel, LEFT_CLAW_CLOSED, LEFT_CLAW_OPEN, RIGHT_CLAW_CLOSED, RIGHT_CLAW_OPEN);
    }

    public PersonalityCoreClawMover getClawMover() {
        return clawMover;
    }

    public void actuateClawMoverUsingController(double y) {
        clawMover.actuateUsingController(y);
    }

    public void setClawMoverPower(double power) {
        clawMover.setPower(power);
    }

    public void runClawMoverFor(double seconds, double power) {
        clawMover.runFor(seconds, power);
    }

    public PersonalityCoreClawRotator getClawRotator() {
        return clawRotator;
    }

    public void faceClawToBoard() {
        clawRotator.faceBoard();
    }

    public void faceClawToGround() {
        clawRotator.faceGround();
    }

    public void actuateClawRotatorUsingController(double y) {
        clawRotator.actuateUsingController(y);
    }

    public void setClawRotatorPosition(double target) {
        clawRotator.setPosition(target);
    }

    public void setClawRotatorDegrees(double degrees) {
        clawRotator.setDegrees(degrees);
    }

    public PersonalityCoreHook getHook() {
        return hook;
    }

    public void actuateHookUsingController(double y) {
        hook.actuateUsingController(y);
    }

    public void setHookPosition(double y) {
        hook.setPosition(y);
    }

    public void extendHook() {
        hook.extend();
    }

    public void retractHook() {
        hook.retract();
    }

    public void uprightHook() {
        hook.upright();
    }

    public PersonalityCoreManagementRail getManagementRail() {
        return managementRail;
    }

    public void actuateManagementRailUsingController(double y) {
        managementRail.actuateUsingController(y);
    }

    public void setManagementRailPower(double p) {
        managementRail.setPower(p);
    }

    public DualClaws getClaws() {
        return claws;
    }

    public void toggleServo(DualClaws.ServoSide side) {
        claws.toggleServo(side);
    }

    public void closeServo(DualClaws.ServoSide side) {
        claws.closeServo(side);
    }

    public void openServo(DualClaws.ServoSide side) {
        claws.openServo(side);
    }

    public void update() {
        if (clawMover != null) clawMover.update();
        if (clawRotator != null) clawRotator.update();
        if (hook != null) hook.update();
        if (managementRail != null) managementRail.update();
        if (claws != null) claws.update();
    }
}
