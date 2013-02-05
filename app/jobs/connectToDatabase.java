package jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import services.DatabaseStatementExecutor;

/**
 * User: mark.mcdonald
 * Date: 12/4/12
 */
@OnApplicationStart
public class connectToDatabase extends Job{
    public void doJob(){
        DatabaseStatementExecutor.connect();
    }
}
