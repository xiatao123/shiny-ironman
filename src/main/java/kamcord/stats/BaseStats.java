package kamcord.stats;

import com.google.common.collect.Sets;
import kamcord.Key;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by taoxia on 3/3/15.
 */
public class BaseStats extends AbstractStats{
    Set<String> initialIds;
    Set<String> reopenIds;

    public BaseStats(Key key){
        super(key);
    }

    public void insertInitialId(String id){
        if (initialIds==null){
            initialIds = new HashSet<>();
        }
        initialIds.add(id);
    }

    public void setReopenIds(Set<String> reopenIds){
        this.reopenIds = reopenIds;
    }

    @Override
    public Set<String> getInitialSet() {
        return initialIds;
    }

    @Override
    public Set<String> getReopenSet() {
        return reopenIds;
    }
}
