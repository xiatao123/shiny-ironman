package kamcord.stats;

import com.google.common.collect.Sets;
import kamcord.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by taoxia on 3/3/15.
 */
public class HighLevelStats extends AbstractStats {
    List<Stats> statsList;

    public HighLevelStats(Key key){
        super(key);
        statsList = new ArrayList<>();
    }

    public void addStats(Stats stats){
        statsList.add(stats);
    }

    @Override
    public Set<String> getInitialSet() {
        Set<String> results = null;
        for (Stats stats:statsList){
            if (results==null){
                results = stats.getInitialSet();
            }else {
                results = Sets.union(results, stats.getInitialSet());
            }
        }
        return results;
    }

    @Override
    public Set<String> getReopenSet() {
        Set<String> results = null;
        for (Stats stats:statsList){
            if (results==null){
                results = stats.getReopenSet();
            }else {
                if(stats.getReopenSet()!=null) {
                    results = Sets.union(results, stats.getReopenSet());
                }
            }
        }
        return results;
    }
}
