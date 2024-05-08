package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// Annotations in Java are preceeded by an @ symbol.
// They are meant to provide metadata on a class or method (depending on what they appear above).
//
// In FTC programming, the @TeleOp annotation is a necessity. It communicates to the REV Control Hub:
//    - What name to display for the Java file in the Driver Station application (name field)
//    - What group to put the file into, for organization purposes, in the Driver Station application (group field)
// Most of the time, the only field you need is the name field. The group field is utilized here for further organization.
@TeleOp(name = "Field Centric TeleOp w/ Encoders", group = "Driver Control Period")
public class FieldCentricTeleOpEncoders extends OpMode {

    // DcMotorEx is the type we use for any motor hardware.
    DcMotorEx frontLeft, frontRight, backLeft, backRight;

    // From Wikipedia: An inertial measurement unit (IMU) is an electronic device that measures and reports a body's specific force, angular rate, and sometimes the orientation of the body.
    // In our case, we need the built in IMU in the REV Control Hubs to field centric control.
    // The type is simply what kind of IMU is installed in the REV Control Hub. Most of the time, it's BHI260IMU.
    BHI260IMU imu;

    // A timer, which begins from program start. Displayed to Driver Station application.
    private ElapsedTime runtime = new ElapsedTime();

    // The init function is a necesity of any TeleOp class. 
    // It runs once at start.
    public void init() {
        imu = hardwareMap.get(BHI260IMU.class, "imu");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        // left motors are reversed.
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        frontRight.setDirection(DcMotorEx.Direction.FORWARD);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.FORWARD);


        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);


        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        IMU.Parameters parameters = new IMU.Parameters(
            new RevHubOrientationOnRobot(
                    // Default was left, down
                    RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                    RevHubOrientationOnRobot.UsbFacingDirection.UP)
                ); // these should always be configured to your REV control hub

        imu.initialize(parameters);
        //imu.resetYaw();
    }

    boolean slowMode = false;

    // The loop function is a necessity of any TeleOp class. 
    // It repeatedly runs any code placed inside it, continously until the program has been halted.
    //
    // Keen-eyed programmers may also have noticed the Java annotation: @Override.
    // It overrides the function with the same name in a superclass.
    @Override
    public void loop() {
double y = -gamepad1.left_stick_y; // make sure this is negated
        double x = gamepad1.left_stick_x;
        double r = gamepad1.right_stick_x;
        
        // modifier for mecanum wheel speed
        double multiplier;

        if (slowMode) {
            multiplier = 2000;
        }
        else {
            multiplier = 3000;
        }

        // to be honest, i have no idea why we do this. might be to initialize field centric
        // make it so it's not easy to accidentally hit the button
        // think of this like the xbox start button
        // can be changed if needed
        // used to be options button
        if (gamepad1.y) {
            imu.resetYaw();
        }

        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        
        // rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // counteract imperfect strafing BUT removes a bit of percision

        // denom is largest motor power (abs value) or 1
        // ensures all powers maintain the same ratio
        // ONLY IF at least one is out of range [-1, 1]
        double denom = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(r), 1);

        double FL = ((rotY + rotX + r) ) / denom;
        double BL = ((rotY - rotX + r) ) / denom;
        double FR = ((rotY - rotX - r) ) / denom;
        double BR = ((rotY + rotX - r) ) / denom;

        // turn slowmode on
        if (gamepad1.dpad_up) {
            slowMode = true;
        }
        // turn slowmode off
        else if (gamepad1.dpad_down){
            slowMode = false;
        }


        // ------------- Slides -------------
        // TODO: Add preset heights for the backboard. Link these with a button.
        current = leftBall.getCurrentPosition();

        if (gamepad2.right_trigger > 0) {
            target -= 5;
        }
        else if (gamepad2.left_trigger > 0) {
            target += 5;
        }

        leftBall.setTargetPosition(target);
        leftBall.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        rightBall.setTargetPosition(target);
        rightBall.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        if (target > current) {
            leftBall.setPower(0.5);
            rightBall.setPower(0.5);
        }
        else if (target < current) {
            leftBall.setPower(-0.5);
            rightBall.setPower(-0.5);
        }
        else {
            leftBall.setPower(0);
            rightBall.setPower(0);
        }
        // ---------------------------------------


        // ------------- Drone Launcher -------------
        if (gamepad2.y) {
            drone.setPosition(1.0);
        }
        else {
            drone.setPosition(0);
        }
        // ---------------------------------------


        // ------------- Claw Servo -------------
        /*if (gamepad2.right_stick_y > -1 && gamepad2.right_stick_y < 1) {
            if (rotateClaw < 1 || rotateClaw > -1) {
                rotateClaw += gamepad2.right_stick_y;
            }
            c1.setPosition(rotateClaw);
            c2.setPosition(rotateClaw);
        }

        if (gamepad2.right_bumper) {
            cClaw.setPosition(claw);
            claw = 0.0;
        }
        else {
            cClaw.setPosition(claw);
            claw = 1.0;
        }*/
        // ---------------------------------------


        frontLeft.setVelocity(FL*multiplier);
        backLeft.setVelocity(BL*multiplier);
        frontRight.setVelocity(FR*multiplier);
        backRight.setVelocity(BR*multiplier);


        // ------------- Console Data -------------
        telemetry.addData("Status", "Run Time: ", runtime.toString());
        telemetry.addData("--------- Mecanum ---------", null);
        telemetry.addData("Slowmode: ", slowMode);
        telemetry.addData("Front Left", FL);
        telemetry.addData("Front Right", FR);
        telemetry.addData("Back Left", BL);
        telemetry.addData("Back Right", BR);
        telemetry.addData("--------- Slides ---------", null);
        telemetry.addData("Target Position (Slides): ", target);
        telemetry.addData("Current Position (Slides): ", current);
        telemetry.addData("------------- Claw -------------", null);
        telemetry.addData("Servo Position: ", rotateClaw);
        telemetry.addData("Open (1.0) - Closed (0.0): ", claw);
    }
}