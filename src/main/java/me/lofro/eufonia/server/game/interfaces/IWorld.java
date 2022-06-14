package me.lofro.eufonia.server.game.interfaces;

public interface IWorld {
    static boolean vanishEnabled(String worldName) {
        return switch (worldName) {
            case "saw_forest", "saw_underground", "psicodelia_void_train" -> true;
            default -> false;
        };
    }
    boolean vanishEnabled();

    static boolean advOnlySeesSrv(String worldName) {
        return "saw_underground".equals(worldName);
    }
    boolean advOnlySeesSrv();
}
