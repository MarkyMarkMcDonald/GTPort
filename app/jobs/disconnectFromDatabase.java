package jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStop;
import services.DatabaseStatementExecutor;

/**
 * User: mark.mcdonald
 * Date: 12/4/12
 */
@OnApplicationStop
public class disconnectFromDatabase extends Job{
    public void doJob(){
        DatabaseStatementExecutor.disconnect();
    }
}
