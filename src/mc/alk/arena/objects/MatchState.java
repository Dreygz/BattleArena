package mc.alk.arena.objects;

/**
 * @author alkarin
 *
 * Enum of MatchTransitions, and MatchStates
 */
public enum MatchState implements CompetitionState{
	NONE("None"), DEFAULTS("defaults"),
	ONENTER("onEnter"), ONLEAVE("onLeave"), //ONENTERWAITROOM("onEnterWaitRoom"),
	ONENTERARENA("onEnterArena"), ONLEAVEARENA("onLeaveArena"), //ONENTERWAITROOM("onEnterWaitRoom"),
	INQUEUE("inQueue"),INCOURTYARD("inCourtyard"),
	INLOBBY("inLobby"), INWAITROOM("inWaitroom"),INARENA("inArena"),INSPECTATE("inSpectate"),
	PREREQS ("preReqs"), ONJOIN ("onJoin"), ONOPEN("onOpen"),
	ONBEGIN("onBegin"), ONPRESTART ("onPreStart"), ONSTART ("onStart"), ONVICTORY ("onVictory"),
	ONCOMPLETE ("onComplete"), ONCANCEL ("onCancel"), ONFINISH("onFinish"),
	ONSPAWN ("onSpawn"), ONDEATH ("onDeath"),
	WINNERS ("winners"), DRAWERS ("drawers"), LOSERS ("losers"),
	FIRSTPLACE ("firstPlace"), PARTICIPANTS("participants"),
	ONMATCHINTERVAL("onMatchInterval"), ONMATCHTIMEEXPIRED("onMatchTimeExpired"),
	ONCOUNTDOWNTOEVENT("onCountdownToEvent"),
	ONENTERQUEUE("onEnterQueue")
	;

	String name;
	MatchState(String name){
		this.name = name;
	}
	@Override
	public String toString(){
		return name;
	}

	public static MatchState fromString(String str){
		str = str.toUpperCase();
		try{
			return MatchState.valueOf(str);
		} catch (Exception e){
			if (str.equals("ONCOUNTDOWNTOEVENT")) return ONCOUNTDOWNTOEVENT;
			else if (str.equals("WINNER")) return WINNERS;
			return null;
		}
	}

	public boolean isRunning() {
		return this == MatchState.ONSTART;
	}
}
