package frc.subsystems;

enum Goal { // The integer numbers to represent different control panel functions
    NONE, TOCOLOR, INTROTATIONS;
}

// Sample "Goals" as in things for the robot to achieve
// The computer sets a goal to go to a color, or go a number of rotations,
// respectively.
public class GoalFlow { // Use the goals to do things with the robot here
    private Goal currentGoal;
    private Object[] args;

    public GoalFlow() {
        currentGoal = Goal.NONE;
    }

    public void setGoal(Goal goal, Object... argv) {
        this.args = argv;
        this.currentGoal = goal;
    } // Setter for goals

    public Goal getGoal() {
        return this.currentGoal;
    } // Get the current goal

    public void endGoal() {
        this.currentGoal = Goal.NONE;
    } // Detach the goal

    public void runGoal(Object... args) {

    } // Run code for goals
}