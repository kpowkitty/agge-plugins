protected int tickDelay() {
    return MathUtil.random(config.tickDelayMin(), config.tickDelayMax());
}
