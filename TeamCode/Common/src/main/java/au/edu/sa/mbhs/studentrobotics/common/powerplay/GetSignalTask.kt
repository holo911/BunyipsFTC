package au.edu.sa.mbhs.studentrobotics.common.powerplay

import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Direction
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.Vision
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.AprilTag
import com.qualcomm.robotcore.util.ElapsedTime

/**
 * Intermediate task for using AprilTag detections to detect a Signal position during an initLoop.
 * Updated 26/12/23 to use the new Vision class.
 * @author Lucas Bubner, 2022
 */
class GetSignalTask(private val vision: Vision) : Task() {
    private lateinit var at: AprilTag
    private val lockTimer = ElapsedTime()
//    private var noDetections = 0

    // Decimation is now handled internally by the SDK, so these values are not used.
//    private val decimationHigh = 3f
//    private val decimationLow = 2f
//    private val decimationHighMetersThreshold = 1.0f
//    private val decimationLowThreshold = 4

    /**
     * Get the saved position of where to park.
     * @return An enum of either LEFT, CENTER, or RIGHT determining where to park
     */
    @Volatile
    var position: Direction? = null
        private set

    override fun init() {
//        if (cam.mode != CamMode.OPENCV) cam.swapModes()

        // Tag size in metres
//        val tagsize = 0.166

        // Lens intrinsics calibrations, units in pixels
        // This is calibrated for the Logitech C920 camera, FTC season 2022-2023
//        val fx = 578.272
//        val fy = 578.272
//        val cx = 402.145
//        val cy = 221.506
//        at = AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy)
//        opMode.log("initialised AprilTagDetectionPipeline")
//        cam.setPipeline(at)

        if (!vision.isInitialised)
            vision.init(at)
        // Will assume AprilTag is attached if the VisionPortal is already initialised
        vision.start(at)
        named("Get Signal")
    }

    override fun isTaskFinished(): Boolean {
        if (position == null) {
            lockTimer.reset()
        }
        // Ensure the signal remains constant for 3 seconds before locking in
        return lockTimer.seconds() >= 3.0
    }

    override fun periodic() {
        // Caution! ParkingPosition will be null if the camera does not pick up anything in its task runtime.
        // Be sure to check if ParkingPosition is null before setting up your specific tasks, to handle a fallback value.
        var newPosition: Direction? = null
        val detections = at.data
        // Check if there are new frames
        // If there are, check if we see any tags
        if (detections.size > 0) {
            // If we do, set parking position based on this information and end the task
            // Will compare detected tags to the APRILTAG_ID array.
            for (detection in detections) {
                when (detection.id) {
                    17 -> {
                        newPosition = Direction.LEFT
                    }

                    13 -> {
                        newPosition = Direction.FORWARD
                    }

                    7 -> {
                        newPosition = Direction.RIGHT
                    }

                    else -> {
                        // Must be seeing a different tag, important we ignore it
                    }
                }
            }
        }
        if (position != newPosition) {
            lockTimer.reset()
        }
        position = newPosition
    }
}