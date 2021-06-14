package net.chickensalad.survival.gui;

import net.chickensalad.survival.objects.WarpPad;
import org.bukkit.entity.Player;

public interface MenuFactory {

    WarpListGUI newListGui(WarpPad pad, Player player);
    WarpEditGUI newEditGui(WarpPad pad, Player player);
}
