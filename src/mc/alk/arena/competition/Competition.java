package mc.alk.arena.competition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import mc.alk.arena.controllers.MethodController;
import mc.alk.arena.events.BAEvent;
import mc.alk.arena.events.CompetitionEvent;
import mc.alk.arena.listeners.PlayerHolder;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.CompetitionState;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.arenas.ArenaListener;
import mc.alk.arena.objects.teams.ArenaTeam;
import mc.alk.arena.objects.teams.TeamHandler;

/**
 * Base class for Matches and Events
 * @author alkarin
 *
 */
public abstract class Competition implements PlayerHolder, TeamHandler {

	/** Our teams */
	protected final List<ArenaTeam> teams = new CopyOnWriteArrayList<ArenaTeam>();

	/** Players that have left the match */
	protected final Set<String> leftPlayers = Collections.synchronizedSet(new HashSet<String>());


	/** a list of team indexes.  Teams can come and go, so the List of teams isnt always reflective
	 * of the real index.*/
	protected final Map<ArenaTeam,Integer> teamIndexes = new ConcurrentHashMap<ArenaTeam,Integer>();

	/** Which team indexes are currently used */
	protected final Set<Integer> usedIndexes = new HashSet<Integer>();

	static int count =0;

	final protected int id = count++;

	/** Our Method Controller that will handle anyone listening to this competition*/
	protected final MethodController methodController = new MethodController("id ="+id);

	/**
	 * Get the time of when the competition did the given state
	 * @return time or null if not found
	 */
	public abstract Long getTime(CompetitionState state);

	/**
	 * Get the unique ID for this competition
	 * @return
	 */
	public int getID(){
		return id;
	}

	/**
	 * Get the Name for this competition
	 * @return
	 */
	public abstract String getName();

	/**
	 * Returns the current state of the competition
	 * @return
	 */
	public abstract CompetitionState getState();

	/**
	 * Transition from one state to another
	 * onStart -> onVictory
	 * @param state
	 */
	protected abstract void transitionTo(CompetitionState state);

	/**
	 * Signify that a player has left the competition
	 * @param player
	 * @return
	 */
	public boolean playerLeft(ArenaPlayer player) {
		return leftPlayers.contains(player.getName());
	}

	/**
	 * Returns either the MatchParams or EventParams of the match/event
	 * @return
	 */
	public abstract MatchParams getParams();

	/**
	 * Add a team to this competition
	 * @param team
	 * @return true if the team was added, false if not
	 */
	public abstract boolean addTeam(ArenaTeam team);

	/**
	 * Remove the team from the competition
	 * @param team
	 * @return whether or not the team was removed
	 */
	public abstract boolean removeTeam(ArenaTeam team);

	/**
	 * Signify that the set of players were added to the team
	 * @param t
	 * @param players
	 */
	public abstract void addedToTeam(ArenaTeam team, Collection<ArenaPlayer> players);

	/**
	 * Signify that the set of players were added to the team
	 * @param t
	 * @param inArena
	 * @return true if the player could be added to the team, false otherwise
	 */
	public abstract boolean addedToTeam(ArenaTeam team, ArenaPlayer player);

	/**
	 * Signify that the set of players were removed from the team
	 * @param t
	 * @param players
	 */
	public abstract void removedFromTeam(ArenaTeam team, Collection<ArenaPlayer> players);

	/**
	 * Signify that the set of players were added to the team
	 * @param t
	 * @param inArena
	 */
	public abstract void removedFromTeam(ArenaTeam team, ArenaPlayer player);

	/**
	 * Set our teams
	 * @param teams
	 */
	public void setTeams(List<ArenaTeam> teams){
		this.teams.clear();
		this.teams.addAll(teams);
		this.teamIndexes.clear();
		this.usedIndexes.clear();
		for (int i=0;i<teams.size();i++){
			teamIndexes.put(teams.get(i), i);
			usedIndexes.add(i);
		}
	}

	/**
	 * return the teams for this competition
	 * @return
	 */
	public List<ArenaTeam> getTeams() {
		return teams;
	}

	/**
	 * Notify Bukkit Listeners and specific listeners to this match
	 * @param BAevent event
	 */
	public void callEvent(BAEvent event) {
		if (event instanceof CompetitionEvent && ((CompetitionEvent)event).getCompetition()==null){
			((CompetitionEvent)event).setCompetition(this);}
//		event.callEvent(); /// Call anyone using generic bukkit listeners
		methodController.callEvent(event);
	}

	/**
	 * Add a collection of listeners for this competition
	 * @param transitionListeners
	 */
	public void addArenaListeners(Collection<ArenaListener> transitionListeners){
		for (ArenaListener tl: transitionListeners){
			addArenaListener(tl);}
	}

	/**
	 * Add an arena listener for this competition
	 * @param arenaListener
	 */
	public void addArenaListener(ArenaListener arenaListener){
		methodController.addListener(arenaListener);
	}

	/**
	 * Remove an arena listener for this competition
	 * @param arenaListener
	 */
	public boolean removeArenaListener(ArenaListener arenaListener){
		return methodController.removeListener(arenaListener);
	}

	/**
	 * Get the team that this player is inside of
	 * @param player
	 * @return ArenaPlayer, or null if no team contains this player
	 */
	public ArenaTeam getTeam(ArenaPlayer player) {
		for (ArenaTeam t: teams) {
			if (t.hasMember(player)) return t;}
		return null;
	}

	/**
	 * Get the team with this index
	 * @param teamIndex
	 * @return ArenaPlayer, or null if no team exists
	 */
	public ArenaTeam getTeam(int teamIndex) {
		return teams.size() <= teamIndex? null : teams.get(teamIndex);
	}

	/**
	 * Is the player inside of this competition?
	 * @param player to check for
	 * @return true or false
	 */
	public boolean hasPlayer(ArenaPlayer player) {
		for (ArenaTeam t: teams) {
			if (t.hasMember(player)) return true;}
		return false;
	}

	/**
	 * Is the player alive and inside of this competition?
	 * @param player to check for
	 * @return true or false
	 */
	public boolean hasAlivePlayer(ArenaPlayer player) {
		for (ArenaTeam t: teams) {
			if (t.hasAliveMember(player)) return true;}
		return false;
	}

	/**
	 * Get the players that are currently inside of this competition
	 * @return Set of ArenaPlayers
	 */
	public Set<ArenaPlayer> getPlayers() {
		HashSet<ArenaPlayer> players = new HashSet<ArenaPlayer>();
		for (ArenaTeam t: teams){
			players.addAll(t.getPlayers());}
		return players;
	}
}
