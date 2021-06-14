package net.chickensalad.survival.objects;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public final class SurvivalPlayer {

    protected UUID uniqueId;
    private String name;

    private Date firstJoin;
    private Date lastJoin;

    private int kills;
    private int deaths;

    private transient String lastMessaged;
    private boolean receivedStarterKit;

    SurvivalPlayer() {
    }

    public SurvivalPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.firstJoin = new Date();
    }
}
