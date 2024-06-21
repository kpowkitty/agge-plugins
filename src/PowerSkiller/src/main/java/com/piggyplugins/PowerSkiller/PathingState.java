public class PathingState implements State {
    private final stack stack;
    private final Context ctx;


    public PathingState(StateStack stack, Context ctx) {
        this.stack = stack;
        this.ctx = ctx;
        Pathing pathing = new Pathing();
    }

    @Override
    public void update() {
        // Implement update logic
    }

    @Override
    public void draw() {
        // Implement draw logic
    }

    @Override
    public void handleEvent(Object event) {
        // Implement event handling logic
    }

    private boolean setPath(WorldPoint wp)
    {
        return pathing.pathTo(wp);
    }

                if (shouldBank() && cantBank()) {
                    try {
                        goal = BankLocation.fromString(config.setBank());
                        log.info("Valid bank WorldPoint");
                        pathing.pathTo(goal);
                        log.info("Found a path! Pathing...");
                    } catch (IllegalArgumentException e) {
                        log.info(e.getMessage());
                    }
                } else {
                    try {
                        // User can provide a skilling location, optional 
                        // WorldPoint poll in logs.
                        goal = new WorldPoint(config.skillingX(), 
                                              config.skillingY(), 
                                              config.skillingZ());
                        log.info("Valid skilling WorldPoint");
                        pathing.pathTo(goal);
                        log.info("Found a path! Pathing...");
                    } catch (IllegalArgumentException e) {
                        log.info(e.getMessage());
                    }
                }
            break;

    /**
     * Finalizer procedure for PathingState. Make sure to call!
     * @remark PathingState DOES have a finalizer procedure (null the Pathing 
     * reference).
     */
    private void finalizer()
    {
        pathing = null;
    }
}

      pathing.run();

        // If a pathing command is executed, then block all states until
        // finished pathing. Is also done this way because having a pathing 
        // state introduces stuttering pathing due to scanning states.
        if (!pathing.isPathing()) {
            state = getNextState();
            handleState();
        }
