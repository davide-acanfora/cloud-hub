package grafana.dashboard;

import grafana.Configurable;
import grafana.datasource.DataSource;

public abstract class Dashboard implements Configurable{
	protected DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}
}
