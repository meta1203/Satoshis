package dogecoin.mc.dogecoins.database;

import dogecoin.mc.dogecoins.Dogecoins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SystemCheckThread extends Thread {

    private int waitTime;
    private boolean startCheck;

    public SystemCheckThread(int waitTime, boolean startCheck) {
        this.waitTime = waitTime * 3600000;
        this.startCheck = startCheck;
    }

    @Override
    public void run() {
        if (startCheck) {
            test(false);
        }
        if (waitTime <= 0) {
            return;
        }
        while (true) {
            try {
                this.wait(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            test(true);
        }
    }

    public void test(boolean loaded) {
        String msg = Dogecoins.scanner.getInfo();
        if (loaded) {
            broadcastToPerms(msg, "satoshis.info");
        }
        Dogecoins.log.warning(msg);
    }

    public void broadcastToPerms(String msg, String perm) {
        for (Player current : Bukkit.getServer().getOnlinePlayers()) {
            if (current.hasPermission(perm)) {
                current.sendMessage(msg);
            }
        }
    }

}
