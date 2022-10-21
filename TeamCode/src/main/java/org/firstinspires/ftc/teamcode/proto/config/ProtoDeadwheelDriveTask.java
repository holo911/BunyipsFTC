package org.firstinspires.ftc.teamcode.proto.config;

import org.firstinspires.ftc.teamcode.common.BunyipsOpMode;
import org.firstinspires.ftc.teamcode.common.tasks.BaseTask;
import org.firstinspires.ftc.teamcode.common.tasks.Task;

public class ProtoDeadwheelDriveTask extends BaseTask implements Task {

    private final ProtoDrive drive;
    private final Deadwheel x, y;
    private final double px_mm, py_mm, xspeed, yspeed;

    public ProtoDeadwheelDriveTask(BunyipsOpMode opMode, double time, LisaDrive drive, Deadwheel x, Deadwheel y, double px_mm, double py_mm, double xspeed, double yspeed) {
        super(opMode, time);
        this.drive = drive;
        this.x = x;
        this.y = y;
        this.px_mm = px_mm;
        this.py_mm = py_mm;
        this.xspeed = xspeed;
        this.yspeed = yspeed;
    }

    @Override
    public void init() {
        super.init();
        x.enableTracking();
        y.enableTracking();
    }

    @Override
    public boolean isFinished() {
        return super.isFinished() || (x.targetPositionReached() && y.targetPositionReached());
    }

    @Override
    public void run() {
        if (isFinished()) {
            x.disableTracking();
            y.disableTracking();
            drive.deinit();
            return;
        }
        if (x.getTravelledMM() >= px_mm) {
            xspeed = 0.0;
        }
        if (y.getTravelledMM() >= py_mm) {
            yspeed = 0.0;
        }
        drive.setSpeedXYR(xspeed, yspeed, 0);
        drive.update();
    }
}