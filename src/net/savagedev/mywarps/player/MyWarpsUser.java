package net.savagedev.mywarps.player;

import net.savagedev.mywarps.commands.state.CommandState;
import net.savagedev.mywarps.commands.state.ConfirmationManager;
import net.savagedev.mywarps.utils.CaseInsensitiveString;

import java.util.List;

public class MyWarpsUser {
    private ConfirmationManager confirmationManager;
    private List<CaseInsensitiveString> ownedWarps;

    MyWarpsUser(List<CaseInsensitiveString> ownedWarps) {
        this.confirmationManager = new ConfirmationManager(CommandState.READY);
        this.ownedWarps = ownedWarps;
    }

    ConfirmationManager getConfirmationManager() {
        return this.confirmationManager;
    }

    public List<CaseInsensitiveString> getOwnedWarps() {
        return this.ownedWarps;
    }
}
