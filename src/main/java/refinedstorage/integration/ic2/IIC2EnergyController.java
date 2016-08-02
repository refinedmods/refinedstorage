package refinedstorage.integration.ic2;

public interface IIC2EnergyController {
    void invalidate();

    void update();

    void onChunkUnload();
}
