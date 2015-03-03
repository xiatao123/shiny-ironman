package kamcord.stats;

import com.google.common.collect.Sets;
import kamcord.Key;

import java.util.Set;

/**
 * Created by taoxia on 3/3/15.
 */
public abstract class AbstractStats implements Stats {
    Key key;

    public AbstractStats(Key key) {
        this.key = key;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public int getInitialCount() {
        if(getInitialSet()==null){
            return 0;
        }
        return getInitialSet().size();
    }

    @Override
    public int getReopenCount() {
        if(getInitialSet()==null||getReopenSet()==null){
            return 0;
        }
        return Sets.intersection(getInitialSet(), getReopenSet()).size();
    }
}
