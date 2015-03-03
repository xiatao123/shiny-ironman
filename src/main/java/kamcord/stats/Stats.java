package kamcord.stats;

import kamcord.Key;

import java.util.Set;

/**
 * Created by taoxia on 3/3/15.
 */
public interface Stats {
    public int getInitialCount();
    public int getReopenCount();
    public Set<String> getInitialSet();
    public Set<String> getReopenSet();
    public Key getKey();
}
