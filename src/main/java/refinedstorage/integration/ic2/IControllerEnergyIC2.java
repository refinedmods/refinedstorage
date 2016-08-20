package refinedstorage.integration.ic2;

public interface IControllerEnergyIC2 {
    void invalidate();

    void update();

    void onChunkUnload();
}
